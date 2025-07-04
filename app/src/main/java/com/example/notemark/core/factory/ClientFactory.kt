package com.example.notemark.core.factory

import android.util.Log
import com.example.notemark.core.HttpRoutes
import com.example.notemark.core.manager.SessionManager
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Inject

class HttpClientFactory @Inject constructor(
    private val sessionManager: SessionManager
) {
    private val refreshClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }
        defaultRequest {
            contentType(ContentType.Application.Json)
            header("X-User-Email", HttpRoutes.EMAIL)
        }
    }

    fun build(): HttpClient {
        return HttpClient(CIO) {
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    }
                )
            }

            defaultRequest {
                contentType(ContentType.Application.Json)
                header("X-User-Email", HttpRoutes.EMAIL)
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        val accessToken = sessionManager.getAccessToken() ?: ""
                        val refreshToken = sessionManager.getRefreshToken() ?: ""

                        BearerTokens(
                            accessToken = accessToken,
                            refreshToken = refreshToken
                        )
                    }

                    refreshTokens {
                        val refreshToken = sessionManager.getRefreshToken()

                        val response = refreshClient.post<AccessTokenRequest, AccessTokenResponse>(
                            route = HttpRoutes.REFRESH_TOKEN,
                            body = AccessTokenRequest(refreshToken = refreshToken ?: "")
                        )

                        when (response) {
                            is Result.Success -> {
                                sessionManager.saveTokens(
                                    accessToken = response.data.accessToken,
                                    refreshToken = response.data.refreshToken
                                )

                                BearerTokens(
                                    accessToken = response.data.accessToken,
                                    refreshToken = response.data.refreshToken
                                )
                            }
                            is Result.Error -> {
                                // Only clear tokens if it's an authentication error (401/403)
                                // Not for network errors, server errors, etc.
                                // initially here was just sessionManager.clearTokens()
                                // and it automatically activates clearing token function
                                // when any type of error occur during the app in process.
                                if (response.error.contains("401") || response.error.contains("403")) {
                                    sessionManager.clearTokens()
                                } else {
                                    Log.d("HttpClient", "Refresh token failed but not auth error: ${response.error}")
                                }

                                BearerTokens(accessToken = "", refreshToken = "")
                            }
                        }
                    }
                }
            }
        }
    }
}

data class AccessTokenRequest(
    val refreshToken: String
)

data class AccessTokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val username: String
)

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val error: String) : Result<Nothing>()
}

suspend inline fun <reified T, reified R> HttpClient.post(
    route: String,
    body: T
): Result<R> {
    return try {
        val response = post(route) {
            setBody(body)
        }

        if (response.status.isSuccess()) {
            val responseBody = response.body<R>()
            Result.Success(responseBody)
        } else {
            Result.Error("HTTP ${response.status.value}: ${response.status.description}")
        }
    } catch (e: Exception) {
        Result.Error(e.message ?: "Unknown error occurred")
    }
}