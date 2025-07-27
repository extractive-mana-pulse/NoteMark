package com.example.notemark.main.presentation.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.notemark.main.data.local.NoteDatabase
import com.example.notemark.main.data.local.NoteEntity
import com.example.notemark.main.data.remote.NoteDTO
import com.example.notemark.main.data.remote.toNote
import com.example.notemark.main.domain.model.NoteRequest
import com.example.notemark.main.domain.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val repository: NotesRepository,
    private val noteDatabase: NoteDatabase,
    pager: Pager<Int, NoteEntity>
) : ViewModel() {

    val notePagingFlow = pager
        .flow
        .map { pagingData ->
            pagingData.map {
                it.toNote()
            }
        }
        .cachedIn(viewModelScope)


    private val _createNoteState = MutableStateFlow(NoteUiState())
    val createNoteState: StateFlow<NoteUiState> = _createNoteState.asStateFlow()

    fun createNote(noteRequest: NoteRequest) {
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
    private val _updateNoteState = MutableStateFlow(NoteUiState())
    val updateNoteState: StateFlow<NoteUiState> = _updateNoteState.asStateFlow()

    fun updateNote(noteRequest: NoteRequest) {
        viewModelScope.launch {
            _updateNoteState.value = _updateNoteState.value.copy(isLoading = true, error = null)

            val result = repository.updateNote(noteRequest)

            result.collect { result ->
                result.fold(
                    onSuccess = { data ->
                        _updateNoteState.value = _updateNoteState.value.copy(
                            isLoading = false,
                            isSuccess = true,
                            createdNote = data
                        )
                        },
                    onFailure = { error ->
                        _updateNoteState.value = _updateNoteState.value.copy(
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

                    },
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

    // this function is to clear all saved notes locally of the user.
    fun clear() {
        viewModelScope.launch(Dispatchers.IO) {
            noteDatabase.dao.clearAll()
        }
    }
}
sealed class DeleteNoteUiState {
    object Idle : DeleteNoteUiState()
    object Loading : DeleteNoteUiState()
    object Success : DeleteNoteUiState()
    data class Error(val message: String) : DeleteNoteUiState()
}

data class NoteUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val createdNote: NoteDTO? = null,
    val error: String? = null
)