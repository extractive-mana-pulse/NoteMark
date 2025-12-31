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
import com.example.notemark.main.domain.model.Note
import com.example.notemark.main.domain.model.NoteRequest
import com.example.notemark.main.domain.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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

    private val _updateNoteState = MutableStateFlow(NoteUiState())
    val updateNoteState: StateFlow<NoteUiState> = _updateNoteState.asStateFlow()

    private val _deleteNoteState = MutableStateFlow<DeleteNoteUiState>(DeleteNoteUiState.Idle)
    val deleteNoteState: StateFlow<DeleteNoteUiState> = _deleteNoteState.asStateFlow()

    // Current note being edited (only for edit mode)
    private val _currentNote = MutableStateFlow<NoteDTO?>(null)
    val currentNote: StateFlow<NoteDTO?> = _currentNote.asStateFlow()

    // Auto-save status (only for edit mode)
    private val _autoSaveStatus = MutableStateFlow<AutoSaveStatus>(AutoSaveStatus.Saved)
    val autoSaveStatus: StateFlow<AutoSaveStatus> = _autoSaveStatus.asStateFlow()

    // Track current mode
    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode.asStateFlow()

    private var autoSaveJob: Job? = null
    private val autoSaveDelay = 500L // 500ms debounce

    sealed class AutoSaveStatus {
        object Saved : AutoSaveStatus()
        object Saving : AutoSaveStatus()
        object Error : AutoSaveStatus()
    }

    // CREATE MODE FUNCTIONS
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
                        // Set the created note as current note for tracking
                        _currentNote.value = data
                    },
                    onFailure = { error ->
                        _createNoteState.value = _createNoteState.value.copy(
                            isLoading = false,
                            error = error.message ?: "Unknown error occurred while creating note."
                        )
                    }
                )
            }
        }
    }

    // Function to check if current note is empty (for deletion when user cancels)
    fun isCurrentNoteEmpty(): Boolean {
        val note = _currentNote.value ?: return true
        return note.title.isBlank() || note.title == "Note Title" && note.content.isBlank()
    }

    // Function to delete current note if empty
    fun deleteCurrentNoteIfEmpty() {
        val note = _currentNote.value ?: return
        if (isCurrentNoteEmpty()) {
            deleteNote(note.id)
        }
    }

    // Clear current note when leaving screen
    fun clearCurrentNote() {
        _currentNote.value = null
        autoSaveJob?.cancel()
    }

    // EDIT MODE FUNCTIONS
    fun enterEditMode(note: NoteDTO) {
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

    // Auto-save functions (only work in edit mode)
    fun updateNoteTitle(newTitle: String) {
        if (!_isEditMode.value) return // Only work in edit mode

        val current = _currentNote.value ?: return
        val updatedNote = current.copy(title = newTitle)
        _currentNote.value = updatedNote
        scheduleAutoSave()
    }

    fun updateNoteContent(newContent: String) {
        if (!_isEditMode.value) return // Only work in edit mode

        val current = _currentNote.value ?: return
        val updatedNote = current.copy(content = newContent)
        _currentNote.value = updatedNote
        scheduleAutoSave()
    }

    private fun scheduleAutoSave() {
        if (!_isEditMode.value) return // Only work in edit mode

        // Cancel previous auto-save job
        autoSaveJob?.cancel()

        // Set status to saving
        _autoSaveStatus.value = AutoSaveStatus.Saving

        // Schedule new auto-save job with debounce
        autoSaveJob = viewModelScope.launch {
            delay(autoSaveDelay)
            performAutoSave()
        }
    }

    private suspend fun performAutoSave() {
        if (!_isEditMode.value) return // Only work in edit mode

        val noteToSave = _currentNote.value ?: return

        try {
            val noteRequest = NoteRequest(
                id = noteToSave.id,
                title = noteToSave.title,
                content = noteToSave.content,
                createdAt = noteToSave.createdAt,
                updatedAt = System.currentTimeMillis().toString() // Update lastEditedAt
            )

            val result = repository.updateNote(noteRequest)

            result.collect { result ->
                result.fold(
                    onSuccess = { data ->
                        _currentNote.value = data
                        _autoSaveStatus.value = AutoSaveStatus.Saved
                    },
                    onFailure = { error ->
                        _autoSaveStatus.value = AutoSaveStatus.Error
                    }
                )
            }
        } catch (e: Exception) {
            _autoSaveStatus.value = AutoSaveStatus.Error
        }
    }

    // Force save for edit mode (when user finishes editing)
    fun finishEditing() {
        if (!_isEditMode.value) return

        autoSaveJob?.cancel()
        viewModelScope.launch {
            performAutoSave()
            // Update lastEditedAt to current time when finishing editing
            _currentNote.value?.let { note ->
                val updatedNote = note.copy(
                    updatedAt = System.currentTimeMillis().toString()
                )
                _currentNote.value = updatedNote
            }
            exitEditMode()
        }
    }

    // Regular update function (for manual saves, not auto-save)
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
                            error = error.message ?: "Unknown error occurred while updating note."
                        )
                    }
                )
            }
        }
    }

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

    // Utility functions
    fun resetDeleteState() {
        _deleteNoteState.value = DeleteNoteUiState.Idle
    }

    fun resetCreateNoteState() {
        _createNoteState.value = NoteUiState()
    }

    fun resetUpdateNoteState() {
        _updateNoteState.value = NoteUiState()
    }

    // Clear all saved notes locally
    fun clear() {
        viewModelScope.launch(Dispatchers.IO) {
            noteDatabase.dao.clearAll()
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
    val createdNote: NoteDTO? = null,
    val error: String? = null
)