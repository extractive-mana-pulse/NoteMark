package com.example.domain

data class NotesResponse(
    val notes: List<Note>,
    val totalPages: Int? = null,
    val currentPage: Int? = null,
    val totalNotes: Int? = null
)