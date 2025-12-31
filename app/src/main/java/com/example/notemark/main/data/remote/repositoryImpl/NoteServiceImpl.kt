package com.example.notemark.main.data.remote.repositoryImpl

import com.example.notemark.ConnectivityObserver
import com.example.notemark.ConnectivityViewModel
import com.example.notemark.core.HttpRoutes
import com.example.notemark.core.manager.SessionManager
import com.example.notemark.main.data.local.NoteDatabase
import com.example.notemark.main.data.local.SyncDao
import com.example.notemark.main.data.remote.NoteDTO
import com.example.notemark.main.data.remote.api.NoteService
import com.example.notemark.main.data.remote.toNotEntity
import com.example.notemark.main.data.remote.toNoteDTO
import com.example.notemark.main.data.remote.toNoteEntity
import com.example.notemark.main.domain.model.NoteRequest
import com.example.notemark.main.domain.model.NotesResponse
import com.example.notemark.main.presentation.screens.note.SyncOperation
import com.example.notemark.main.presentation.screens.note.SyncRecord
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import java.util.UUID

class NoteServiceImpl(
    private val client: HttpClient,
    private val sessionManager: SessionManager,
    private val noteDatabase: NoteDatabase,
    private val connectivityObserver: ConnectivityObserver,
): NoteService {

    override suspend fun getNotes(page: Int, size: Int): NotesResponse {
        return try {
            val accessToken = sessionManager.getAccessToken()

            val response = client.get(HttpRoutes.NOTES) {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $accessToken")
                header("X-User-Email", HttpRoutes.EMAIL)
            }
            response.body<NotesResponse>()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun createNote(body: NoteRequest): Result<NoteDTO> {
        return try {
            // 1. Always save locally first (offline-first principle)
            val noteEntity = body.toNotEntity()
            val localResult = noteDatabase.dao.upsertNote(note = noteEntity)
            val username = sessionManager.getUserName()
            val userId = UUID.nameUUIDFromBytes(username?.toByteArray())
            val isOnline = connectivityObserver.getCurrentNetworkState()
            // 2. Add to sync queue for later synchronization
            val syncRecord = SyncRecord(
                id = UUID.randomUUID(),
                userId = userId.toString(), // Internal user ID
                noteId = noteEntity.id, // Assuming noteEntity has an ID
                operation = SyncOperation.CREATE,
                payload = Json.encodeToString(body), // JSON representation
                timeStamp = System.currentTimeMillis().toString()
            )
            noteDatabase.syncDao.insertSyncRecord(syncRecord)

            // 3. Try to sync immediately if online
            return if (isOnline) {
                try {
                    val accessToken = sessionManager.getAccessToken()
                    val response = client.post(
                        urlString = HttpRoutes.NOTES
                    ) {
                        contentType(ContentType.Application.Json)
                        setBody(body)
                        header("Authorization", "Bearer $accessToken")
                        header("X-User-Email", HttpRoutes.EMAIL)
                    }

                    // 4. If sync successful, remove from sync queue
                    noteDatabase.syncDao.deleteSyncRecord(syncRecord.noteId)

                    // 5. Update local record with server response if needed
                    val serverNote = response.body<NoteDTO>()
                    noteDatabase.dao.upsertNote(serverNote.toNoteEntity())

                    Result.success(serverNote)
                } catch (e: Exception) {
                    // Network failed, but local save succeeded
                    // SyncRecord remains in queue for background sync
                    Result.success(noteEntity.toNoteDTO()) // Return local version
                }
            } else {
                // Offline - return local result
                Result.success(noteEntity.toNoteDTO())
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateNote(body: NoteRequest): Result<NoteDTO> {
        return try {
            val accessToken = sessionManager.getAccessToken()
            val response = client.put(
                urlString = HttpRoutes.NOTES
            ) {
                contentType(ContentType.Application.Json)
                setBody(body)
                header("Authorization", "Bearer $accessToken")
                header("X-User-Email", HttpRoutes.EMAIL)
            }
            Result.success(response.body<NoteDTO>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteNote(id: String): Result<Unit> {
        noteDatabase.dao.deleteNoteById(id)
        return try {
            val response = client.delete(
                urlString = "${HttpRoutes.NOTES}/$id"
            ) {
                header(HttpHeaders.Accept, "application/json")
                header("X-User-Email", HttpRoutes.EMAIL)
            }
            Result.success(response.body())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}