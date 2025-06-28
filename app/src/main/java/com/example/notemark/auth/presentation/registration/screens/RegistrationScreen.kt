package com.example.notemark.auth.presentation.registration.screens

import androidx.compose.foundation.Image
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.notemark.NoteMarkButton
import com.example.notemark.NoteMarkLink
import com.example.notemark.NoteMarkTextField
import com.example.notemark.R
import com.example.notemark.TextFieldWithTitle
import com.example.notemark.auth.presentation.registration.vm.RegistrationState
import com.example.notemark.auth.presentation.registration.vm.RegistrationViewModel
import com.example.notemark.auth.presentation.util.DeviceConfiguration
import com.example.notemark.navigation.screens.AuthScreens

@Composable
fun RegistrationScreen(
    navController: NavHostController = rememberNavController(),
) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.statusBars
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
                    RegistrationHeader(
                        modifier = Modifier
                            .weight(1f)
                    )
                    RegistrationSheet(
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
                    RegistrationHeader(
                        modifier = Modifier
                            .widthIn(max = 540.dp),
                        alignment = Alignment.CenterHorizontally
                    )
                    RegistrationSheet(
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
    modifier: Modifier = Modifier
) {
    val registrationViewModel: RegistrationViewModel = hiltViewModel()
    val registrationState by registrationViewModel.registrationState

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val context = LocalConfiguration.current

    val isDisabled = username.isBlank() || email.isBlank() ||
            password.isBlank() || repeatPassword.isBlank() ||
            password != repeatPassword ||
            registrationState is RegistrationState.Loading

    LaunchedEffect(registrationState) {
        if (registrationState is RegistrationState.Success) {
            navController.navigate(AuthScreens.LogIn.route)
            registrationViewModel.clearState()
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (registrationState is RegistrationState.Error) {
            Text(
                text = (registrationState as RegistrationState.Error).message
            )
        }
        TextFieldWithTitle(
            title = "Username",
            placeholder = "John.doe",
            focusManager = focusManager,
            focusDirection = FocusDirection.Down,
            imeAction = ImeAction.Next,
            value = username,
            onValueChange = { username = it },
        )

        Spacer(modifier = Modifier.height(16.dp))

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
            imeAction = ImeAction.Next,
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextFieldWithTitle(
            title = "Repeat password",
            placeholder = "Password",
            focusManager = focusManager,
            focusDirection = FocusDirection.Enter,
            imeAction = ImeAction.Done,
            trailingIcon = {
                Image(
                    painter = painterResource(R.drawable.eye),
                    contentDescription = null
                )
            },
            value = repeatPassword,
            onValueChange = { repeatPassword = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        NoteMarkButton(
            text = if (registrationState is RegistrationState.Loading) "Creating..." else "Create account",
            onClick = {
                registrationViewModel.registerUser(
                    username = username,
                    email = email,
                    password = password
                )
            },
            enabled = !isDisabled,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        NoteMarkLink(
            text = "Already have an account?",
            onClick = { navController.navigate(AuthScreens.LogIn.route) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}