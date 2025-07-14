package com.example.notemark.main.domain.model

import com.example.notemark.main.formatAsNoteDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Note(
    val id: String = "",
    val title: String,
    val content: String,
    val createdAt: String,
    @SerialName("lastEditedAt")
    val updatedAt: String? = ""
)

fun Note.getFormattedCreatedAt(): String = createdAt.formatAsNoteDate()
fun Note.getFormattedUpdatedAt(): String = updatedAt?.formatAsNoteDate() ?: ""