package com.example.notemark.main.domain.repository

import com.example.notemark.main.data.local.NoteEntity
import com.example.notemark.main.data.remote.NoteDTO
import com.example.notemark.main.data.remote.repositoryImpl.NoteServiceImpl
import com.example.notemark.main.domain.model.CreateNoteRequest
import com.example.notemark.main.domain.model.Note
import com.example.notemark.main.domain.model.NotesResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NotesRepository(
    private val api: NoteServiceImpl
) {

    suspend fun getNotes(): NotesResponse = api.getNotes(
        page = 1,
        size = 10
    )

    fun createNote(
        body: CreateNoteRequest
    ) : Flow<Result<NoteDTO>> = flow {
        emit(api.createNote(body))
    }

    suspend fun deleteNote(noteId: String): Result<Unit> {
        return api.deleteNote(noteId)
    }
}