package com.example.notemark.navigation.navs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.notemark.navigation.graphs.Graph

internal fun NavGraphBuilder.homeNavGraph(navController: NavHostController) {
    navigation(
        route = Graph.HOME,
        startDestination = "HomeScreen.Home.route"
    ) {
//        composable(route = HomeScreen.Home.route) {
//
//        }
    }
}
