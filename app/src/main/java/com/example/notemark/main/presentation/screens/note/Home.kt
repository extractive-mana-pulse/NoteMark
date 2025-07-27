package com.example.notemark.main.presentation.screens.note

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.notemark.R
import com.example.notemark.auth.presentation.util.DeviceConfiguration
import com.example.notemark.core.manager.SessionManager
import com.example.notemark.core.truncateAtWord
import com.example.notemark.main.domain.model.Note
import com.example.notemark.main.domain.model.getFormattedCreatedAt
import com.example.notemark.main.presentation.vm.DeleteNoteUiState
import com.example.notemark.main.presentation.vm.NotesViewModel
import com.example.notemark.navigation.screens.AuthScreens
import com.example.notemark.navigation.screens.HomeScreens
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController = rememberNavController(),
    connectivityState: State<Boolean>,
    notesList: LazyPagingItems<Note>
) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    val username = sessionManager.getUserName()
    val accessToken = sessionManager.getAccessToken()
    val refreshToken = sessionManager.getRefreshToken()

    LaunchedEffect(Unit) {
        Log.d("HomeScreen", "Username: ${sessionManager.getUserName()}")
        Log.d("HomeScreen", "Access Token: ${sessionManager.getAccessToken()}")
        Log.d("HomeScreen", "Refresh Token: ${sessionManager.getRefreshToken()}")
    }

    LaunchedEffect(Unit) {
        if (accessToken.isNullOrEmpty() && refreshToken.isNullOrEmpty()) {
            navController.navigate(AuthScreens.LogIn.route) {
                popUpTo(0) {
                    inclusive
                }
            }
        }
    }

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)

    when(deviceConfiguration) {
        DeviceConfiguration.MOBILE_PORTRAIT -> {
            MainContent(
                modifier = Modifier,
                navController = navController,
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
                navController = navController,
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
                navController = navController,
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
    navController: NavHostController,
    username: String?,
    notes: LazyPagingItems<Note>,
    connectivityState: State<Boolean>,
    columnCount: Int
) {
    val context = LocalContext.current
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
                            },
                        text = "${username?.take(2)}",
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
                                    navController = navController,
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

@Composable
private fun EmptyState(notes: LazyPagingItems<Note>) {

    if (notes.loadState.refresh !is LoadState.Loading && notes.itemSnapshotList.items.isEmpty() && !notes.loadState.hasError) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 48.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = stringResource(R.string.empty_container_text),
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
    note: Note,
    notes: LazyPagingItems<Note>
) {

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)
    val isPhone = deviceConfiguration == DeviceConfiguration.MOBILE_PORTRAIT

    var contextMenuNoteId by rememberSaveable { mutableStateOf<String?>(null) }
    val haptics = LocalHapticFeedback.current

    Column(
        modifier = modifier
            .combinedClickable(
                onClick = {
                    navController.navigate(
                        route = HomeScreens.Details(
                            noteId = note.id
                        )
                    )
                },
                onLongClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    contextMenuNoteId = note.id
                },
            )
            .background(
                MaterialTheme.colorScheme.surfaceContainerLowest,
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
    ) {

        Text(
            text = note.getFormattedCreatedAt(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = note.title,
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Text(
            text = note.content.truncateAtWord(if (isPhone) 150 else 250),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    if (contextMenuNoteId != null) {
        NoteActionSheet(
            isVisible = note.id == contextMenuNoteId,
            onDismissSheet = { contextMenuNoteId = null },

            noteId = contextMenuNoteId ?: "",
            notes = notes
        )
    }
}

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

@Composable
private fun SuccessContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val animationState = rememberLottieAnimatable()
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.success))


        LaunchedEffect(composition) {
            composition?.let {
                animationState.animate(
                    composition = it,
                    iterations = 1
                )
            }
        }

        Box(
            modifier = Modifier.size(120.dp),
            contentAlignment = Alignment.Center
        ) {
            if (composition != null) {
                LottieAnimation(
                    composition = composition,
                    progress = { animationState.progress },
                    modifier = Modifier.size(120.dp)
                )
            } else {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Note deleted successfully!",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ActionContent(
    deleteNoteState: DeleteNoteUiState,
    onDeleteClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (deleteNoteState is DeleteNoteUiState.Success) {
            SuccessContent()
        } else {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .clickable(
                    enabled = deleteNoteState !is DeleteNoteUiState.Loading
                ) {
                    onDeleteClick()
                },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            ListItem(
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent
                ),
                headlineContent = {
                    Text(
                        text = when (deleteNoteState) {
                            is DeleteNoteUiState.Loading -> "Deleting..."
                            else -> "Delete Note?"
                        },
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = if (deleteNoteState is DeleteNoteUiState.Loading) {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            } else {
                                MaterialTheme.colorScheme.error
                            }
                        )
                    )
                },
                supportingContent = {
                    Text(
                        text = "Are you sure you want to delete this note? This action cannot be undone.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                leadingContent = {
                    Box(
                        modifier = Modifier.size(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (deleteNoteState is DeleteNoteUiState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        }
    }
}