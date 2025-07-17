package com.example.notemark.main.data.remote.api

import com.example.notemark.auth.domain.DataError
import com.example.notemark.main.domain.model.CreateNoteRequest
import com.example.notemark.main.domain.model.Note
import com.example.notemark.main.domain.model.NotesResponse

interface NoteService {

    suspend fun getNotes(
        page: Int,
        size: Int,
    ): Result<NotesResponse>

    suspend fun createNote(
        body: CreateNoteRequest
    ): Result<Note>

    suspend fun deleteNote(
        id: String
    ): Result<Unit>
}