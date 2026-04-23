package com.example.notemark.navigation.navs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.notemark.navigation.graphs.Graph
import com.example.notemark.navigation.screens.AuthScreens
import com.example.notemark.navigation.screens.HomeScreens
import com.example.presentation.landing.screens.LandingScreen
import com.example.presentation.login.LoginScreen
import com.example.presentation.registration.RegistrationScreen

internal fun NavGraphBuilder.authNavGraph(
    navController: NavHostController,
) {
    navigation(
        route = Graph.AUTH,
        startDestination = AuthScreens.Landing::class.qualifiedName ?: ""
    ) {
        composable<AuthScreens.Landing> {
            LandingScreen(
                onNavigateToRegistration = {
                    navController.navigate(AuthScreens.Registration) {
                        popUpTo(AuthScreens.Landing) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToLogIn = {
                    navController.navigate(AuthScreens.LogIn){
                        popUpTo(AuthScreens.Landing) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable<AuthScreens.LogIn> {
            LoginScreen(
                onNavigateToRegistration = {
                    navController.navigate(AuthScreens.Registration) {
                        popUpTo(AuthScreens.LogIn) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(HomeScreens.Home) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable<AuthScreens.Registration> {
            RegistrationScreen(
                onNavigateToLogIn = {
                    navController.navigate(AuthScreens.LogIn) {
                        popUpTo(AuthScreens.Registration) {
                            inclusive = true
                        }
                    }
                },
            )
        }
    }
}