package com.example.notemark.main.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.notemark.main.presentation.screens.note.SyncRecord

@Database(
    entities = [NoteEntity::class, SyncRecord::class],
    version = 3
)
abstract class NoteDatabase: RoomDatabase() {
    abstract val dao: NoteDao
    abstract val syncDao: SyncDao
}