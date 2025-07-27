package com.example.notemark.main.domain.model

import com.example.notemark.main.DateFormatter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NoteRequest(
    val id: String,
    val title: String,
    val content: String,
    val createdAt: String = DateFormatter.getCurrentIsoString(),
    @SerialName("lastEditedAt")
    val updatedAt: String? = DateFormatter.getCurrentIsoString()
)