package com.example.notemark.auth.presentation.registration.screens

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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.notemark.auth.presentation.registration.vm.RegistrationState
import com.example.notemark.auth.presentation.registration.vm.RegistrationViewModel
import com.example.notemark.auth.presentation.util.DeviceConfiguration
import com.example.notemark.auth.presentation.util.FormValidation
import com.example.notemark.auth.presentation.util.ValidationResult
import com.example.notemark.core.components.NoteMarkButton
import com.example.notemark.core.components.NoteMarkLink
import com.example.notemark.core.components.NoteMarkTextField
import com.example.notemark.navigation.screens.AuthScreens
import kotlinx.coroutines.launch

@Composable
fun RegistrationScreen(
    navController: NavHostController = rememberNavController(),
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.statusBars,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        ) { innerPadding ->

        val rootModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            .padding(horizontal = 16.dp, vertical = 24.dp)
            .consumeWindowInsets(WindowInsets.navigationBars)

        val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
        val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)

        when(deviceConfiguration) {
            DeviceConfiguration.MOBILE_PORTRAIT -> {
                Column(
                    modifier = rootModifier,
                ) {
                    RegistrationHeader(
                        modifier = Modifier.fillMaxWidth()
                    )
                    RegistrationSheet(
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
                    RegistrationHeader(
                        modifier = Modifier
                            .weight(1f)
                    )
                    RegistrationSheet(
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
                    RegistrationHeader(
                        modifier = Modifier
                            .widthIn(max = 540.dp),
                        alignment = Alignment.CenterHorizontally
                    )
                    RegistrationSheet(
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
private fun RegistrationHeader(
    modifier: Modifier = Modifier,
    alignment: Alignment.Horizontal = Alignment.Start
) {
    Column(
        modifier = modifier,
        horizontalAlignment = alignment
    ) {
        Text(
            text = "Create account",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Capture your thoughts and ideas.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun RegistrationSheet(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    snackbarHostState : SnackbarHostState
) {
    val registrationViewModel: RegistrationViewModel = hiltViewModel()
    val registrationState by registrationViewModel.registrationState

    val scope = rememberCoroutineScope()
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(registrationState) {
        when(registrationState) {
            is RegistrationState.Success -> {
                navController.navigate(AuthScreens.LogIn.route)
                registrationViewModel.clearState()
            }
            is RegistrationState.Error -> {
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
            RegistrationState.Idle -> {}
            RegistrationState.Loading -> {}
        }
    }

    val usernameValidation = FormValidation.validateUsername(username)
    val emailValidation = FormValidation.validateEmail(email)
    val passwordValidation = FormValidation.validatePassword(password)
    val repeatPasswordValidation = FormValidation.validateRepeatPassword(password, repeatPassword)

    val isFormValid = FormValidation.isFormValid(username, email, password, repeatPassword)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NoteMarkTextField(
            text = username,
            onValueChange = { username = it },
            label = "Username",
            hint = "Enter username",
            isInputSecret = false,
            focusManager = focusManager,
            focusDirection = FocusDirection.Down,
            imeAction = ImeAction.Next,
            errorText = if (usernameValidation is ValidationResult.Error) usernameValidation.message else null,
            isError = usernameValidation is ValidationResult.Error,
            showFocusText = "Use between 3 and 20 characters for your username."
        )

        NoteMarkTextField(
            text = email,
            onValueChange = { email = it },
            label = "Email",
            hint = "Enter email",
            isInputSecret = false,
            focusManager = focusManager,
            focusDirection = FocusDirection.Down,
            imeAction = ImeAction.Next,
            errorText = if (emailValidation is ValidationResult.Error) emailValidation.message else null,
            isError = emailValidation is ValidationResult.Error
        )

        NoteMarkTextField(
            text = password,
            onValueChange = { password = it },
            label = "Password",
            hint = "Enter password",
            isInputSecret = true,
            focusManager = focusManager,
            focusDirection = FocusDirection.Down,
            imeAction = ImeAction.Next,
            errorText = if (passwordValidation is ValidationResult.Error) passwordValidation.message else null,
            isError = passwordValidation is ValidationResult.Error,
            showFocusText = "Use 8+ characters with a number or symbol for better security."
        )

        NoteMarkTextField(
            text = repeatPassword,
            onValueChange = { repeatPassword = it },
            label = "Repeat Password",
            hint = "Repeat password",
            isInputSecret = true,
            focusManager = focusManager,
            focusDirection = FocusDirection.Down,
            imeAction = ImeAction.Done,
            errorText = if (repeatPasswordValidation is ValidationResult.Error) repeatPasswordValidation.message else null,
            isError = repeatPasswordValidation is ValidationResult.Error
        )

        Spacer(modifier = Modifier.height(16.dp))

        NoteMarkButton(
            text = if (registrationState is RegistrationState.Loading) "" else "Create account",
            onClick = {
                registrationViewModel.registerUser(
                    username = username,
                    email = email,
                    password = password
                )
            },
            enabled = isFormValid,
            modifier = Modifier.fillMaxWidth(),
            isLoading = registrationState is RegistrationState.Loading
        )

        Spacer(modifier = Modifier.height(8.dp))

        NoteMarkLink(
            text = "Already have an account?",
            onClick = {
                navController.navigate(AuthScreens.LogIn.route)
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}