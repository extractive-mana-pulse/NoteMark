package com.example.presentation

enum class SyncInterval(val displayName: String) {
    MANUAL_ONLY("Manual Only"),
    EVERY_15_MINUTES("15 minutes"),
    EVERY_30_MINUTES("30 minutes"),
    EVERY_HOUR("1 hour")
}