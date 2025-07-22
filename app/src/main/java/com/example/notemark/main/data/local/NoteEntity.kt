package com.example.notemark.main.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.notemark.main.formatAsNoteDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class NoteEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val content: String,
    val createdAt: String,
    @SerialName("lastEditedAt")
    val updatedAt: String? = ""
)

fun NoteEntity.getFormattedCreatedAt(): String = createdAt.formatAsNoteDate()
fun NoteEntity.getFormattedUpdatedAt(): String = updatedAt?.formatAsNoteDate() ?: ""