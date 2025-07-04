package com.example.notemark.auth.data.remote.api

import com.example.notemark.core.HttpRoutes
import com.example.notemark.core.factory.AccessTokenResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class LogoutServiceImpl(
    private val client: HttpClient
): LogoutService {
    override suspend fun logout(): AccessTokenResponse? {
        return try {
            val response = client.post(
                urlString = HttpRoutes.LOGOUT
            ){
                contentType(ContentType.Application.Json)
                header("X-User-Email", HttpRoutes.EMAIL)
            }
            if (response.status.isSuccess()) {
                response.body<AccessTokenResponse>()
            } else {
                null
            }
        } catch (e: Exception){
            null
        }
    }
}