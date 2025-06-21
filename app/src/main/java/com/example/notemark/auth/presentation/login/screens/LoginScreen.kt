package com.example.notemark.auth.presentation.login.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.notemark.R
import com.example.notemark.navigation.screens.AuthScreens
import com.example.notemark.ui.theme.CustomTheme

@Composable
fun LoginScreen(
    navController: NavHostController = rememberNavController(),
    theme: CustomTheme
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.primary)
            .statusBarsPadding()

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = theme.surfaceLowest,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                )
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Text(
                text = "Log In",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Capture your thoughts and ideas.",
                style = MaterialTheme.typography.bodyLarge
            )
            LoginSheet(
                navController = navController,
                theme = theme
            )
        }
    }
}

@Composable
fun LoginSheet(
    navController: NavHostController,
    theme: CustomTheme
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isDisabled by remember { mutableStateOf(true) }
    var focusManager = LocalFocusManager.current

    // Update button state based on email and password fields
    isDisabled = email.isBlank() || password.isBlank()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextFieldWithTitle(
            title = "Email",
            placeholder = "john.doe@example.com",
            theme = theme,
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
            theme = theme,
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
                containerColor = theme.primary,
                contentColor = theme.onPrimary,
                disabledContainerColor = theme.opacity12,
                disabledContentColor = theme.onSurface
            ),
            shape = RoundedCornerShape(12.dp),
            enabled = !isDisabled
        ) {
            Text(
                text = "Log in",
                style = MaterialTheme.typography.titleSmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(
            onClick = { navController.navigate(AuthScreens.Registration.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Don't have an account?",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = theme.primary
                )
            )
        }
    }
}
@Composable
fun TextFieldWithTitle(
    title: String,
    placeholder: String,
    theme: CustomTheme,
    focusManager: FocusManager,
    focusDirection: FocusDirection,
    imeAction: ImeAction,
    trailingIcon: @Composable (() -> Unit)? = null,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(
                letterSpacing = 0.01.sp
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .onFocusChanged { focus ->
                    if (focus.isFocused) {
//                        state.passwordError == null
                    }
                },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = theme.surface,
                unfocusedContainerColor = theme.surface,
                focusedIndicatorColor = theme.surface,
                unfocusedIndicatorColor = theme.surface,
            ),
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = theme.onSurfaceVar,
                        letterSpacing = MaterialTheme.typography.bodySmall.letterSpacing
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            trailingIcon = trailingIcon,
            isError = false,
            keyboardOptions = KeyboardOptions(
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(
                        focusDirection = focusDirection
                    )
                }
            )
        )
    }
}