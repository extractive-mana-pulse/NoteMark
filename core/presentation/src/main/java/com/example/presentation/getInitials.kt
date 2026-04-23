package com.example.presentation

fun getInitials(username: String): String {
    val trimmedName = username.trim()
    if (trimmedName.isEmpty()) return "U"

    val words = trimmedName.split("\\s+".toRegex()).filter { it.isNotEmpty() }

    return when {
        words.isEmpty() -> "U"
        words.size == 1 -> {
            
            val word = words[0].uppercase()
            if (word.length >= 2) word.take(2) else word
        }
        else -> {
            
            val firstInitial = words.first().first().uppercase()
            val lastInitial = words.last().first().uppercase()
            firstInitial + lastInitial
        }
    }
}