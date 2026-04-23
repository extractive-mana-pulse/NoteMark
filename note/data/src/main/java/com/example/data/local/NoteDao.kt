package com.example.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

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
    fun pagingSource(): PagingSource<Int, com.example.domain.NoteEntity>

    @Query("DELETE FROM noteentity")
    suspend fun clearAll()
}