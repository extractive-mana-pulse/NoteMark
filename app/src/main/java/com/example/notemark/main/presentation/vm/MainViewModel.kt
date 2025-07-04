package com.example.notemark.main.presentation.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notemark.main.domain.model.NotesUiState
import com.example.notemark.main.domain.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: NotesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    private val pageSize = 10

    init {
        loadNotes()
    }

    fun loadNotes(
        refresh: Boolean = false
    ) {
        viewModelScope.launch {
            val currentState = _uiState.value
            val page = if (refresh) 0 else currentState.currentPage

            _uiState.value = currentState.copy(isLoading = true, error = null)

            repository.getNotes(page, pageSize).collect { result ->
                result.fold(
                    onSuccess = { response ->
                        val newNotes = if (refresh) {
                            response.notes
                        } else {
                            currentState.notes + response.notes
                        }

                        _uiState.value = NotesUiState(
                            notes = newNotes,
                            isLoading = false,
                            currentPage = page + 1,
                            hasNextPage = response.notes.size == pageSize
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = currentState.copy(
                            isLoading = false,
                            error = exception.message ?: "Unknown error occurred"
                        )
                    }
                )
            }
        }
    }

    fun refreshNotes() {
        loadNotes(refresh = true)
    }

    fun loadNextPage() {
        if (!_uiState.value.isLoading && _uiState.value.hasNextPage) {
            loadNotes()
        }
    }
}