package com.example.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [NoteEntity::class],
    version = 3
)
abstract class NoteDatabase: RoomDatabase() {
    abstract val dao: NoteDao
}