package com.example.domain.model

data class AccessTokenResponse(
    val accessToken: String,
    val refreshToken: String,
)