package com.example.notemark.auth.presentation.login.screens

import android.content.res.Configuration
import android.content.res.Resources
import android.widget.Toast
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
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.notemark.NoteMarkButton
import com.example.notemark.NoteMarkLink
import com.example.notemark.NoteMarkTextField
import com.example.notemark.auth.presentation.login.vm.LoginState
import com.example.notemark.auth.presentation.login.vm.LoginViewModel
import com.example.notemark.auth.presentation.registration.vm.RegistrationState
import com.example.notemark.auth.presentation.util.DeviceConfiguration
import com.example.notemark.navigation.screens.AuthScreens

@Composable
fun LoginScreen(
    navController: NavHostController = rememberNavController(),
) {
    Scaffold(
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
                            .padding(top = 32.dp)
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
                            .verticalScroll(rememberScrollState())
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
                            .widthIn(max = 540.dp)
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
    modifier: Modifier = Modifier
) {

    val loginViewModel: LoginViewModel = hiltViewModel()
    val state by loginViewModel.loginState.collectAsStateWithLifecycle()

    LaunchedEffect(state) {
        if (state is LoginState.Success) {
            navController.navigate(AuthScreens.LogIn.route)
            loginViewModel.clearState()
        }
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isDisabled by remember { mutableStateOf(true) }
    val focusManager = LocalFocusManager.current

    // Update button state based on email and password fields
    isDisabled = email.isBlank() || password.isBlank()
    val context = LocalContext.current

    Column(
        modifier = modifier
    ) {
        if (state is LoginState.Error) {
            Toast.makeText(context, (state as LoginState.Error).message, Toast.LENGTH_SHORT).show()
        }
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
            text = if (state is LoginState.Loading) "Logging in..." else "Log in",
            onClick = {
                loginViewModel.loginUser(email, password)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isDisabled
        )
        Spacer(modifier = Modifier.height(24.dp))

        NoteMarkLink(
            text = "Don't have an account?",
            onClick = { navController.navigate(AuthScreens.Registration.route) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

fun isTablet(): Boolean {
    val configuration = Resources.getSystem().configuration
    return (configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE
}