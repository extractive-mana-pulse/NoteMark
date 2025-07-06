package com.example.notemark.main.presentation.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.notemark.R
import com.example.notemark.core.manager.SessionManager
import com.example.notemark.main.domain.model.Note
import com.example.notemark.main.domain.model.NotesUiState
import com.example.notemark.main.presentation.vm.MainViewModel
import com.example.notemark.navigation.screens.HomeScreens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val viewModel: MainViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sessionManager = SessionManager(context)

    Log.d("HomeScreen", "Token: ${sessionManager.getAccessToken()}")
    Log.d("HomeScreen", "Token: ${sessionManager.getRefreshToken()}")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                actions = {
//                    Icon(
//                        Icons.Default.Refresh,
//                        contentDescription = "Refresh",
//                        modifier = Modifier.clickable {
//                            viewModel.refreshNotes()
//                        }
//                    )
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.settings),
                        contentDescription = "Settings",
                        modifier = Modifier
                            .clickable {
                                navController.navigate(HomeScreens.Settings.route)
                            }
                            .padding(end = 16.dp)
                    )
                    Text(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(10.dp)
                            .clickable {
                                Toast.makeText(
                                    context,
                                    "This page is not available yet!",
                                    Toast.LENGTH_SHORT
                                ).show()
//                                navController.navigate(HomeScreens.Profile.route)
                            },
                        text = "PL",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = MaterialTheme.colorScheme.onPrimary,
                            textAlign = TextAlign.Center
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(HomeScreens.CreateNote.route)
                },
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF58A1F8),
                                Color(0xFF5A4CF7)
                            )
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ),
                containerColor = Color.Transparent,
                elevation = FloatingActionButtonDefaults.elevation(0.dp)
            ) {
                Icon(
                    tint = MaterialTheme.colorScheme.onPrimary,
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add icon"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(innerPadding)
        ) {
            // Infinite scroll logic
            LaunchedEffect(listState) {
                snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                    .collect { lastVisibleIndex ->
                        if (lastVisibleIndex != null &&
                            lastVisibleIndex >= uiState.notes.size - 3 &&
                            !uiState.isLoading &&
                            uiState.hasNextPage) {
                            viewModel.loadNextPage()
                        }
                    }
            }

            uiState.error?.let { error ->
                Toast.makeText(context, "Error message: $error", Toast.LENGTH_SHORT).show()
                Log.e("HomeScreen's:", "Error message: $error")
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                state = rememberLazyGridState(),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(uiState.notes) { index, note ->
                    NoteItem(
                        navController = navController,
                        note = note,
                        modifier = Modifier
                    )
                }

                // Loading indicator for pagination
                if (uiState.isLoading && uiState.notes.isNotEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }

            // Initial loading indicator
            if (uiState.isLoading && uiState.notes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            EmptyState(uiState)
        }
    }
}

@Composable
private fun EmptyState(uiState: NotesUiState) {
    if (!uiState.isLoading && uiState.notes.isEmpty() && uiState.error == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 48.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = "You’ve got an empty board,\n" +
                        "let’s place your first note on it!",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}

@Composable
fun NoteItem(
    modifier: Modifier,
    navController: NavHostController = rememberNavController(),
    note: Note
) {

    Column(
        modifier = modifier
            .clickable {
                navController.navigate(HomeScreens.Details(
                    noteId = note.id
                ))
            }
            .background(
                MaterialTheme.colorScheme.surfaceContainerLowest,
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
    ) {

        Text(
            text = note.createdAt ?: "01/01/2000",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = note.title,
            style = MaterialTheme.typography.titleMedium,
        )

        Text(
            text = note.content ?: "",
            style = MaterialTheme.typography.bodySmall,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NoteItemPreview() {
    NoteItem(
        modifier = Modifier
            .height(212.dp)
            .width(180.dp),
        note = Note(
            "1",
            "Title of the note",
            "Augue non mauris ante viverra ut arcu sed ut lectus interdum morbi sed leo purus gravida non id mi augue." +
                    "Augue non mauris ante viverra ut arcu sed ut lectus interdum morbi sed leo purus gravida non id mi augue.",
            "12/12/2023",
            "12/12/2023"
        )
    )
}