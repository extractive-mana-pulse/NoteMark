package com.example.notemark.auth.presentation.login.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.notemark.auth.presentation.login.vm.LoginState
import com.example.notemark.auth.presentation.login.vm.LoginViewModel
import com.example.notemark.auth.presentation.util.DeviceConfiguration
import com.example.notemark.auth.presentation.util.isValidEmail
import com.example.notemark.auth.presentation.vm.AuthViewModel
import com.example.notemark.core.components.NoteMarkButton
import com.example.notemark.core.components.NoteMarkLink
import com.example.notemark.core.components.NoteMarkTextField
import com.example.notemark.navigation.screens.AuthScreens
import com.example.notemark.navigation.screens.HomeScreens
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavHostController = rememberNavController(),
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.statusBars
    ) { innerPadding ->

        val rootModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .clip(
                RoundedCornerShape(
                    topStart = 15.dp,
                    topEnd = 15.dp
                )
            )
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(
                horizontal = 16.dp,
                vertical = 24.dp
            )
            .consumeWindowInsets(WindowInsets.navigationBars)

        val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
        val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)

        when(deviceConfiguration) {
            DeviceConfiguration.MOBILE_PORTRAIT -> {
                Column(
                    modifier = rootModifier,
                ) {
                    LoginHeaderSection(
                        modifier = Modifier.fillMaxWidth()
                    )
                    LoginSheet(
                        navController = navController,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp),
                        snackbarHostState = snackbarHostState
                    )
                }
            }
            DeviceConfiguration.MOBILE_LANDSCAPE -> {
                Row(
                    modifier = rootModifier
                        .windowInsetsPadding(WindowInsets.displayCutout)
                        .padding(
                            horizontal = 32.dp
                        ),
                    horizontalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    LoginHeaderSection(
                        modifier = Modifier
                            .weight(1f)
                    )
                    LoginSheet(
                        navController = navController,
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        snackbarHostState = snackbarHostState
                    )
                }
            }
            DeviceConfiguration.TABLET_PORTRAIT,
            DeviceConfiguration.TABLET_LANDSCAPE,
            DeviceConfiguration.DESKTOP -> {
                Column(
                    modifier = rootModifier
                        .verticalScroll(rememberScrollState())
                        .padding(top = 48.dp),
                    verticalArrangement = Arrangement.spacedBy(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LoginHeaderSection(
                        modifier = Modifier
                            .widthIn(max = 540.dp),
                        alignment = Alignment.CenterHorizontally
                    )
                    LoginSheet(
                        navController = navController,
                        modifier = Modifier
                            .widthIn(max = 540.dp),
                        snackbarHostState = snackbarHostState
                    )
                }
            }
        }
    }
}

@Composable
fun LoginHeaderSection(
    alignment: Alignment.Horizontal = Alignment.Start,
    modifier: Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = alignment
    ) {
        Text(
            text = "Log In",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "Capture your thoughts and ideas.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun LoginSheet(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    snackbarHostState : SnackbarHostState
) {

    val scope = rememberCoroutineScope()
    val loginViewModel: LoginViewModel = hiltViewModel()
    val authViewModel: AuthViewModel = hiltViewModel()
    val state by loginViewModel.loginState.collectAsStateWithLifecycle()

    LaunchedEffect(state) {
        when (state) {
            is LoginState.Success -> {
                authViewModel.refreshAuthState()
                navController.navigate(HomeScreens.Home.route) {
                    popUpTo(0) {
                        inclusive = true
                    }
                }
                loginViewModel.clearState()
            }
            is LoginState.Error -> {
                scope.launch {
                    val result = snackbarHostState
                        .showSnackbar(
                            message = "Invalid login credentials",
                            actionLabel = "",
                            duration = SnackbarDuration.Short
                        )
                    when (result) {
                        SnackbarResult.ActionPerformed -> {}
                        SnackbarResult.Dismissed -> {}
                    }
                }
            }
            is LoginState.Idle -> {}
            is LoginState.Loading -> {}
        }
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isDisabled by remember { mutableStateOf(true) }
    val focusManager = LocalFocusManager.current

    isDisabled = email.isBlank() || password.isBlank() || !isValidEmail(email)

    Column(
        modifier = modifier
    ) {
        NoteMarkTextField(
            text = email,
            onValueChange = { email = it },
            label = "Email",
            hint = "john.doe@example.com",
            isInputSecret = false,
            modifier = Modifier.fillMaxWidth(),
            focusManager = focusManager,
            focusDirection = FocusDirection.Down,
            imeAction = ImeAction.Next,
        )

        Spacer(modifier = Modifier.height(16.dp))

        NoteMarkTextField(
            text = password,
            onValueChange = { password = it },
            label = "Password",
            hint = "Password",
            isInputSecret = true,
            modifier = Modifier.fillMaxWidth(),
            focusManager = focusManager,
            focusDirection = FocusDirection.Down,
            imeAction = ImeAction.Done,
        )

        Spacer(modifier = Modifier.height(24.dp))

        NoteMarkButton(
            text = if (state is LoginState.Loading) "" else "Log in",
            onClick = {
                loginViewModel.loginUser(email, password)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isDisabled,
            isLoading = state is LoginState.Loading
        )

        Spacer(modifier = Modifier.height(24.dp))

        NoteMarkLink(
            text = "Don't have an account?",
            onClick = {
                navController.navigate(AuthScreens.Registration.route) {
                    popUpTo(AuthScreens.LogIn.route) {
                        inclusive = true
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}