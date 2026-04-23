package com.example.data.remote

import com.example.domain.HttpRoutes
import com.example.domain.LoginService
import com.example.domain.model.LoginModel
import com.example.domain.model.LoginResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class LoginServiceImpl(
    private val client: HttpClient
): LoginService {

    override suspend fun login(body: LoginModel): LoginResponse? {
        return try {
            val response = client.post(
                urlString = HttpRoutes.LOGIN
            ) {
                contentType(ContentType.Application.Json)
                header("X-User-Email", HttpRoutes.EMAIL)
                setBody(body)
            }

            if (response.status.isSuccess()) {
                response.body<LoginResponse>()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}