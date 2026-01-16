package com.example.domain.model

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val username: String,
)