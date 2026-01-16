package com.example.presentation.home.components

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import com.example.domain.Note
import com.example.presentation.DeleteNoteUiState
import com.example.presentation.NotesViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteActionSheet(
    isVisible: Boolean,
    onDismissSheet: () -> Unit,
    noteId: String,
    notes: LazyPagingItems<Note>
) {
    val context = LocalContext.current
    val viewModel: NotesViewModel = hiltViewModel()
    val deleteNoteState by viewModel.deleteNoteState.collectAsStateWithLifecycle()
    val onDeleteClick = { viewModel.deleteNote(noteId = noteId) }

    LaunchedEffect(deleteNoteState) {
        when (deleteNoteState) {
            is DeleteNoteUiState.Success -> {
                delay(2_000L)
                notes.refresh()
                viewModel.resetDeleteState()
                onDismissSheet()
            }
            is DeleteNoteUiState.Error -> {
                Toast.makeText(
                    context,
                    (deleteNoteState as DeleteNoteUiState.Error).message,
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.resetDeleteState()
            }
            else -> {}
        }
    }

    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = {
                onDismissSheet()
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ActionContent(
                    deleteNoteState = deleteNoteState,
                    onDeleteClick = onDeleteClick,
                )
            }
        }
    }
}
