package com.example.notemark.main.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.notemark.main.presentation.screens.note.SyncRecord

@Dao
interface NoteDao {

    @Query("SELECT COUNT(*) FROM noteentity")
    suspend fun getNotesCount(): Int

    @Upsert
    suspend fun upsertAll(notes: List<NoteEntity>)

    @Upsert
    suspend fun upsertNote(note: NoteEntity)

    @Query("DELETE FROM noteentity WHERE id = :noteId")
    suspend fun deleteNoteById(noteId: String)

    @Query("SELECT * FROM noteentity")
    fun pagingSource(): PagingSource<Int, NoteEntity>

    @Query("DELETE FROM noteentity")
    suspend fun clearAll()
}