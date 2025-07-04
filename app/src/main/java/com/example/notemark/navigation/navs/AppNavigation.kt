package com.example.notemark.navigation.navs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.notemark.auth.presentation.vm.AuthState
import com.example.notemark.auth.presentation.vm.AuthViewModel
import com.example.notemark.navigation.graphs.Graph

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
) {

    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    when (authState) {
        AuthState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        AuthState.Authenticated -> {
            NavHost(
                navController = navController,
                startDestination = Graph.HOME
            ) {
                homeNavGraph(navController = navController)
                authNavGraph(navController = navController)
            }
        }

        AuthState.Unauthenticated -> {
            NavHost(
                navController = navController,
                startDestination = Graph.AUTH
            ) {
                authNavGraph(navController = navController)
                homeNavGraph(navController = navController)
            }
        }
    }
}