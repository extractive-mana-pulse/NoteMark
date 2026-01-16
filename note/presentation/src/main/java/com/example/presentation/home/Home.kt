package com.example.presentation.home

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.example.presentation.DateFormatter
import com.example.presentation.DeviceConfiguration
import com.example.presentation.NoteUiState
import com.example.presentation.NotesViewModel
import com.example.notemark.core.presentation.R
import com.example.domain.Note
import com.example.domain.NoteRequest
import com.example.presentation.getInitials
import com.example.presentation.home.components.EmptyState
import com.example.presentation.home.components.NoteItem
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    connectivityState: State<Boolean>,
    notesList: LazyPagingItems<Note>,
    noteUiState: State<NoteUiState>,
    onNavigateToDetailsWithId: (String) -> Unit,
    onNavigateToCreateNote: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToHome: () -> Unit,
    username: String?,
    accessToken: String?,
    refreshToken: String?
    ) {

    LaunchedEffect(Unit) { if (accessToken.isNullOrEmpty() && refreshToken.isNullOrEmpty()) { onNavigateToLogin() } }

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)

    when(deviceConfiguration) {
        DeviceConfiguration.MOBILE_PORTRAIT -> {
            MainContent(
                modifier = Modifier,
                onNavigateToDetailsWithId = onNavigateToDetailsWithId,
                onNavigateToCreateNote = onNavigateToCreateNote,
                onNavigateToSettings = onNavigateToSettings,
                onNavigateToHome = onNavigateToHome,
                username = username,
                notes = notesList,
                connectivityState = connectivityState,
                columnCount = 2
            )
        }
        DeviceConfiguration.MOBILE_LANDSCAPE -> {
            MainContent(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surface
                    )
                    .windowInsetsPadding(WindowInsets.displayCutout)
                    .fillMaxSize()
                    .consumeWindowInsets(WindowInsets.navigationBars),
                onNavigateToDetailsWithId = onNavigateToDetailsWithId,
                onNavigateToCreateNote = onNavigateToCreateNote,
                onNavigateToSettings = onNavigateToSettings,
                onNavigateToHome = onNavigateToHome,
                username = username,
                notes = notesList,
                connectivityState = connectivityState,
                columnCount = 3
            )
        }
        DeviceConfiguration.TABLET_PORTRAIT,
        DeviceConfiguration.TABLET_LANDSCAPE,
        DeviceConfiguration.DESKTOP -> {
            MainContent(
                modifier = Modifier,
                onNavigateToDetailsWithId = onNavigateToDetailsWithId,
                onNavigateToCreateNote = onNavigateToCreateNote,
                onNavigateToSettings = onNavigateToSettings,
                onNavigateToHome = onNavigateToHome,
                username = username,
                notes = notesList,
                connectivityState = connectivityState,
                columnCount = 3
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainContent(
    modifier: Modifier,
    onNavigateToDetailsWithId: (String) -> Unit = {},
    onNavigateToCreateNote: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    username: String?,
    notes: LazyPagingItems<Note>,
    connectivityState: State<Boolean>,
    columnCount: Int,
) {
    val context = LocalContext.current
    val uuid = remember { UUID.randomUUID() }
    val noteViewModel = hiltViewModel<NotesViewModel>()
    val noteState by noteViewModel.createNoteState.collectAsStateWithLifecycle()
    val creationTime by remember { mutableStateOf(DateFormatter.getCurrentIsoString()) }

    LaunchedEffect(notes.loadState) {
        when {
            notes.loadState.refresh is LoadState.Error -> {
                val error = (notes.loadState.refresh as LoadState.Error).error
                Log.e("Home", "Refresh error: ${error.message}")
                if (notes.itemCount == 0) {
                    Toast.makeText(
                        context,
                        "Unable to load notes. Showing cached data.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            notes.loadState.append is LoadState.Error -> {
                val error = (notes.loadState.append as LoadState.Error).error
                Log.e("Home", "Append error: ${error.message}")
            }
        }
    }

    val initials = getInitials(username ?: "U")

    LaunchedEffect(noteState) { if (noteState.isSuccess) onNavigateToHome() }

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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        if (!connectivityState.value) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.cloud_off),
                                contentDescription = "Offline",
                                tint = Color(0x66535364),
                            )
                        }
                    }
                },
                actions = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.settings),
                        contentDescription = "Settings",
                        modifier = Modifier
                            .clickable { onNavigateToSettings() }
                            .padding(end = 16.dp)
                    )
                    Text(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(10.dp),
                        text = initials,
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
                    noteViewModel.createNote(
                        NoteRequest(
                            id = uuid.toString(),
                            title = "Note Title",
                            content = "",
                            createdAt = creationTime,
                            updatedAt = creationTime,
                        )
                    )
                    onNavigateToCreateNote()
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
                    painter = painterResource(R.drawable.plus),
                    contentDescription = null
                )
            }
        },
        contentWindowInsets = WindowInsets.statusBars
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(innerPadding)
        ) {
            if (notes.loadState.refresh is LoadState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.CenterHorizontally)
                )
            } else {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(columnCount),
                    state = rememberLazyStaggeredGridState(),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalItemSpacing = 8.dp,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    content = {
                        items(
                            notes.itemCount,
                            key = notes.itemKey { it.id }
                        ) { index ->
                            notes[index]?.let {
                                NoteItem(
                                    modifier = Modifier,
                                    onNavigateToDetailsWithId = { noteId ->
                                        onNavigateToDetailsWithId(noteId)
                                    },
                                    note = it,
                                    notes = notes
                                )
                            }
                        }
                        if (notes.loadState.append is LoadState.Loading) {
                            item(span = StaggeredGridItemSpan.FullLine) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                )
            }
            EmptyState(notes)
        }
    }
}