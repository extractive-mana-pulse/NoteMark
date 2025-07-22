package com.example.notemark.main.data.remote.repositoryImpl

import com.example.notemark.core.HttpRoutes
import com.example.notemark.core.manager.SessionManager
import com.example.notemark.main.data.remote.NoteDTO
import com.example.notemark.main.data.remote.api.NoteService
import com.example.notemark.main.domain.model.CreateNoteRequest
import com.example.notemark.main.domain.model.NotesResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

class NoteServiceImpl(
    private val client: HttpClient,
    private val sessionManager: SessionManager
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

    override suspend fun createNote(body: CreateNoteRequest): Result<NoteDTO> {
        return try {
            val accessToken = sessionManager.getAccessToken()
            val response = client.post(
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
        return try {
            val response = client.delete(
                urlString = "${HttpRoutes.NOTES}/$id"
            ) {
                header(HttpHeaders.Accept, "application/json")
                header("X-User-Email", HttpRoutes.EMAIL)
            }
            println("Response status: ${response.status}")
            println("Response headers: ${response.headers}")
            println("Content-Type: ${response.headers[HttpHeaders.ContentType]}")

            Result.success(response.body())

        } catch (e: Exception) {
            println("Delete note error: ${e.message}")
            Result.failure(e)
        }
    }
}