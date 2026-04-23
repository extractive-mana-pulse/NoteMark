package com.example.domain.model

data class SuccessfulLogin(
    val accessToken: String,
    val refreshToken: String
)