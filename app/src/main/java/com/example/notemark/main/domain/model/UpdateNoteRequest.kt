package com.example.notemark.main.domain.model

import com.example.notemark.main.DateFormatter
import kotlinx.serialization.Serializable

@Serializable
data class UpdateNoteRequest(
    val title: String,
    val content: String,
    val updatedAt: String = DateFormatter.getCurrentIsoString()
)
