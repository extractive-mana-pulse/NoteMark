package com.example.notemark.navigation.navs

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.notemark.navigation.graphs.Graph

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        // please note that after successfully logging in,
        // we need to remove a back stack also launch the app from Home screen not from Auth screens again.
        // if (authenticationManager.isUserSignedIn()) Graph.HOME else Graph.AUTH
        startDestination = Graph.AUTH
    ) {
        authNavGraph(
            navController = navController,
        )
        homeNavGraph(
            navController = navController,
//            theme = theme
        )
    }
}