package com.example.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class NoteRequestDTO(
    val id: String? = null,
    val title: String,
    val content: String,
    val createdAt: String,
    val updatedAt: String
)