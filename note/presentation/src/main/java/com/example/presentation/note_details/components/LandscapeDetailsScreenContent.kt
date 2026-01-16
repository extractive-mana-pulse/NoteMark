package com.example.presentation.note_details.components

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.notemark.core.presentation.R
import com.example.domain.Note
import com.example.presentation.getFormattedUpdatedAt
import com.example.presentation.note_details.DetailsViewModel

@Composable
internal fun LandscapeDetailsScreenContent(
    modifier: Modifier,
    navController: NavHostController,
    note: Note?,
    noteId: String?
) {
    val context = LocalContext.current
    val activity = context as Activity
    val scrollState = rememberScrollState()
    val viewModel: DetailsViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Main content - this stays in place
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 80.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    viewModel.onScreenTap()
                }
        ) {
            // Left spacer to account for back button (keep content centered)
            Spacer(modifier = Modifier.width(64.dp)) // Adjust based on your back button width

            // Content area
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 24.dp, bottom = 80.dp) // Add bottom padding for FAB
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
                                    text = stringResource(R.string.date_created),
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
                            if (!note.getFormattedUpdatedAt().isEmpty()) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.last_edited),
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
                                        PaddingValues(horizontal = 16.dp, vertical = 32.dp)
                                    } else {
                                        PaddingValues(horizontal = 16.dp, vertical = 32.dp)
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
        }

        // Back button overlay - positioned absolutely at top-left
        AnimatedVisibility(
            visible = !uiState.isReaderMode || uiState.isUiVisible,
            enter = fadeIn(animationSpec = tween(300)) + slideInVertically(
                animationSpec = tween(300)
            ) { -it },
            exit = fadeOut(animationSpec = tween(300)) + slideOutVertically(
                animationSpec = tween(300)
            ) { -it },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
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

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = stringResource(R.string.all_notes).uppercase(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    letterSpacing = 1.0.sp,
                    fontFamily = FontFamily(Font(R.font.space_grotesk_regular)),
                )
            }
        }

        // FAB overlay - positioned absolutely at bottom-center
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
//                    navController.navigate(
//                        HomeScreens.EditNote(
//                            noteId = noteId ?: ""
//                        )
//                    )
                },
                onReaderModeClick = { viewModel.toggleReaderMode() },
                isReaderMode = uiState.isReaderMode
            )
        }
    }
}
