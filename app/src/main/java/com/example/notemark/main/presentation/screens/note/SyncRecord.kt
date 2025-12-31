package com.example.notemark.main.presentation.screens.note

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class SyncRecord(
    @PrimaryKey
    val id: UUID,
    val userId: String,
    val noteId: String,
    val operation: SyncOperation,
    val payload: String,
    val timeStamp: String
)

enum class SyncOperation {
    CREATE,
    UPDATE,
    DELETE
}