package com.example.notemark.main.presentation.screens.note

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.notemark.R
import com.example.notemark.auth.presentation.util.DeviceConfiguration
import com.example.notemark.main.domain.model.Note
import com.example.notemark.main.domain.model.getFormattedUpdatedAt
import com.example.notemark.main.presentation.vm.DetailsScreenUiState
import com.example.notemark.main.presentation.vm.DetailsViewModel
import com.example.notemark.main.presentation.vm.NotesViewModel
import com.example.notemark.navigation.screens.HomeScreens

@Preview(showBackground = true, showSystemUi = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    navController: NavHostController = rememberNavController(),
    noteId: String? = null
) {
    val context = LocalContext.current
    val activity = context as Activity
    val scrollState = rememberScrollState()
    val viewModel: DetailsViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val notesViewModel: NotesViewModel = hiltViewModel()
    val notes = notesViewModel.notePagingFlow.collectAsLazyPagingItems()
    val note = notes.itemSnapshotList.items.find { it.id == noteId }

    LaunchedEffect(uiState.requestedOrientation) {
        uiState.requestedOrientation?.let { orientation ->
            activity.requestedOrientation = orientation
            viewModel.onOrientationHandled()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.setOriginalOrientation(activity.requestedOrientation)
    }

    BackHandler(enabled = uiState.isReaderMode) {
        viewModel.toggleReaderMode()
    }

    LaunchedEffect(scrollState.value) {
        if (scrollState.isScrollInProgress) {
            viewModel.onScrollDetected()
        }
    }

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)

    when(deviceConfiguration) {
        DeviceConfiguration.MOBILE_PORTRAIT -> {
            Scaffold(
                topBar = {
                    AnimatedVisibility(
                        visible = !uiState.isReaderMode || uiState.isUiVisible,
                        enter = fadeIn(animationSpec = tween(300)) + slideInVertically(
                            animationSpec = tween(300)
                        ) { -it },
                        exit = fadeOut(animationSpec = tween(300)) + slideOutVertically(
                            animationSpec = tween(300)
                        ) { -it }
                    ) {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "all notes".uppercase(),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 16.sp,
                                    lineHeight = 24.sp,
                                    letterSpacing = 1.0.sp,
                                    fontFamily = FontFamily(Font(R.font.space_grotesk_regular)),
                                )
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        if (uiState.isReaderMode) viewModel.toggleReaderMode() else navController.popBackStack()
                                    }
                                ) {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.ios_arrow_left),
                                        contentDescription = "Navigate up",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        )
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.onPrimary)
                        .padding(
                            if (!uiState.isReaderMode || uiState.isUiVisible) innerPadding else PaddingValues(
                                0.dp
                            )
                        )
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            viewModel.onScreenTap()
                        },
                ) {
                    DetailsBody(
                        scrollState = scrollState,
                        uiState = uiState,
                        note = note,
                        noteId = noteId ?: ""
                    )

                    AnimatedVisibility(
                        visible = !uiState.isReaderMode || uiState.isUiVisible,
                        enter = fadeIn(animationSpec = tween(300)) + slideInVertically(
                            animationSpec = tween(300)
                        ) { it },
                        exit = fadeOut(animationSpec = tween(300)) + slideOutVertically(
                            animationSpec = tween(300)
                        ) { it },
                        modifier = Modifier.align(Alignment.BottomCenter)
                    ) {
                        ExtendedFabFSheet(
                            onEditClick = {
                                navController.navigate(
                                    HomeScreens.EditNote(
                                        noteId = noteId ?: ""
                                    )
                                )
                            },
                            onReaderModeClick = { viewModel.toggleReaderMode() },
                            isReaderMode = uiState.isReaderMode
                        )
                    }
                }
            }
        }
        DeviceConfiguration.MOBILE_LANDSCAPE -> {

            Scaffold(
                topBar = {
                    AnimatedVisibility(
                        visible = !uiState.isReaderMode || uiState.isUiVisible,
                        enter = fadeIn(animationSpec = tween(300)) + slideInVertically(
                            animationSpec = tween(300)
                        ) { -it },
                        exit = fadeOut(animationSpec = tween(300)) + slideOutVertically(
                            animationSpec = tween(300)
                        ) { -it }
                    ) {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "all notes".uppercase(),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 16.sp,
                                    lineHeight = 24.sp,
                                    letterSpacing = 1.0.sp,
                                    fontFamily = FontFamily(Font(R.font.space_grotesk_regular)),
                                )
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        if (uiState.isReaderMode) viewModel.toggleReaderMode() else navController.popBackStack()
                                    }
                                ) {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.ios_arrow_left),
                                        contentDescription = "Navigate up",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        )
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.onPrimary)
                        .windowInsetsPadding(WindowInsets.displayCutout)
                        .consumeWindowInsets(WindowInsets.navigationBars)
                        .padding(if (!uiState.isReaderMode || uiState.isUiVisible) innerPadding else PaddingValues(0.dp))
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            viewModel.onScreenTap()
                        },
                ) {
                    DetailsBody(
                        scrollState = scrollState,
                        uiState = uiState,
                        note = note,
                        noteId = noteId ?: ""
                    )

                    AnimatedVisibility(
                        visible = !uiState.isReaderMode || uiState.isUiVisible,
                        enter = fadeIn(animationSpec = tween(300)) + slideInVertically(
                            animationSpec = tween(300)
                        ) { it },
                        exit = fadeOut(animationSpec = tween(300)) + slideOutVertically(
                            animationSpec = tween(300)
                        ) { it },
                        modifier = Modifier.align(Alignment.BottomCenter)
                    ) {
                        ExtendedFabFSheet(
                            onEditClick = {
                                navController.navigate(
                                    HomeScreens.EditNote(
                                        noteId = noteId ?: ""
                                    )
                                )
                            },
                            onReaderModeClick = { viewModel.toggleReaderMode() },
                            isReaderMode = uiState.isReaderMode
                        )
                    }
                }
            }
        }
        DeviceConfiguration.TABLET_PORTRAIT,
        DeviceConfiguration.TABLET_LANDSCAPE,
        DeviceConfiguration.DESKTOP -> {
            Scaffold(
                topBar = {
                    AnimatedVisibility(
                        visible = !uiState.isReaderMode || uiState.isUiVisible,
                        enter = fadeIn(animationSpec = tween(300)) + slideInVertically(
                            animationSpec = tween(300)
                        ) { -it },
                        exit = fadeOut(animationSpec = tween(300)) + slideOutVertically(
                            animationSpec = tween(300)
                        ) { -it }
                    ) {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "all notes".uppercase(),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 16.sp,
                                    lineHeight = 24.sp,
                                    letterSpacing = 1.0.sp,
                                    fontFamily = FontFamily(Font(R.font.space_grotesk_regular)),
                                )
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        if (uiState.isReaderMode) viewModel.toggleReaderMode() else navController.popBackStack()
                                    }
                                ) {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.ios_arrow_left),
                                        contentDescription = "Navigate up",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        )
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.onPrimary)
                        .padding(
                            if (!uiState.isReaderMode || uiState.isUiVisible) innerPadding else PaddingValues(
                                0.dp
                            )
                        )
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            viewModel.onScreenTap()
                        },
                ) {
                    DetailsBody(
                        scrollState = scrollState,
                        uiState = uiState,
                        note = note,
                        noteId = noteId ?: ""
                    )

                    AnimatedVisibility(
                        visible = !uiState.isReaderMode || uiState.isUiVisible,
                        enter = fadeIn(animationSpec = tween(300)) + slideInVertically(
                            animationSpec = tween(300)
                        ) { it },
                        exit = fadeOut(animationSpec = tween(300)) + slideOutVertically(
                            animationSpec = tween(300)
                        ) { it },
                        modifier = Modifier.align(Alignment.BottomCenter)
                    ) {
                        ExtendedFabFSheet(
                            onEditClick = {
                                navController.navigate(
                                    HomeScreens.EditNote(
                                        noteId = noteId ?: ""
                                    )
                                )
                            },
                            onReaderModeClick = { viewModel.toggleReaderMode() },
                            isReaderMode = uiState.isReaderMode
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailsBody(
    scrollState: ScrollState,
    uiState: DetailsScreenUiState,
    note: Note?,
    noteId: String
) {
    if (note != null && note.id == noteId) {
        Column(
            modifier = Modifier.verticalScroll(scrollState)
        ) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            HorizontalDivider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Date created",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    )
                    Text(
                        text = note.getFormattedUpdatedAt(),
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Last edited",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    )
                    Text(
                        text = note.getFormattedUpdatedAt(),
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            HorizontalDivider()

            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        if (uiState.isReaderMode && !uiState.isUiVisible) {
                            PaddingValues(horizontal = 24.dp, vertical = 32.dp)
                        } else {
                            PaddingValues(16.dp)
                        }
                    )
            )
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
internal fun ExtendedFabFSheet(
    modifier: Modifier = Modifier,
    onEditClick: () -> Unit,
    onReaderModeClick: () -> Unit,
    isReaderMode: Boolean = false
) {
    Row(
        modifier = modifier
            .padding(16.dp)
            .background(
                MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.medium
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = onEditClick
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.edit),
                contentDescription = null
            )
        }

        IconButton(
            onClick = onReaderModeClick,
            modifier = Modifier
                .then(
                    if (isReaderMode) {
                        Modifier
                            .padding(4.dp)
                            .background(
                            color = Color(0x1A5977F7),
                            shape = MaterialTheme.shapes.medium
                        )
                    } else {
                        Modifier
                    }
                )
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.book_open),
                contentDescription = null,
                tint = if (isReaderMode) MaterialTheme.colorScheme.primary else LocalContentColor.current
            )
        }
    }
}