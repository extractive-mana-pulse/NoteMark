package com.example.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class NotesResponse(
    val notes: List<NoteDTO>,
    val totalCount: Int? = null,
    val currentPage: Int? = null,
    val totalPages: Int? = null
)