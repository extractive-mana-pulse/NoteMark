package com.example.notemark.main.domain.repository

import com.example.notemark.main.data.remote.NoteDTO
import com.example.notemark.main.data.remote.repositoryImpl.NoteServiceImpl
import com.example.notemark.main.domain.model.NoteRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NotesRepository(
    private val api: NoteServiceImpl
) {

    fun createNote(
        body: NoteRequest
    ) : Flow<Result<NoteDTO>> = flow {
        emit(api.createNote(body))
    }

    fun updateNote(
        body: NoteRequest
    ) : Flow<Result<NoteDTO>> = flow {
        emit(api.updateNote(body))
    }

    suspend fun deleteNote(noteId: String): Result<Unit> {
        return api.deleteNote(noteId)
    }
}