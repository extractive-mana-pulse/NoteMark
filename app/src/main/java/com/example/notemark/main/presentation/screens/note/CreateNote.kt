package com.example.notemark.main.presentation.screens.note

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.notemark.main.DateFormatter
import com.example.notemark.main.domain.model.CreateNoteRequest
import com.example.notemark.main.presentation.vm.MainViewModel
import com.example.notemark.navigation.screens.HomeScreens
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNoteScreen(
    navController: NavHostController = rememberNavController(),
) {
    val uuid = remember { UUID.randomUUID() }
    val context = LocalContext.current
    val viewModel: MainViewModel = hiltViewModel()
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
//    var isLoading by remember { mutableStateOf(false) }
    val noteState by viewModel.createNoteState.collectAsStateWithLifecycle()
    val creationTime by remember { mutableStateOf(DateFormatter.getCurrentIsoString()) }


    LaunchedEffect(noteState) {
        if (noteState.isSuccess) {
            navController.navigate(HomeScreens.Details(
                noteId = uuid.toString()
            )
            )
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
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onPrimary
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
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.onPrimary)
                .padding(innerPadding),
        ) {

            noteState.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp).align(Alignment.Center)
                )
            }

            Column {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier
                        .fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Note Title",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    },
                    textStyle = MaterialTheme.typography.titleLarge.copy(
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
}