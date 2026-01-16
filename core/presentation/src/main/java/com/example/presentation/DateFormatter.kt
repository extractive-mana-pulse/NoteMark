package com.example.presentation

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

object DateFormatter {

    private val homeCurrentYearFormatter = DateTimeFormatter.ofPattern("dd MMM", Locale.ENGLISH)
    private val homeOtherYearFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
    private val detailsFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.ENGLISH)

    /**
     * Formats a timestamp for home screen display:
     * - "dd MMM" for current year (e.g., "19 Apr")
     * - "dd MMM yyyy" for other years (e.g., "19 Apr 2024")
     * If the timestamp is less than 5 minutes ago, returns "Just now"
     */
    fun formatForHome(timestamp: Long): String {
        val noteTime = Instant.ofEpochMilli(timestamp)

        val localDateTime = LocalDateTime.ofInstant(noteTime, ZoneId.systemDefault())
        val currentYear = LocalDateTime.now().year
        val noteYear = localDateTime.year
        return if (noteYear == currentYear) {
            localDateTime.format(homeCurrentYearFormatter)
        } else {
            localDateTime.format(homeOtherYearFormatter)
        }
    }

    /**
     * Formats a timestamp for details screen display: "dd MMM yyyy, HH:mm"
     * If the timestamp is less than 5 minutes ago, returns "Just now"
     */
    fun formatForDetails(timestamp: Long): String {
        val now = Instant.now()
        val noteTime = Instant.ofEpochMilli(timestamp)

        val minutesDiff = ChronoUnit.MINUTES.between(noteTime, now)

        return if (minutesDiff < 5) {
            "Just now"
        } else {
            val localDateTime = LocalDateTime.ofInstant(noteTime, ZoneId.systemDefault())
            localDateTime.format(detailsFormatter)
        }
    }

    /**
     * Formats an ISO 8601 string for home screen display
     */
    fun formatForHome(isoString: String): String {
        return try {
            val instant = Instant.parse(isoString)
            formatForHome(instant.toEpochMilli())
        } catch (e: Exception) {
            "Invalid date"
        }
    }

    /**
     * Formats an ISO 8601 string for details screen display
     */
    fun formatForDetails(isoString: String): String {
        return try {
            val instant = Instant.parse(isoString)
            formatForDetails(instant.toEpochMilli())
        } catch (e: Exception) {
            "Invalid date"
        }
    }

    /**
     * Gets current timestamp as ISO 8601 string
     */
    fun getCurrentIsoString(): String = Instant.now().toString()
}

fun String.formatAsNoteDate(): String = DateFormatter.formatForHome(this)
fun String.formatAsDetailsDate(): String = DateFormatter.formatForDetails(this)