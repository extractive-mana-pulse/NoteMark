package com.example.notemark.main.presentation.screens.note

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.BottomSheetDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
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
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.notemark.R
import com.example.notemark.auth.presentation.util.DeviceConfiguration
import com.example.notemark.core.manager.SessionManager
import com.example.notemark.main.domain.model.Note
import com.example.notemark.main.domain.model.NotesUiState
import com.example.notemark.main.domain.model.getFormattedCreatedAt
import com.example.notemark.main.presentation.vm.DeleteNoteUiState
import com.example.notemark.main.presentation.vm.MainViewModel
import com.example.notemark.navigation.screens.AuthScreens
import com.example.notemark.navigation.screens.HomeScreens
import kotlinx.coroutines.delay

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
    val username = sessionManager.getUserName()
    val accessToken = sessionManager.getAccessToken()
    val refreshToken = sessionManager.getRefreshToken()

    LaunchedEffect(
        Unit
    ) {
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
                navController,
                context,
                username,
                listState,
                uiState,
                viewModel
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
                navController,
                context,
                username,
                listState,
                uiState,
                viewModel
            )
        }
        DeviceConfiguration.TABLET_PORTRAIT,
        DeviceConfiguration.TABLET_LANDSCAPE,
        DeviceConfiguration.DESKTOP -> {
            MainContent(
                modifier = Modifier,
                navController,
                context,
                username,
                listState,
                uiState,
                viewModel
            )
        }
    }

}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun MainContent(
    modifier: Modifier,
    navController: NavHostController,
    context: Context,
    username: String?,
    listState: LazyListState,
    uiState: NotesUiState,
    viewModel: MainViewModel
) {
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

            LaunchedEffect(listState) {
                snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                    .collect { lastVisibleIndex ->
                        if (lastVisibleIndex != null &&
                            lastVisibleIndex >= uiState.notes.size - 3 &&
                            !uiState.isLoading &&
                            uiState.hasNextPage
                        ) {
                            viewModel.loadNextPage()
                        }
                    }
            }

            uiState.error?.let { error ->
                Toast.makeText(context, "Error message: $error", Toast.LENGTH_SHORT).show()
                Log.e("HomeScreen's:", "Error message: $error")
            }

            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                state = rememberLazyStaggeredGridState(),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalItemSpacing = 8.dp,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(uiState.notes) { index, note ->
                    NoteItem(
                        navController = navController,
                        note = note,
                        modifier = Modifier,
                        viewModel = viewModel
                    )
                }

                if (uiState.isLoading && uiState.notes.isNotEmpty()) {
                    item(span = StaggeredGridItemSpan.SingleLane) {
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
    note: Note,
    viewModel: MainViewModel
) {

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
            style = MaterialTheme.typography.titleMedium,
        )

        Text(
            text = note.content,
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
            viewModel = viewModel,
            noteId = note.id
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteActionSheet(
    isVisible: Boolean,
    onDismissSheet: () -> Unit,
    viewModel: MainViewModel,
    noteId: String
) {
    val context = LocalContext.current
    val deleteNoteState by viewModel.deleteNoteState.collectAsStateWithLifecycle()
    val onDeleteClick = { viewModel.deleteNote(noteId = noteId) }

    LaunchedEffect(deleteNoteState) {
        when (deleteNoteState) {
            is DeleteNoteUiState.Success -> {
                viewModel.resetDeleteState()
                onDismissSheet()
            }
            is DeleteNoteUiState.Error -> {
                Log.e("NoteActionSheet", (deleteNoteState as DeleteNoteUiState.Error).message)
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
                    onDeleteClick = onDeleteClick
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

        Text(
            text = "Your note has been permanently removed",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun ActionContent(
    deleteNoteState: DeleteNoteUiState,
    onDeleteClick: () -> Unit
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
                            else -> "Delete Note"
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
                        text = "This action cannot be undone",
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