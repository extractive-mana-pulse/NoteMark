package com.example.notemark.main.data.remote

import com.example.notemark.main.formatAsNoteDate
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

fun NoteDTO.getFormattedCreatedAt(): String = createdAt.formatAsNoteDate()
fun NoteDTO.getFormattedUpdatedAt(): String = updatedAt?.formatAsNoteDate() ?: ""