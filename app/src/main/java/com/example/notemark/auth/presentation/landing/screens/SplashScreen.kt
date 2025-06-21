package com.example.notemark.auth.presentation.landing.screens

import com.example.notemark.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.notemark.ui.theme.CustomTheme

@Composable
fun SplashScreen(
    theme: CustomTheme
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.primary)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}