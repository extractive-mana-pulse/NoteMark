package com.example.presentation

import com.example.domain.Note

fun Note.getFormattedCreatedAt(): String = createdAt.formatAsNoteDate()
fun Note.getFormattedUpdatedAt(): String = updatedAt?.formatAsDetailsDate() ?: ""