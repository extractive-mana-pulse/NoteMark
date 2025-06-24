package com.example.notemark.navigation.navs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.notemark.auth.presentation.landing.screens.LandingScreen
import com.example.notemark.auth.presentation.login.screens.LoginScreen
import com.example.notemark.auth.presentation.registration.screens.RegistrationScreen
import com.example.notemark.navigation.graphs.Graph
import com.example.notemark.navigation.screens.AuthScreens

internal fun NavGraphBuilder.authNavGraph(
    navController: NavHostController,
) {
    navigation(
        route = Graph.AUTH,
        startDestination = AuthScreens.Landing.route
    ) {
        composable(
            route = AuthScreens.Landing.route
        ) {
            LandingScreen(
                navController = navController,
            )
        }
        composable(
            route = AuthScreens.LogIn.route
        ) {
            LoginScreen(
                navController = navController,
            )
        }
        composable(
            route = AuthScreens.Registration.route
        ) {
            RegistrationScreen(
                navController = navController,
            )
        }
    }
}