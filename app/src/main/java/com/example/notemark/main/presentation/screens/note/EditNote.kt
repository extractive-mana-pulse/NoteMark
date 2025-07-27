package com.example.notemark.main.presentation.screens.note

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.notemark.R
import com.example.notemark.auth.presentation.util.DeviceConfiguration
import com.example.notemark.main.DateFormatter
import com.example.notemark.main.domain.model.NoteRequest
import com.example.notemark.main.domain.model.getFormattedCreatedAt
import com.example.notemark.main.presentation.vm.NotesViewModel
import com.example.notemark.navigation.screens.HomeScreens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen(
    navController: NavController = rememberNavController(),
    noteId: String,
) {

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)

    when(deviceConfiguration) {
        DeviceConfiguration.MOBILE_PORTRAIT -> {
            EditNoteScreenBody(
                modifier = Modifier,
                navController = navController,
                noteId = noteId
            )
        }
        DeviceConfiguration.MOBILE_LANDSCAPE -> {
            EditNoteScreenBody(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.onPrimary
                    )
                    .windowInsetsPadding(WindowInsets.displayCutout)
                    .fillMaxSize()
                    .consumeWindowInsets(WindowInsets.navigationBars),
                navController = navController,
                noteId = noteId
            )
        }
        DeviceConfiguration.TABLET_PORTRAIT,
        DeviceConfiguration.TABLET_LANDSCAPE,
        DeviceConfiguration.DESKTOP -> {
            EditNoteScreenBody(
                modifier = Modifier,
                navController = navController,
                noteId = noteId
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun EditNoteScreenBody(
    modifier: Modifier,
    navController: NavController,
    noteId: String,
) {

    val context = LocalContext.current
    val viewModel: NotesViewModel = hiltViewModel()
    val noteState by viewModel.updateNoteState.collectAsStateWithLifecycle()
    val updateTime by remember { mutableStateOf(DateFormatter.getCurrentIsoString()) }

    val notes = viewModel.notePagingFlow.collectAsLazyPagingItems()
    val note = notes.itemSnapshotList.items.find { it.id == noteId }

    LaunchedEffect(noteState) {
        if (noteState.isSuccess) {
            navController.navigate(HomeScreens.Home.route) {
                popUpTo(HomeScreens.Home.route) {
                    inclusive = true
                }
            }
        }
    }

    LaunchedEffect(noteState.error) {
        noteState.error?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    }

    if (note != null && note.id == noteId) {
        var title by remember { mutableStateOf(note.title) }
        var content by remember { mutableStateOf(note.content) }
        var showExitDialog by remember { mutableStateOf(false) }

        val originalTitle = remember { note.title }
        val originalContent = remember { note.content }

        val hasChanges = title != originalTitle || content != originalContent

        BackHandler(enabled = true) {
            if (hasChanges) {
                showExitDialog = true
            } else {
                navController.popBackStack()
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = ""
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                if (hasChanges) {
                                    showExitDialog = true
                                } else {
                                    navController.popBackStack()
                                }
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
                                if (title.isNotBlank() && content.isNotBlank()) {
                                    val noteRequest = NoteRequest(
                                        id = noteId,
                                        title = title.trim(),
                                        content = content.trim(),
                                        createdAt = note.getFormattedCreatedAt(),
                                        updatedAt = updateTime
                                    )
                                    viewModel.updateNote(noteRequest)
                                }
                            },
                            enabled = !noteState.isLoading && title.isNotBlank() && content.isNotBlank()
                        ) {
                            if (noteState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                )
                            } else {
                                Text(
                                    text = stringResource(R.string.update_note).uppercase(),
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
            }
        ) { innerPadding ->
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.onPrimary)
                    .padding(innerPadding),
            ) {
                Column {
                    TextField(
                        value = title,
                        onValueChange = { title = it },
                        modifier = Modifier
                            .fillMaxWidth(),
                        textStyle = MaterialTheme.typography.titleLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        placeholder = {
                            Text(
                                text = "Note Title",
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
                        modifier = Modifier
                            .fillMaxSize(),
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
                            focusedIndicatorColor = MaterialTheme.colorScheme.surface,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.surface,
                            disabledIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                        )
                    )
                }
            }
        }
        if (showExitDialog) {
            AlertDialog(
                onDismissRequest = { showExitDialog = false },
                title = {
                    Text(
                        text = stringResource(R.string.discard_changes),
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = stringResource(R.string.dialog_content_text),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showExitDialog = false
                            navController.navigateUp()
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.discard),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showExitDialog = false }
                    ) {
                        Text(
                            text = stringResource(R.string.keep_editing)
                        )
                    }
                }
            )
        }
    }
}