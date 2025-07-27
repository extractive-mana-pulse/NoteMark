package com.example.notemark.auth.data.remote.api

import com.example.notemark.core.factory.AccessTokenResponse

interface LogoutService {

    suspend fun logout(refreshToken: String): AccessTokenResponse?

}