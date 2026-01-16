package com.example.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull

interface ConnectivityObserver {
    fun isConnected(): Flow<Boolean>

    suspend fun getCurrentNetworkState(): Boolean {
        return withTimeoutOrNull(1000) { // 1 second timeout
            isConnected().first()
        } ?: false // Default to offline if timeout
    }
}