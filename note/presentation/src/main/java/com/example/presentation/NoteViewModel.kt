package com.example.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import com.example.domain.LocalNoteDataSource
import com.example.domain.Note
import com.example.domain.NoteRequest
import com.example.domain.NoteService
import com.example.domain.Result
import com.example.domain.RootError
import com.example.domain.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val service: NoteService,
    private val sessionManager: SessionRepository,
    private val localDataSource: LocalNoteDataSource,
    pager: Pager<Int, Note>
) : ViewModel() {

    val notePagingFlow = pager
        .flow
        .cachedIn(viewModelScope)

    private val _createNoteState = MutableStateFlow(NoteUiState())
    val createNoteState: StateFlow<NoteUiState> = _createNoteState.asStateFlow()

    private val _updateNoteState = MutableStateFlow(NoteUiState())
    val updateNoteState: StateFlow<NoteUiState> = _updateNoteState.asStateFlow()

    private val _deleteNoteState = MutableStateFlow<DeleteNoteUiState>(DeleteNoteUiState.Idle)
    val deleteNoteState: StateFlow<DeleteNoteUiState> = _deleteNoteState.asStateFlow()

    private val _currentNote = MutableStateFlow<Note?>(null)
    val currentNote: StateFlow<Note?> = _currentNote.asStateFlow()

    private val _autoSaveStatus = MutableStateFlow<AutoSaveStatus>(AutoSaveStatus.Saved)
    val autoSaveStatus: StateFlow<AutoSaveStatus> = _autoSaveStatus.asStateFlow()

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode.asStateFlow()

    private var autoSaveJob: Job? = null
    private val autoSaveDelay = 500L

    sealed class AutoSaveStatus {
        object Saved : AutoSaveStatus()
        object Saving : AutoSaveStatus()
        object Error : AutoSaveStatus()
    }

    private val _username = MutableStateFlow<String?>(null)
    val username: StateFlow<String?> = _username.asStateFlow()

    fun getUsername(): String? = sessionManager.getUserName()

    private val _accessToken = MutableStateFlow<String?>(null)
    val accessToken: StateFlow<String?> = _accessToken.asStateFlow()

    fun getAccessToken(): String? = sessionManager.getAccessToken()

    private val _refreshToken = MutableStateFlow<String?>(null)
    val refreshToken: StateFlow<String?> = _refreshToken.asStateFlow()

    fun getRefreshToken(): String? = sessionManager.getRefreshToken()

    // CREATE MODE FUNCTIONS
    fun createNote(noteRequest: NoteRequest) {
        viewModelScope.launch {
            _createNoteState.value = _createNoteState.value.copy(isLoading = true, error = null)

            when (val result = service.createNote(noteRequest)) {
                is Result.Success<Note, RootError> -> {
                    _createNoteState.value = _createNoteState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        createdNote = result.data
                    )
                    _currentNote.value = result.data
                }
                is Result.Error<Note, RootError> -> {
                    _createNoteState.value = _createNoteState.value.copy(
                        isLoading = false,
                        error = result.error.toString()
                    )
                }

            }
        }
    }

    fun isCurrentNoteEmpty(): Boolean {
        val note = _currentNote.value ?: return true
        return note.title.isBlank() || note.title == "Note Title" && note.content.isBlank()
    }

    fun deleteCurrentNoteIfEmpty() {
        val note = _currentNote.value ?: return
        if (isCurrentNoteEmpty()) {
            deleteNote(note.id)
        }
    }

    fun clearCurrentNote() {
        _currentNote.value = null
        autoSaveJob?.cancel()
    }

    // EDIT MODE FUNCTIONS
    fun enterEditMode(note: Note) {
        _isEditMode.value = true
        _currentNote.value = note
        _autoSaveStatus.value = AutoSaveStatus.Saved
    }

    fun exitEditMode() {
        _isEditMode.value = false
        _currentNote.value = null
        autoSaveJob?.cancel()
        _autoSaveStatus.value = AutoSaveStatus.Saved
    }

    fun updateNoteTitle(newTitle: String) {
        if (!_isEditMode.value) return

        val current = _currentNote.value ?: return
        val updatedNote = current.copy(title = newTitle)
        _currentNote.value = updatedNote
        scheduleAutoSave()
    }

    fun updateNoteContent(newContent: String) {
        if (!_isEditMode.value) return

        val current = _currentNote.value ?: return
        val updatedNote = current.copy(content = newContent)
        _currentNote.value = updatedNote
        scheduleAutoSave()
    }

    private fun scheduleAutoSave() {
        if (!_isEditMode.value) return

        autoSaveJob?.cancel()
        _autoSaveStatus.value = AutoSaveStatus.Saving

        autoSaveJob = viewModelScope.launch {
            delay(autoSaveDelay)
            performAutoSave()
        }
    }

    private suspend fun performAutoSave() {
        if (!_isEditMode.value) return

        val noteToSave = _currentNote.value ?: return

        try {
            val noteRequest = NoteRequest(
                id = noteToSave.id,
                title = noteToSave.title,
                content = noteToSave.content,
                createdAt = noteToSave.createdAt,
                updatedAt = System.currentTimeMillis().toString()
            )

            when (val result = service.updateNote(noteRequest)) {
                is Result.Success<Note, RootError> -> {
                    _currentNote.value = result.data
                    _autoSaveStatus.value = AutoSaveStatus.Saved
                }
                is Result.Error<Note, RootError> -> {
                    _autoSaveStatus.value = AutoSaveStatus.Error
                }
            }
        } catch (e: Exception) {
            _autoSaveStatus.value = AutoSaveStatus.Error
        }
    }

    fun finishEditing() {
        if (!_isEditMode.value) return

        autoSaveJob?.cancel()
        viewModelScope.launch {
            performAutoSave()
            _currentNote.value?.let { note ->
                val updatedNote = note.copy(
                    updatedAt = System.currentTimeMillis().toString()
                )
                _currentNote.value = updatedNote
            }
            exitEditMode()
        }
    }

    fun updateNote(noteRequest: NoteRequest) {
        viewModelScope.launch {
            _updateNoteState.value = _updateNoteState.value.copy(isLoading = true, error = null)

            val result = service.updateNote(noteRequest)

            when (result) {
                is com.example.domain.Result.Success<Note, RootError> -> {
                    _updateNoteState.value = _updateNoteState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        createdNote = result.data
                    )
                }
                is com.example.domain.Result.Error<Note, RootError> -> {
                    _updateNoteState.value = _updateNoteState.value.copy(
                        isLoading = false,
                        error = result.error.toString()
                    )
                }
            }
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            _deleteNoteState.value = DeleteNoteUiState.Loading

            try {
                when (val result = service.deleteNote(noteId)) {
                    is Result.Success<Unit, RootError> -> {
                        _deleteNoteState.value = DeleteNoteUiState.Success
                    }
                    is Result.Error<Unit, RootError> -> {
                        _deleteNoteState.value = DeleteNoteUiState.Error(
                            result.error.toString()
                        )
                    }
                }
            } catch (e: Exception) {
                _deleteNoteState.value = DeleteNoteUiState.Error(
                    e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun resetDeleteState() {
        _deleteNoteState.value = DeleteNoteUiState.Idle
    }

    fun resetCreateNoteState() {
        _createNoteState.value = NoteUiState()
    }

    fun resetUpdateNoteState() {
        _updateNoteState.value = NoteUiState()
    }

    fun clear() {
        viewModelScope.launch(Dispatchers.IO) {
            localDataSource.clearAllNotes()
        }
    }

    override fun onCleared() {
        super.onCleared()
        autoSaveJob?.cancel()
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
    val createdNote: Note? = null,
    val error: String? = null
)