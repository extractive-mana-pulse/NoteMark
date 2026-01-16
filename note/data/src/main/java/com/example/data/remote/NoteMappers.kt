package com.example.data.remote

import com.example.data.local.NoteEntity
import com.example.domain.Note
import com.example.domain.NoteRequest
import com.example.domain.NotesResponse as DomainNotesResponse

// DTO <-> Domain Note
fun NoteDTO.toDomain(): Note {
    return Note(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Note.toDTO(): NoteDTO {
    return NoteDTO(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

// DTO <-> NoteEntity
fun NoteDTO.toEntity(): NoteEntity {
    return NoteEntity(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun NoteEntity.toDTO(): NoteDTO {
    return NoteDTO(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

// NoteEntity <-> Domain Note
fun NoteEntity.toDomain(): Note {
    return Note(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt,
        updatedAt = updatedAt ?: ""
    )
}

fun Note.toEntity(): NoteEntity {
    return NoteEntity(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

// NoteRequest -> DTO
fun NoteRequest.toDTO(): NoteRequestDTO {
    return NoteRequestDTO(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

// NoteRequest -> DTO
fun NoteRequestDTO.toNote(): Note {
    return Note(
        id = id ?: "",
        title = title,
        content = content,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

// NotesResponse (data) <-> NotesResponse (domain)
fun NotesResponse.toDomain(): DomainNotesResponse {
    return DomainNotesResponse(
        notes = this.notes.map { it.toDomain() },
        totalPages = this.totalPages,
        currentPage = this.currentPage,
        totalNotes = this.totalCount
    )
}