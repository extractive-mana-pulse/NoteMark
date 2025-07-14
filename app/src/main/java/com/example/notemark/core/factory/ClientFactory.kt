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
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject

class HttpClientFactory @Inject constructor(
    private val sessionManager: SessionManager
) {
    private val refreshClient = HttpClient(CIO) {
        install(Logging) {
            level = LogLevel.ALL

        }
        install(ContentNegotiation) {
            json(
                json = Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }
        defaultRequest {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${sessionManager.getAccessToken()}")
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
                        Log.d("TokenRefresh", "Attempting refresh with token: ${refreshToken?.take(10)}...")

                        if (refreshToken.isNullOrEmpty()) {
                            Log.w("TokenRefresh", "No refresh token available, clearing tokens")
                            sessionManager.clearTokens()
                            return@refreshTokens BearerTokens(accessToken = "", refreshToken = "")
                        }

                        try {
                            val response = refreshClient.post(
                                urlString = HttpRoutes.REFRESH_TOKEN
                            ) {
                                setBody(AccessTokenRequest(refreshToken = refreshToken))
                                contentType(ContentType.Application.Json)
                            }

                            if (response.status.isSuccess()) {
                                val tokenResponse = response.body<AccessTokenResponse>()
                                Log.d("TokenRefresh", "Token refresh successful")
                                sessionManager.saveTokens(tokenResponse.accessToken, tokenResponse.refreshToken)
                                BearerTokens(
                                    accessToken = tokenResponse.accessToken,
                                    refreshToken = tokenResponse.refreshToken
                                )
                            } else {
                                Log.e("TokenRefresh", "Token refresh failed with status: ${response.status}")
                                sessionManager.clearTokens()
                                BearerTokens(accessToken = "", refreshToken = "")
                            }
                        } catch (e: Exception) {
                            Log.e("TokenRefresh", "Token refresh exception: ${e.message}")
                            sessionManager.clearTokens()
                            BearerTokens(accessToken = "", refreshToken = "")
                        }
                    }
                }
            }
        }
    }
}

@Serializable
data class AccessTokenRequest(
    val refreshToken: String
)

@Serializable
data class AccessTokenResponse(
    val accessToken: String,
    val refreshToken: String,
)