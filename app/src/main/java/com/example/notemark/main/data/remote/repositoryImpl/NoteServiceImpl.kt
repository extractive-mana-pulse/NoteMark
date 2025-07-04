package com.example.notemark.main.data.remote.repositoryImpl

import com.example.notemark.core.HttpRoutes
import com.example.notemark.main.data.remote.api.NoteService
import com.example.notemark.main.domain.model.NotesResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.contentType

class NoteServiceImpl(
    private val client: HttpClient
): NoteService {

    override suspend fun getNotes(page: Int, size: Int): Result<NotesResponse> {
        return try {
            val response = client.get(
                HttpRoutes.GET_NOTES
            ) {
                parameter("page", page)
                parameter("size", size)
                contentType(ContentType.Application.Json)
                header("X-User-Email", HttpRoutes.EMAIL)
            }
            Result.success(response.body<NotesResponse>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}