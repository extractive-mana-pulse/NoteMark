package com.example.presentation.settings.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.presentation.NotesViewModel
import com.example.notemark.core.presentation.R
import com.example.presentation.SyncInterval
import com.example.presentation.login.LoginViewModel
import com.example.presentation.login.LogoutState

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun SettingsItem(
    onNavigateUp: () -> Unit = {},
    onNavigateToLanding: () -> Unit = {},
) {
    val context = LocalContext.current
    val loginViewModel: LoginViewModel = hiltViewModel()
    val notesViewModel: NotesViewModel = hiltViewModel()
    var showSyncIntervalDropdown by remember { mutableStateOf(false) }
    val logoutState by loginViewModel.logoutState.collectAsStateWithLifecycle()
    var selectedSyncInterval by remember { mutableStateOf(SyncInterval.MANUAL_ONLY) }
    val refreshToken by notesViewModel.refreshToken.collectAsStateWithLifecycle()

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
                onNavigateToLanding()
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
                        onClick = { onNavigateUp() }
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