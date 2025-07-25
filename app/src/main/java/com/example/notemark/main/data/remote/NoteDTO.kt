package com.example.notemark.main.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NoteDTO(
    val id: String,
    val title: String,
    val content: String,
    val createdAt: String,
    @SerialName("lastEditedAt")
    val updatedAt: String? = ""
)