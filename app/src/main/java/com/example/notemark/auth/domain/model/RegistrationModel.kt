package com.example.notemark.auth.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class RegistrationModel(
    val username: String,
    val email: String,
    val password: String,
)