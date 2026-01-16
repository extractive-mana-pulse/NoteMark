package com.example.domain

interface LocalNoteDataSource {
    suspend fun clearAllNotes()
    suspend fun getNotesCount(): Int
}