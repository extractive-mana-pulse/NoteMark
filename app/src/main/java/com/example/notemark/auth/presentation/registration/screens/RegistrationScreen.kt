package com.example.notemark.auth.presentation.registration.screens

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.notemark.R
import com.example.notemark.TextFieldWithTitle
import com.example.notemark.navigation.screens.AuthScreens

@Composable
fun RegistrationScreen(
    navController: NavHostController = rememberNavController(),
) {

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        contentWindowInsets = WindowInsets.statusBars
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerLowest,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                )
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .consumeWindowInsets(WindowInsets.navigationBars),

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
            RegistrationSheet(
                navController = navController,
            )
        }
    }
}

@Composable
fun RegistrationSheet(
    navController: NavHostController,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isDisabled by remember { mutableStateOf(true) }
    var focusManager = LocalFocusManager.current

    isDisabled = email.isBlank() || password.isBlank()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextFieldWithTitle(
            title = "Username",
            placeholder = "John.doe",
            focusManager = focusManager,
            focusDirection = FocusDirection.Down,
            imeAction = ImeAction.Next,
            value = email,
            onValueChange = { email = it },
            showFocusText = "Use between 3 and 20 characters for your username"
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextFieldWithTitle(
            title = "Email",
            placeholder = "john.doe@example.com",
            focusManager = focusManager,
            focusDirection = FocusDirection.Down,
            imeAction = ImeAction.Next,
            value = email,
            onValueChange = { email = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextFieldWithTitle(
            title = "Password",
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
            value = password,
            onValueChange = { password = it },
            showFocusText = "Use between 8+ characters with a number or symbol for better security"
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
            value = password,
            onValueChange = { password = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // Handle login logic here
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = RoundedCornerShape(12.dp),
            enabled = !isDisabled
        ) {
            Text(
                text = "Create account",
                style = MaterialTheme.typography.titleSmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(
            onClick = { navController.navigate(AuthScreens.LogIn.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Already have an account?",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Composable
fun IsLandscape() {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
}