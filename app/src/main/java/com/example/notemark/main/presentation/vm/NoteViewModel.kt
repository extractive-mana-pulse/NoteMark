package com.example.notemark.main.presentation.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notemark.main.domain.model.CreateNoteRequest
import com.example.notemark.main.domain.model.Note
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

    fun loadNextPage() {
        if (!_uiState.value.isLoading && _uiState.value.hasNextPage) {
            loadNotes()
        }
    }

    private val _createNoteState = MutableStateFlow(CreateNoteUiState())
    val createNoteState: StateFlow<CreateNoteUiState> = _createNoteState.asStateFlow()

    fun createNote(noteRequest: CreateNoteRequest) {
        viewModelScope.launch {
            _createNoteState.value = _createNoteState.value.copy(isLoading = true, error = null)

            val result = repository.createNote(noteRequest)

            result.collect { result ->
                result.fold(
                    onSuccess = { data ->
                        _createNoteState.value = _createNoteState.value.copy(
                            isLoading = false,
                            isSuccess = true,
                            createdNote = data
                        )
                    },
                    onFailure = { error ->
                        _createNoteState.value = _createNoteState.value.copy(
                            isLoading = false,
                            error = error.message ?: "Unknown error occurred while creating error."
                        )
                    }
                )
            }
        }
    }

    private val _deleteNoteState = MutableStateFlow<DeleteNoteUiState>(DeleteNoteUiState.Idle)
    val deleteNoteState: StateFlow<DeleteNoteUiState> = _deleteNoteState.asStateFlow()

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            _deleteNoteState.value = DeleteNoteUiState.Loading

            try {
                val result = repository.deleteNote(noteId)
                result.fold(
                    onSuccess = {
                        _deleteNoteState.value = DeleteNoteUiState.Success
                        val currentNotes = _uiState.value.notes.toMutableList()
                        currentNotes.removeAll { it.id == noteId }
                        _uiState.value = _uiState.value.copy(
                            notes = currentNotes,
                            isLoading = false,
                            error = null,
                        )},
                    onFailure = { error ->
                        _deleteNoteState.value = DeleteNoteUiState.Error(error.message ?: "Unknown error occurred")
                    }
                )
            } catch (e: Exception) {
                _deleteNoteState.value = DeleteNoteUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
    fun resetDeleteState() { _deleteNoteState.value = DeleteNoteUiState.Idle }
}
sealed class DeleteNoteUiState {
    object Idle : DeleteNoteUiState()
    object Loading : DeleteNoteUiState()
    object Success : DeleteNoteUiState()
    data class Error(val message: String) : DeleteNoteUiState()
}

data class CreateNoteUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val createdNote: Note? = null,
    val error: String? = null
)