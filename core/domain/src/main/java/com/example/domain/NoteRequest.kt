package com.example.domain

data class NoteRequest(
    val id: String,
    val title: String,
    val content: String,
    val createdAt: String,
    val updatedAt: String
)