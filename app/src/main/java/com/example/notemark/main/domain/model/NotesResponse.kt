package com.example.notemark.main.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class NotesResponse(
    val notes: List<Note>,
    val totalCount: Int? = null,
    val currentPage: Int? = null,
    val totalPages: Int? = null
)