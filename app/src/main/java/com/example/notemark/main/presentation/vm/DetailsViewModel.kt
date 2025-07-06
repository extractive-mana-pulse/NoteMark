package com.example.notemark.main.presentation.vm

import android.content.pm.ActivityInfo
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailsScreenViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DetailsScreenUiState())
    val uiState: StateFlow<DetailsScreenUiState> = _uiState.asStateFlow()

    private var hideUiJob: Job? = null
    private var originalOrientation: Int = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

    fun setOriginalOrientation(orientation: Int) {
        originalOrientation = orientation
    }

    fun toggleReaderMode() {
        val currentState = _uiState.value
        hideUiJob?.cancel()

        if (!currentState.isReaderMode) {
            _uiState.update {
                it.copy(
                    isReaderMode = true,
                    isUiVisible = true,
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                )
            }
            viewModelScope.launch {
                delay(500)
                _uiState.update { it.copy(isUiVisible = false) }
            }
        } else {
            _uiState.update {
                it.copy(
                    isReaderMode = false,
                    isUiVisible = true,
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                )
            }
        }
    }

    fun onOrientationHandled() {
        _uiState.update { it.copy(requestedOrientation = null) }
    }

    fun onScreenTap() {
        val currentState = _uiState.value

        if (currentState.isReaderMode) {
            hideUiJob?.cancel()

            if (currentState.isUiVisible) {
                _uiState.update { it.copy(isUiVisible = false) }
            } else {
                _uiState.update { it.copy(isUiVisible = true) }
                startAutoHideTimer()
            }
        }
    }

    fun onScrollDetected() {
        val currentState = _uiState.value

        if (currentState.isReaderMode && currentState.isUiVisible) {
            hideUiJob?.cancel()
            _uiState.update { it.copy(isUiVisible = false) }
        }
    }

    private fun startAutoHideTimer() {
        hideUiJob = viewModelScope.launch {
            delay(5000)
            _uiState.update { it.copy(isUiVisible = false) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        hideUiJob?.cancel()
    }
}

data class DetailsScreenUiState(
    val isReaderMode: Boolean = false,
    val isUiVisible: Boolean = true,
    val requestedOrientation: Int? = null
)