package com.example.notemark.main.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
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
import com.example.notemark.auth.presentation.vm.AuthViewModel
import com.example.notemark.navigation.screens.AuthScreens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController = rememberNavController()
) {
    val authViewModel: AuthViewModel = hiltViewModel()

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)

    when(deviceConfiguration) {
        DeviceConfiguration.MOBILE_PORTRAIT -> {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Settings".uppercase(),
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
                                    navController.navigateUp()
                                }
                            ) {
                                Icon(
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    imageVector = ImageVector.vectorResource(R.drawable.ios_arrow_left),
                                    contentDescription = "Navigate Up"
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.onPrimary)
                        .padding(innerPadding)
                ) {
                    SettingsItem(
                        authViewModel,
                        navController
                    )
                }
            }
        }
        DeviceConfiguration.MOBILE_LANDSCAPE -> {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Settings".uppercase(),
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
                                    navController.navigateUp()
                                }
                            ) {
                                Icon(
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    imageVector = ImageVector.vectorResource(R.drawable.ios_arrow_left),
                                    contentDescription = "Navigate Up"
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.onPrimary)
                        .padding(innerPadding)
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.displayCutout)
                ) {
                    SettingsItem(
                        authViewModel,
                        navController
                    )
                }
            }
        }
        DeviceConfiguration.TABLET_PORTRAIT,
        DeviceConfiguration.TABLET_LANDSCAPE,
        DeviceConfiguration.DESKTOP -> {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Settings".uppercase(),
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
                                    navController.navigateUp()
                                }
                            ) {
                                Icon(
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    imageVector = ImageVector.vectorResource(R.drawable.ios_arrow_left),
                                    contentDescription = "Navigate Up"
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.onPrimary)
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    SettingsItem(
                        authViewModel,
                        navController
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsItem(
    authViewModel: AuthViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current
    val loginViewModel: LoginViewModel = hiltViewModel()
    val logoutState by loginViewModel.logoutState.collectAsStateWithLifecycle()

    LaunchedEffect(logoutState) {
        when(logoutState) {
            is LogoutState.Error -> {
                Toast.makeText(
                    context,
                    "Logout error: ${(logoutState as LogoutState.Error).message}",
                    Toast.LENGTH_SHORT
                ).show()
                loginViewModel.clearState()
            }
            is LogoutState.Success -> {
                navController.navigate(AuthScreens.Landing.route) {
                    popUpTo(0) {
                        inclusive = true
                    }
                }
                loginViewModel.clearState()
            }
            LogoutState.Loading -> {}
            LogoutState.Idle -> {}
        }
    }

    if (logoutState is LogoutState.Loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                // Only trigger logout if not already loading
                if (logoutState !is LogoutState.Loading) {
                    authViewModel.signOut()
                    loginViewModel.logoutUser()
                }
            },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.logout),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
        )
        Text(
            text = "Log out",
            style = MaterialTheme.typography.titleSmall.copy(
                color = MaterialTheme.colorScheme.error
            ),
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}