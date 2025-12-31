package com.example.notemark.main.data.remote

import com.example.notemark.main.data.local.NoteEntity
import com.example.notemark.main.domain.model.Note
import com.example.notemark.main.domain.model.NoteRequest
import com.example.notemark.main.domain.model.NotesResponse

fun NoteDTO.toNoteEntity(): NoteEntity {
    return NoteEntity(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}


fun NoteEntity.toNote(): Note {
    return Note(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun NoteEntity.toNoteDTO(): NoteDTO {
    return NoteDTO(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun NoteRequest.toNotEntity(): NoteEntity {
    return NoteEntity(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun NotesResponse.toNoteEntityList(): List<NoteEntity> {
    return this.notes.map { it.toNoteEntity() }
}