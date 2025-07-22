package com.example.notemark.main.presentation.screens.note

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.notemark.R
import com.example.notemark.auth.presentation.util.DeviceConfiguration
import com.example.notemark.main.DateFormatter
import com.example.notemark.main.domain.model.CreateNoteRequest
import com.example.notemark.main.presentation.vm.NotesViewModel
import com.example.notemark.navigation.screens.HomeScreens
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNoteScreen(
    navController: NavHostController = rememberNavController(),
) {

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)

    when(deviceConfiguration) {
        DeviceConfiguration.MOBILE_PORTRAIT -> {
            NoteCreationBody(
                modifier = Modifier,
                navController,
            )
        }
        DeviceConfiguration.MOBILE_LANDSCAPE -> {
            NoteCreationBody(
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
            NoteCreationBody(
                modifier = Modifier,
                navController,
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun NoteCreationBody(
    modifier: Modifier,
    navController: NavHostController,
) {
    val context = LocalContext.current
    val uuid = remember { UUID.randomUUID() }
    val viewModel: NotesViewModel = hiltViewModel()
    val focusRequester = remember { FocusRequester() }
    var content by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    val noteState by viewModel.createNoteState.collectAsStateWithLifecycle()
    val creationTime by remember { mutableStateOf(DateFormatter.getCurrentIsoString()) }

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
            Log.d(
                "CreateNoteScreen",
                "CreateNoteScreen: ${noteState.error}"
            )
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
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
                            navController.navigateUp()
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
                                noteState.isLoading
                                val noteRequest = CreateNoteRequest(
                                    id = uuid.toString(),
                                    title = title.trim(),
                                    content = content.trim(),
                                    createdAt = creationTime,
                                    updatedAt = creationTime
                                )
                                viewModel.createNote(noteRequest)
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
                    focusedIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                    disabledIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                )
            )
            Spacer(modifier = Modifier.imePadding().weight(1f))
        }
        noteState.error?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .padding(16.dp)
            )
        }
    }
}