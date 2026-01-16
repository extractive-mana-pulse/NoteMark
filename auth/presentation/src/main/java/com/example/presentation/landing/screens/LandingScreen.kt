package com.example.presentation.landing.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.notemark.core.presentation.R
import com.example.presentation.DeviceConfiguration

@Composable
fun LandingScreen(
    onNavigateToRegistration: () -> Unit = {},
    onNavigateToLogIn: () -> Unit = {},
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)

    when(deviceConfiguration) {
        DeviceConfiguration.MOBILE_PORTRAIT -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.image),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                LandingSheet(
                    onNavigateToLogIn = onNavigateToLogIn,
                    onNavigateToRegistration = onNavigateToRegistration,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .clip(shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                )
            }
        }
        DeviceConfiguration.MOBILE_LANDSCAPE -> {
            Row(
                modifier = Modifier.background(Color(0xFFE0EAFF)),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(id = R.drawable.image),
                    contentDescription = null,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                LandingSheet(
                    onNavigateToLogIn = onNavigateToLogIn,
                    onNavigateToRegistration = onNavigateToRegistration,
                    modifier = Modifier
                        .weight(1.2f)
                        .clip(shape = RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp)),
                )
            }
        }
        DeviceConfiguration.TABLET_PORTRAIT,
        DeviceConfiguration.TABLET_LANDSCAPE,
        DeviceConfiguration.DESKTOP -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE0EAFF))
                    .verticalScroll(rememberScrollState()),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.image),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                LandingSheet(
                    onNavigateToLogIn = onNavigateToLogIn,
                    onNavigateToRegistration = onNavigateToRegistration,
                    modifier = Modifier
                        .padding(horizontal = 48.dp)
                        .clip(shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Composable
fun LandingSheet(
    modifier: Modifier = Modifier,
    onNavigateToLogIn: () -> Unit = {},
    onNavigateToRegistration: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
            )
            .padding(24.dp)
    ) {
        Text(
            text = "Your Own Collection\n" +
                    "of Notes",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Capture your thoughts and ideas.",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onNavigateToRegistration() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = "Get Started",
                style = MaterialTheme.typography.titleSmall
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { onNavigateToLogIn() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary,
            ),
            border = BorderStroke(1.dp, Color(0xFF5977F7)),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(
                text = "Log In",
                style = MaterialTheme.typography.titleSmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}