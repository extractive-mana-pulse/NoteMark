package com.example.presentation.note_details

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.LazyPagingItems
import com.example.domain.Note
import com.example.presentation.DeviceConfiguration
import com.example.presentation.note_details.components.DetailsContent
import com.example.presentation.note_details.components.LandscapeDetailsScreenContent
import kotlin.collections.find

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    navController: NavHostController = rememberNavController(),
    noteId: String? = null,
    notes: LazyPagingItems<Note>
) {
    val note = notes.itemSnapshotList.items.find { it.id == noteId }
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)

    when(deviceConfiguration) {
        DeviceConfiguration.MOBILE_PORTRAIT -> {
            DetailsContent(
                modifier = Modifier,
                navController = navController,
                note = note,
                noteId = noteId,
            )
        }
        DeviceConfiguration.MOBILE_LANDSCAPE -> {
            LandscapeDetailsScreenContent(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.displayCutout)
                    .consumeWindowInsets(WindowInsets.navigationBars),
                navController = navController,
                note = note,
                noteId = noteId,
            )
        }
        DeviceConfiguration.TABLET_PORTRAIT,
        DeviceConfiguration.TABLET_LANDSCAPE -> {
            LandscapeDetailsScreenContent(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.displayCutout)
                    .consumeWindowInsets(WindowInsets.navigationBars),
                navController = navController,
                note = note,
                noteId = noteId,
            )
        }
        DeviceConfiguration.DESKTOP -> {
            DetailsContent(
                modifier = Modifier,
                navController = navController,
                note = note,
                noteId = noteId,
            )
        }
    }
}