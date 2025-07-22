package com.example.notemark.main.domain.model

import com.example.notemark.main.data.remote.NoteDTO
import kotlinx.serialization.Serializable

@Serializable
data class NotesResponse(
    val notes: List<NoteDTO>,
    val totalCount: Int? = null,
    val currentPage: Int? = null,
    val totalPages: Int? = null
)