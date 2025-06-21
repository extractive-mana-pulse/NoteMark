package com.example.notemark.auth.presentation.registration.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun RegistrationScreen() {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Registration",
            style = MaterialTheme.typography.titleLarge

        )
    }
}