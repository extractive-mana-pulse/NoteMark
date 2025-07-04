package com.example.notemark.main.domain.repository

import com.example.notemark.main.data.remote.repositoryImpl.NoteServiceImpl
import com.example.notemark.main.domain.model.NotesResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NotesRepository(private val api: NoteServiceImpl) {
    
    fun getNotes(
        page: Int,
        size: Int
    ): Flow<Result<NotesResponse>> = flow {
        emit(api.getNotes(page, size))
    }
}