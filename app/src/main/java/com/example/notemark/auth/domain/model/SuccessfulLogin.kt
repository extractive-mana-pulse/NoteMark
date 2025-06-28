package com.example.notemark.auth.domain.model

data class SuccessfulLogin(
    val accessToken: String,
    val refreshToken: String
)