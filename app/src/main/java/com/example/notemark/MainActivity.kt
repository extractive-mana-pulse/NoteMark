package com.example.notemark

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.notemark.navigation.navs.AppNavigation
import com.example.notemark.ui.theme.LocalTheme
import com.example.notemark.ui.theme.NoteMarkTheme
import com.example.notemark.ui.theme.darkThemeColors
import com.example.notemark.ui.theme.lightThemeColors

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {

            val navController : NavHostController = rememberNavController()
            val themeColors = if (isSystemInDarkTheme()) lightThemeColors else darkThemeColors

            CompositionLocalProvider(LocalTheme provides themeColors) {

                NoteMarkTheme {

                    AppNavigation(
                        theme = themeColors,
                        navController = navController
                    )
                }
            }
        }
    }
}