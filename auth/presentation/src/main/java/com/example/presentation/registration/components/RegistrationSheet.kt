package com.example.presentation.registration.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.example.presentation.components.NoteMarkButton
import com.example.presentation.components.NoteMarkLink
import com.example.presentation.components.NoteMarkTextField
import com.example.presentation.registration.RegistrationState
import com.example.presentation.registration.RegistrationViewModel
import com.example.presentation.FormValidation
import com.example.presentation.ValidationResult
import kotlinx.coroutines.launch

@Composable
internal fun RegistrationSheet(
    modifier: Modifier = Modifier,
    onNavigateToLogIn: () -> Unit = {},
    snackbarHostState : SnackbarHostState
) {
    val scope = rememberCoroutineScope()
    val registrationViewModel: RegistrationViewModel = hiltViewModel()
    val registrationState by registrationViewModel.registrationState

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(registrationState) {
        when(registrationState) {
            is RegistrationState.Success -> {
                onNavigateToLogIn()
                registrationViewModel.clearState()
            }
            is RegistrationState.Error -> {
                Log.e("RegistrationScreen", "Registration failed: ${(registrationState as RegistrationState.Error).message}")
                scope.launch {
                    val result = snackbarHostState
                        .showSnackbar(
                            message = "Invalid registration credentials: ${(registrationState as RegistrationState.Error).message}",
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
            onClick = { onNavigateToLogIn() },
            modifier = Modifier.fillMaxWidth()
        )
    }
}