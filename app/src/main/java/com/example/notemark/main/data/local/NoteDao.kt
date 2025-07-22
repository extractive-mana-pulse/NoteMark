package com.example.notemark.main.data.local

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

    @Query("SELECT * FROM noteentity")
    fun pagingSource(): PagingSource<Int, NoteEntity>

    @Query("DELETE FROM noteentity")
    suspend fun clearAll()
}