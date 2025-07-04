package com.example.notemark.main.data.remote.api

import com.example.notemark.main.domain.model.NotesResponse

interface NoteService {

    suspend fun getNotes(
        page: Int,
        size: Int,
    ): Result<NotesResponse>

}