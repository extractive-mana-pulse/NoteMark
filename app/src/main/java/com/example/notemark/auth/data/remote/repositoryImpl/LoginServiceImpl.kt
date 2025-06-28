package com.example.notemark.auth.data.remote.repositoryImpl

import com.example.notemark.auth.data.remote.HttpRoutes
import com.example.notemark.auth.data.remote.api.LoginService
import com.example.notemark.auth.domain.model.LoginModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class LoginServiceImpl(
    private val client: HttpClient
): LoginService {

    override suspend fun login(body: LoginModel): LoginModel? {
        return try {
            val response = client.post(
                urlString = HttpRoutes.LOGIN
            ) {
                contentType(ContentType.Application.Json)
                setBody(body)
            }

            if (response.status.isSuccess()) {
                response.body<LoginModel>()
            } else {
                println("Login failed with status: ${response.status}")
                null
            }
        } catch (e: Exception) {
            println("Login error: ${e.message}")
            null
        }
    }
}