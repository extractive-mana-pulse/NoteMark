package com.example.presentation

import com.example.domain.Note

data class NotesUiState(
    val notes: List<Note> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 0,
    val hasNextPage: Boolean = true
)