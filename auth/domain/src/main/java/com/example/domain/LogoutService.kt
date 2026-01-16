package com.example.domain

import com.example.domain.model.AccessTokenResponse

interface LogoutService {

    suspend fun logout(refreshToken: String): AccessTokenResponse?

}