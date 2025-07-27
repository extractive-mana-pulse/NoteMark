package com.example.notemark.main.data.remote.api

import com.example.notemark.main.data.remote.NoteDTO
import com.example.notemark.main.domain.model.NoteRequest
import com.example.notemark.main.domain.model.NotesResponse

interface NoteService {

    suspend fun getNotes(
        page: Int,
        size: Int,
    ): NotesResponse

    suspend fun createNote(
        body: NoteRequest
    ): Result<NoteDTO>

    suspend fun updateNote(
        body: NoteRequest
    ): Result<NoteDTO>

    suspend fun deleteNote(
        id: String
    ): Result<Unit>
}