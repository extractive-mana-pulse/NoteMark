package com.example.domain

interface NoteService {

    suspend fun getNotes(
        page: Int,
        size: Int,
    ): NotesResponse

    suspend fun createNote(
        body: NoteRequest
    ): Result<Note, RootError>

    suspend fun updateNote(
        body: NoteRequest
    ): Result<Note, RootError>

    suspend fun deleteNote(
        id: String
    ): Result<Unit, RootError>
}
