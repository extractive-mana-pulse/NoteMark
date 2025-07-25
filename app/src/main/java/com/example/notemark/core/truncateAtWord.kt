package com.example.notemark.core

fun String.truncateAtWord(maxLength: Int): String {
    if (this.length <= maxLength) return this
    
    val truncated = this.take(maxLength)
    val lastSpace = truncated.lastIndexOf(' ')
    
    return if (lastSpace > 0) {
        truncated.take(lastSpace) + "..."
    } else {
        "$truncated..."
    }
}