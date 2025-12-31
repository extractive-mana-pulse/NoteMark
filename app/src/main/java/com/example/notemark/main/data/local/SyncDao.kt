package com.example.notemark.main.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.notemark.main.presentation.screens.note.SyncRecord

@Dao
interface SyncDao {
    @Upsert
    suspend fun insertSyncRecord(record: SyncRecord)

    @Query("DELETE FROM syncrecord WHERE id = :id")
    suspend fun deleteSyncRecord(id: String)

    @Query("SELECT * FROM syncrecord WHERE userId = :userId")
    suspend fun getAllSyncRecords(userId: String): List<SyncRecord>
}