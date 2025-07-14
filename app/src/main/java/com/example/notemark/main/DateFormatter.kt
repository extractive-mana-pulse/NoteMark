package com.example.notemark.main

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

object DateFormatter {

    private val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.ENGLISH)
    
    /**
     * Formats a timestamp to the required format: "dd MMM yyyy, HH:mm"
     * If the timestamp is less than 5 minutes ago, returns "Just now"
     */
    fun formatNoteDate(timestamp: Long): String {
        val now = Instant.now()
        val noteTime = Instant.ofEpochMilli(timestamp)
        
        // Check if less than 5 minutes ago
        val minutesDiff = ChronoUnit.MINUTES.between(noteTime, now)
        
        return if (minutesDiff < 5) {
            "Just now"
        } else {
            val localDateTime = LocalDateTime.ofInstant(noteTime, ZoneId.systemDefault())
            localDateTime.format(formatter)
        }
    }
    
    /**
     * Formats an ISO 8601 string to the required format
     */
    fun formatNoteDate(isoString: String): String {
        return try {
            val instant = Instant.parse(isoString)
            formatNoteDate(instant.toEpochMilli())
        } catch (e: Exception) {
            // Fallback if parsing fails
            "Invalid date"
        }
    }
    
    /**
     * Gets current timestamp in milliseconds
     */
    fun getCurrentTimestamp(): Long = System.currentTimeMillis()
    
    /**
     * Gets current timestamp as ISO 8601 string
     */
    fun getCurrentIsoString(): String = Instant.now().toString()
}

// Extension functions for easier usage
fun Long.formatAsNoteDate(): String = DateFormatter.formatNoteDate(this)
fun String.formatAsNoteDate(): String = DateFormatter.formatNoteDate(this)