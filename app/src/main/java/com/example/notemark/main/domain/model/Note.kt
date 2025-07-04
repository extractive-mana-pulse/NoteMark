package com.example.notemark.main.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Note(
    val id: String,
    val title: String,
    val content: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
