package com.example.data.remote

import com.example.data.local.NoteDatabase
import com.example.domain.ConnectivityObserver
import com.example.domain.HttpRoutes
import com.example.domain.Note
import com.example.domain.NoteRequest
import com.example.domain.NoteService
import com.example.domain.Result
import com.example.domain.RootError
import com.example.domain.SessionRepository
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
import java.util.UUID

class NoteServiceImpl(
    private val client: HttpClient,
    private val sessionManager: SessionRepository,
    private val noteDatabase: NoteDatabase,
    private val connectivityObserver: ConnectivityObserver,
): NoteService {

    override suspend fun getNotes(page: Int, size: Int): com.example.domain.NotesResponse {
        return try {
            val accessToken = sessionManager.getAccessToken()

            val response = client.get(HttpRoutes.NOTES) {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $accessToken")
                header("X-User-Email", HttpRoutes.EMAIL)
            }
            response.body<com.example.domain.NotesResponse>()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun createNote(body: NoteRequest): Result<Note, RootError> {
        return try {
            // 1. Always save locally first (offline-first principle)
            val noteEntity = body.toDTO()
            val username = sessionManager.getUserName()
            val userId = UUID.nameUUIDFromBytes(username?.toByteArray())
            val isOnline = connectivityObserver.getCurrentNetworkState()

            // 3. Try to sync immediately if online
            if (isOnline) {
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

                    // 5. Update local record with server response if needed
                    val serverNote = response.body<NoteDTO>()
                    noteDatabase.dao.upsertNote(serverNote.toEntity())

                    Result.Success(serverNote.toDomain())
                } catch (e: Exception) {
                    // Network failed, but local save succeeded
                    // SyncRecord remains in queue for background sync
                    Result.Success(noteEntity.toNote()) // Return local version
                }
            } else {
                // Offline - return local result
                Result.Success(noteEntity.toNote())
            }

        } catch (e: Exception) {
            Result.Error(e as RootError)
        }
    }


    override suspend fun updateNote(body: NoteRequest): Result<Note, RootError> {
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
            Result.Success(response.body<NoteDTO>().toDomain())
        } catch (e: Exception) {
            Result.Error(e as RootError)
        }
    }

    override suspend fun deleteNote(id: String): Result<Unit, RootError> {
        return try {
            noteDatabase.dao.deleteNoteById(id)
            val response = client.delete(
                urlString = "${HttpRoutes.NOTES}/$id"
            ) {
                header(HttpHeaders.Accept, "application/json")
                header("X-User-Email", HttpRoutes.EMAIL)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e as RootError)
        }
    }
}