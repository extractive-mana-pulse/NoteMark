package com.example.notemark.main.presentation.screens.note

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.notemark.R
import com.example.notemark.auth.presentation.util.DeviceConfiguration
import com.example.notemark.main.domain.model.NoteRequest
import com.example.notemark.main.presentation.vm.NotesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNoteScreen(
    navController: NavHostController = rememberNavController(),
) {

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)

    when(deviceConfiguration) {
        DeviceConfiguration.MOBILE_PORTRAIT -> {
            CreateNoteBody(
                modifier = Modifier,
                navController,
            )
        }
        DeviceConfiguration.MOBILE_LANDSCAPE -> {
            CreateNoteBody(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.onPrimary
                    )
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.displayCutout)
                    .consumeWindowInsets(WindowInsets.navigationBars),
                navController,
            )
        }
        DeviceConfiguration.TABLET_PORTRAIT,
        DeviceConfiguration.TABLET_LANDSCAPE,
        DeviceConfiguration.DESKTOP -> {
            CreateNoteBody(
                modifier = Modifier,
                navController,
            )
        }
    }
}

@PreviewLightDark
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CreateNoteBody(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val viewModel: NotesViewModel = hiltViewModel()
    val focusRequester = remember { FocusRequester() }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    val noteState by viewModel.updateNoteState.collectAsStateWithLifecycle()
    val currentNote by viewModel.currentNote.collectAsStateWithLifecycle()
    val createNoteState by viewModel.createNoteState.collectAsStateWithLifecycle()

    // Log current note changes
    LaunchedEffect(currentNote) {
        android.util.Log.d("CreateNote", "Current note changed: ${currentNote?.id}")
        currentNote?.let { note ->
            if (title.isEmpty() || title == "Note Title") title = note.title
            if (content.isEmpty()) content = note.content
        }
    }

// In your CreateNoteBody, modify the LaunchedEffect for creating the note:
// Create initial note when screen opens
    LaunchedEffect(Unit) {
        android.util.Log.d("CreateNote", "Screen opened, creating initial note")
        focusRequester.requestFocus()

        // Generate a unique ID for the note
        val noteId = java.util.UUID.randomUUID().toString()

        val initialNoteRequest = NoteRequest(
            id = noteId, // Use generated UUID instead of empty string
            title = "Note Title",
            content = "",
            createdAt = System.currentTimeMillis().toString(),
            updatedAt = System.currentTimeMillis().toString()
        )
        viewModel.createNote(initialNoteRequest)
    }
    // Navigate after successful CREATE
    LaunchedEffect(createNoteState.isSuccess) {
        if (createNoteState.isSuccess) {
            android.util.Log.d("CreateNote", "Note created successfully: ${createNoteState.createdNote?.id}")
            viewModel.resetCreateNoteState()
        }
    }

    // Navigate after successful UPDATE
    LaunchedEffect(noteState.isSuccess) {
        if (noteState.isSuccess) {
            android.util.Log.d("CreateNote", "Note updated successfully, navigating back")
            viewModel.resetUpdateNoteState()
            viewModel.clearCurrentNote()
            navController.navigateUp()
        }
    }

    // Handle back navigation
    BackHandler {
        android.util.Log.d("CreateNote", "Back pressed")
        handleCreateModeBackNavigation(viewModel, navController)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            android.util.Log.d("CreateNote", "X button clicked")
                            handleCreateModeBackNavigation(viewModel, navController)
                        }
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.x),
                            contentDescription = "Navigate Up"
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            android.util.Log.d("CreateNote", "Save button clicked")
                            android.util.Log.d("CreateNote", "Title: '$title'")
                            android.util.Log.d("CreateNote", "Content: '$content'")
                            android.util.Log.d("CreateNote", "CurrentNote: ${currentNote?.id}")
                            android.util.Log.d("CreateNote", "isLoading: ${noteState.isLoading}")

                            if (title.isNotBlank() || content.isNotBlank()) {
                                currentNote?.let { note ->
                                    android.util.Log.d("CreateNote", "Updating note with id: ${note.id}")
                                    val noteRequest = NoteRequest(
                                        id = note.id,
                                        title = title.trim(),
                                        content = content.trim(),
                                        createdAt = note.createdAt,
                                        updatedAt = System.currentTimeMillis().toString()
                                    )
                                    viewModel.updateNote(noteRequest)
                                } ?: android.util.Log.e("CreateNote", "CurrentNote is NULL!")
                            } else {
                                android.util.Log.d("CreateNote", "Note is empty, navigating back")
                                handleCreateModeBackNavigation(viewModel, navController)
                            }
                        },
                        enabled = !noteState.isLoading
                    ) {
                        if (noteState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else {
                            Text(
                                text = "save note".uppercase(),
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 24.sp,
                                letterSpacing = 1.0.sp,
                                fontFamily = FontFamily(Font(R.font.space_grotesk_regular)),
                                modifier = Modifier.padding(end = 16.dp),
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        contentWindowInsets = WindowInsets.statusBars
    ) { innerPadding ->
        // ... rest of your UI code
        Column(
            modifier = modifier
                .background(MaterialTheme.colorScheme.onPrimary)
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            TextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                textStyle = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                placeholder = {
                    Text(
                        text = "Note title",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.onPrimary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.surface,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.surface,
                    disabledIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                )
            )

            TextField(
                value = content,
                onValueChange = { content = it },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = "Amet minim mollit non deserunt ullamco est sit aliqua dolor do amet sint.",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.onPrimary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                    disabledIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                )
            )
        }

        noteState.error?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

private fun handleCreateModeBackNavigation(
    viewModel: NotesViewModel,
    navController: NavHostController
) {
    // Check if note is empty and delete if needed
    if (viewModel.isCurrentNoteEmpty()) {
        viewModel.deleteCurrentNoteIfEmpty()
    }

    // Clear current note from ViewModel
    viewModel.clearCurrentNote()

    // Navigate back
    navController.navigateUp()
}
