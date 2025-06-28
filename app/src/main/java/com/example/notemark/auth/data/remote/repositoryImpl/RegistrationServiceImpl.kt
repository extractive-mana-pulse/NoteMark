package com.example.notemark.auth.data.remote.repositoryImpl

import com.example.notemark.auth.data.remote.HttpRoutes
import com.example.notemark.auth.data.remote.api.RegistrationService
import com.example.notemark.auth.domain.model.RegistrationModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class RegistrationServiceImpl(
    private val client: HttpClient
): RegistrationService {

    override suspend fun register(body: RegistrationModel): RegistrationModel? {
        return try {
            val response = client.post(
                urlString = HttpRoutes.REGISTER
            ) {
                contentType(ContentType.Application.Json)
                setBody(body)
            }

            if (response.status.isSuccess()) {
                response.body<RegistrationModel>()
            } else {
                println("Registration failed with status: ${response.status}")
                null
            }
        } catch (e: Exception) {
            println("Registration error: ${e.message}")
            null
        }
    }
}