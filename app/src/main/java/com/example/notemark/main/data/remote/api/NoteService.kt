package com.example.notemark.main.data.remote.api

import com.example.notemark.main.data.remote.NoteDTO
import com.example.notemark.main.domain.model.CreateNoteRequest
import com.example.notemark.main.domain.model.NotesResponse

interface NoteService {

    suspend fun getNotes(
        page: Int,
        size: Int,
    ): NotesResponse

    suspend fun createNote(
        body: CreateNoteRequest
    ): Result<NoteDTO>

    suspend fun deleteNote(
        id: String
    ): Result<Unit>
}