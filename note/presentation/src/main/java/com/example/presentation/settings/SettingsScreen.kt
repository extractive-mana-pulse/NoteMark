package com.example.presentation.settings

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import com.example.presentation.DeviceConfiguration
import com.example.presentation.settings.components.SettingsItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateUp: () -> Unit = {},
    onNavigateToLanding: () -> Unit = {}
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)

    when(deviceConfiguration) {
        DeviceConfiguration.MOBILE_PORTRAIT -> {
            SettingsItem(
                onNavigateUp = onNavigateUp,
                onNavigateToLanding = onNavigateToLanding
            )
        }
        DeviceConfiguration.MOBILE_LANDSCAPE -> {
            SettingsItem(
                onNavigateUp = onNavigateUp,
                onNavigateToLanding = onNavigateToLanding
            )
        }
        DeviceConfiguration.TABLET_PORTRAIT,
        DeviceConfiguration.TABLET_LANDSCAPE,
        DeviceConfiguration.DESKTOP -> {
            SettingsItem(
                onNavigateUp = onNavigateUp,
                onNavigateToLanding = onNavigateToLanding
            )
        }
    }
}