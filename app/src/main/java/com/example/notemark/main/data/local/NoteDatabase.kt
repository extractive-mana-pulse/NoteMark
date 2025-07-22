package com.example.notemark.main.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [NoteEntity::class],
    version = 2
)
abstract class NoteDatabase: RoomDatabase() {
    abstract val dao: NoteDao
}