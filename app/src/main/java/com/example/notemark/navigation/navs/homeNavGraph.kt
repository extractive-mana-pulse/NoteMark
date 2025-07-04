package com.example.notemark.navigation.navs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.notemark.main.presentation.screens.CreateNoteScreen
import com.example.notemark.main.presentation.screens.HomeScreen
import com.example.notemark.main.presentation.screens.ProfileScreen
import com.example.notemark.main.presentation.screens.SettingsScreen
import com.example.notemark.navigation.graphs.Graph
import com.example.notemark.navigation.screens.HomeScreens

internal fun NavGraphBuilder.homeNavGraph(
    navController: NavHostController
) {

    navigation(
        route = Graph.HOME,
        startDestination = HomeScreens.Home.route
    ) {

        composable(route = HomeScreens.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(route = HomeScreens.CreateNote.route) {
            CreateNoteScreen(navController = navController)
        }

        composable(route = HomeScreens.Profile.route) {
            ProfileScreen(navController = navController)
        }

        composable(route = HomeScreens.Settings.route) {
            SettingsScreen(navController = navController)
        }
    }
}
