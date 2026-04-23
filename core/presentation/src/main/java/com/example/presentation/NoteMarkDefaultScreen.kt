package com.example.presentation

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsControllerCompat

@Composable
fun NoteMarkDefaultScreen(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.background,
    bottomBar: @Composable () -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    Scaffold(
        bottomBar = { bottomBar() },
        modifier = modifier,
        containerColor = containerColor,
    ) { innerPadding ->
        val view = LocalView.current
        Box(
            modifier =
                Modifier
                    .padding(innerPadding),
        ) {
            content()
        }

        SideEffect {
            val window = (view.context as? Activity)?.window
            if (!view.isInEditMode && window != null) {
                val isLightBackground = containerColor.luminance() > 0.5f

                WindowInsetsControllerCompat(window, window.decorView).apply {
                    isAppearanceLightStatusBars = isLightBackground
                }
            }
        }
    }
}