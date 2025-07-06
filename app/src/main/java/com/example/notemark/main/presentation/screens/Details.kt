package com.example.notemark.main.presentation.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.notemark.R
import com.example.notemark.main.presentation.vm.DetailsScreenViewModel
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
    val viewModel: DetailsScreenViewModel = viewModel()
    var isReaderMode by remember { mutableStateOf(false) }
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
                            modifier = Modifier.clickable {
                                if (uiState.isReaderMode) navController.popBackStack()
                            }
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { navController.popBackStack() }
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
            Column(
                modifier = Modifier.verticalScroll(scrollState)
            ) {
                Text(
                    text = "Note Title",
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
                            text = "26 Sep 2024, 18:54",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                            text = "Just now",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        )
                    }
                }

                HorizontalDivider()

                Text(
                    text = "Amet minim mollit non deserunt ullamco est sit aliqua dolor do amet sint. Velit officia consequat duis enim velit mollit. Exercitation veniam consequat sunt nostrud amet. Amet minim mollit non deserunt ullamco est sit aliqua dolor do amet sint. Velit officia consequat duis enim velit mollit. Exercitation veniam consequat sunt nostrud amet. Amet minim mollit non deserunt ullamco est sit aliqua dolor do amet sint. Velit officia consequat duis enim velit mollit. Exercitation veniam consequat sunt nostrud amet.",
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
                        navController.navigate(HomeScreens.Details(
                            noteId = noteId ?: ""
                        ))
                    },
                    onReaderModeClick = { viewModel.toggleReaderMode() },
                    isReaderMode = isReaderMode
                )
            }
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
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = onEditClick,
            modifier = Modifier
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
                        Modifier.background(
                            MaterialTheme.colorScheme.primaryContainer,
                            shape = CircleShape
                        )
                    } else {
                        Modifier
                    }
                )
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.book_open),
                contentDescription = null,
                tint = if (isReaderMode) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    LocalContentColor.current
                }
            )
        }
    }
}