package com.example.data.local

import com.example.domain.LocalNoteDataSource
import javax.inject.Inject

class LocalNoteDataSourceImpl @Inject constructor(
    private val noteDatabase: NoteDatabase
) : LocalNoteDataSource {
    
    override suspend fun clearAllNotes() {
        noteDatabase.dao.clearAll()
    }
    
    override suspend fun getNotesCount(): Int {
        return noteDatabase.dao.getNotesCount()
    }
}