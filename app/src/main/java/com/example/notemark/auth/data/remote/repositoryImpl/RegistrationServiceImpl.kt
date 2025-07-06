package com.example.notemark.auth.data.remote.repositoryImpl

import com.example.notemark.core.HttpRoutes
import com.example.notemark.auth.data.remote.api.RegistrationService
import com.example.notemark.auth.domain.model.RegistrationModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
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
                header("X-User-Email", HttpRoutes.EMAIL)
                setBody(body)
            }

            if (response.status.isSuccess()) {
                // Since registration succeeded (HTTP 200), return success data
                // even if response body parsing fails
                try {
                    response.body<RegistrationModel>()
                } catch (parseException: Exception) {
                    println("Response parsing failed but registration succeeded: ${parseException.message}")
                    // Return the registration data (without password for security)
                    RegistrationModel(
                        username = body.username,
                        email = body.email,
                        password = ""
                    )
                }
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