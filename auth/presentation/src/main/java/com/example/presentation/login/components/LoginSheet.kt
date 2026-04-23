package com.example.presentation.login.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.presentation.components.NoteMarkButton
import com.example.presentation.components.NoteMarkLink
import com.example.presentation.components.NoteMarkTextField
import com.example.presentation.login.LoginState
import com.example.presentation.login.LoginViewModel
import com.example.presentation.isValidEmail
import com.example.presentation.vm.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginSheet(
    modifier: Modifier = Modifier,
    onNavigateToHome: () -> Unit = {},
    snackbarHostState : SnackbarHostState,
    onNavigateToRegistration: () -> Unit = {},
) {

    val scope = rememberCoroutineScope()
    val loginViewModel: LoginViewModel = hiltViewModel()
    val authViewModel: AuthViewModel = hiltViewModel()
    val state by loginViewModel.loginState.collectAsStateWithLifecycle()

    LaunchedEffect(state) {
        when (state) {
            is LoginState.Success -> {
                authViewModel.refreshAuthState()
                onNavigateToHome()

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
            onClick = { onNavigateToRegistration() },
            modifier = Modifier.fillMaxWidth()
        )
    }
}