package com.example.notemark.main.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.notemark.R
import com.example.notemark.auth.presentation.login.vm.LoginViewModel
import com.example.notemark.auth.presentation.login.vm.LogoutState
import com.example.notemark.auth.presentation.util.DeviceConfiguration
import com.example.notemark.core.manager.SessionManager
import com.example.notemark.main.presentation.vm.NotesViewModel
import com.example.notemark.navigation.screens.AuthScreens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController = rememberNavController()
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)

    when(deviceConfiguration) {
        DeviceConfiguration.MOBILE_PORTRAIT -> {
            SettingsItem(
                navController,

            )
        }
        DeviceConfiguration.MOBILE_LANDSCAPE -> {
            SettingsItem(
                navController,

            )
        }
        DeviceConfiguration.TABLET_PORTRAIT,
        DeviceConfiguration.TABLET_LANDSCAPE,
        DeviceConfiguration.DESKTOP -> {
            SettingsItem(
                navController,
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SettingsItem(
    navController: NavHostController,
) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    val refreshToken = sessionManager.getRefreshToken()
    val loginViewModel: LoginViewModel = hiltViewModel()
    val notesViewModel: NotesViewModel = hiltViewModel()
    var showSyncIntervalDropdown by remember { mutableStateOf(false) }
    val logoutState by loginViewModel.logoutState.collectAsStateWithLifecycle()
    var selectedSyncInterval by remember { mutableStateOf(SyncInterval.MANUAL_ONLY) }

    LaunchedEffect(logoutState) {
        when (logoutState) {
            is LogoutState.Error -> {
                Toast.makeText(
                    context,
                    (logoutState as LogoutState.Error).message,
                    Toast.LENGTH_SHORT
                ).show()
                loginViewModel.clearState()
            }

            is LogoutState.Success -> {
                notesViewModel.clear()
                navController.navigate(AuthScreens.Landing.route) {
                    popUpTo(0) {
                        inclusive = true
                    }
                }
                loginViewModel.clearState()
            }
            LogoutState.Loading,
            LogoutState.Idle -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings).uppercase(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        letterSpacing = 1.0.sp,
                        fontFamily = FontFamily(Font(R.font.space_grotesk_regular)),
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigateUp() }
                    ) {
                        Icon(
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            imageVector = ImageVector.vectorResource(R.drawable.ios_arrow_left),
                            contentDescription = "Navigate up"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(innerPadding)
        ) {

            if (logoutState is LogoutState.Loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                SettingsContent(
                    onLogoutClick = {
                        loginViewModel.logoutUser(refreshToken = refreshToken ?: "")
                    },
                    onSyncClick = {
                        Toast.makeText(
                            context,
                            "This page is not available yet!",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onSyncIntervalClick = {
                        showSyncIntervalDropdown = true
                    },
                    showSyncIntervalDropdown = showSyncIntervalDropdown,
                    onDismissSyncIntervalDropdown = {
                        showSyncIntervalDropdown = false
                    },
                    selectedSyncInterval = selectedSyncInterval,
                    onSyncIntervalSelected = { interval ->
                        selectedSyncInterval = interval
                        showSyncIntervalDropdown = false
                    }
                )
            }
        }
    }
}

// Enum for sync intervals
enum class SyncInterval(val displayName: String) {
    MANUAL_ONLY("Manual Only"),
    EVERY_15_MINUTES("15 minutes"),
    EVERY_30_MINUTES("30 minutes"),
    EVERY_HOUR("1 hour")
}

@Composable
private fun SettingsContent(
    onLogoutClick: () -> Unit,
    onSyncClick: () -> Unit,
    onSyncIntervalClick: () -> Unit,
    showSyncIntervalDropdown: Boolean,
    onDismissSyncIntervalDropdown: () -> Unit,
    selectedSyncInterval: SyncInterval,
    onSyncIntervalSelected: (SyncInterval) -> Unit
) {
    SingleItem(
        onEndIconAndTextClick = onSyncIntervalClick,
        supportingText = null,
        headline = stringResource(R.string.sync_interval),
        headlineTextColor = MaterialTheme.colorScheme.onSurface,
        leadingIcon = ImageVector.vectorResource(R.drawable.clock),
        iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
        endIcon = ImageVector.vectorResource(R.drawable.chevron_right),
        endIconTint = MaterialTheme.colorScheme.onSurfaceVariant,
        endText = selectedSyncInterval.displayName,
        endTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        showDropdown = showSyncIntervalDropdown,
        onDismissDropdown = onDismissSyncIntervalDropdown,
        dropdownContent = {
            SyncInterval.entries.forEach { interval ->
                DropdownMenuItem(
                    modifier = Modifier
                        .width(190.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = interval.displayName
                            )

                            if (interval == selectedSyncInterval) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.check),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    },
                    onClick = {
                        onSyncIntervalSelected(interval)
                    }
                )
            }
        }
    )

    SingleItem(
        onClick = onSyncClick,
        supportingText = stringResource(R.string.last_sync),
        headline = stringResource(R.string.sync_data),
        headlineTextColor = MaterialTheme.colorScheme.onSurface,
        supportingTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        leadingIcon = ImageVector.vectorResource(R.drawable.sync_icon),
        iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
        endIcon = null,
        endIconTint = null,
        endText = null,
        endTextColor = null
    )

    SingleItem(
        onClick = onLogoutClick,
        headline = stringResource(R.string.logout),
        supportingText = null,
        headlineTextColor = MaterialTheme.colorScheme.error,
        supportingTextColor = null,
        leadingIcon = ImageVector.vectorResource(R.drawable.logout),
        iconTint = MaterialTheme.colorScheme.error,
        endIcon = null,
        endIconTint = null,
        endText = null,
        endTextColor = null
    )
}

@Composable
private fun SingleItem(
    modifier: Modifier = Modifier,
    onEndIconAndTextClick: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    supportingText: String? = null,
    supportingTextColor: Color? = null,
    headline: String,
    headlineTextColor: Color,
    leadingIcon: ImageVector? = null,
    iconTint: Color? = null,
    endIcon: ImageVector? = null,
    endIconTint: Color? = null,
    endText: String? = null,
    endTextColor: Color? = null,
    showDropdown: Boolean = false,
    onDismissDropdown: (() -> Unit)? = null,
    dropdownContent: (@Composable ColumnScope.() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        leadingIcon?.let { icon ->
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint ?: MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = headline,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = headlineTextColor
                )
            )

            if (!supportingText.isNullOrBlank()) {
                Text(
                    text = supportingText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = supportingTextColor ?: MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        val hasEndContent = !endText.isNullOrBlank() || endIcon != null
        if (hasEndContent) {
            Box {
                Row(
                    modifier = Modifier.then(
                        if (onEndIconAndTextClick != null) {
                            Modifier.clickable { onEndIconAndTextClick() }
                        } else {
                            Modifier
                        }
                    ),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!endText.isNullOrBlank()) {
                        Text(
                            text = endText,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = endTextColor ?: MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    endIcon?.let { icon ->
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = endIconTint ?: MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (dropdownContent != null) {
                    DropdownMenu(
                        expanded = showDropdown,
                        onDismissRequest = onDismissDropdown ?: {}
                    ) {
                        dropdownContent()
                    }
                }
            }
        }
    }
}