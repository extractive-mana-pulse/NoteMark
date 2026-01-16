package com.example.notemark.navigation.navs

import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.data.AndroidConnectivityObserver
import com.example.notemark.navigation.graphs.Graph
import com.example.notemark.navigation.screens.AuthScreens
import com.example.notemark.navigation.screens.HomeScreens
import com.example.presentation.ConnectivityViewModel
import com.example.presentation.NotesViewModel
import com.example.presentation.edit_note.CreateNoteScreen
import com.example.presentation.edit_note.EditNoteScreen
import com.example.presentation.home.HomeScreen
import com.example.presentation.note_details.DetailsScreen
import com.example.presentation.settings.SettingsScreen

internal fun NavGraphBuilder.homeNavGraph(
    navController: NavHostController
) {

    navigation(
        route = Graph.HOME,
        startDestination = HomeScreens.Home::class.qualifiedName ?: ""
    ) {

        composable<HomeScreens.Home> {
            val context = LocalContext.current
            val connectivityViewModel = viewModel<ConnectivityViewModel> {
                ConnectivityViewModel(
                    connectivityObserver = AndroidConnectivityObserver(
                        context = context
                    )
                )
            }
            val viewModel: NotesViewModel = hiltViewModel()
            val username = viewModel.username.collectAsStateWithLifecycle()
            val accessToken = viewModel.accessToken.collectAsStateWithLifecycle()
            val refreshToken = viewModel.refreshToken.collectAsStateWithLifecycle()
            val notesList = viewModel.notePagingFlow.collectAsLazyPagingItems()
            val noteUiState = viewModel.createNoteState.collectAsStateWithLifecycle()
            val connectivityState = connectivityViewModel.isConnected.collectAsStateWithLifecycle()
            HomeScreen(
                onNavigateToHome = {
                    navController.navigate(HomeScreens.Home) {
                        popUpTo(HomeScreens.Home) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(AuthScreens.LogIn) {
                        popUpTo(0) {
                            inclusive
                        }
                    }
                },
                onNavigateToSettings = { navController.navigate(HomeScreens.Settings) },
                onNavigateToCreateNote = { navController.navigate(HomeScreens.CreateNote) },
                onNavigateToDetailsWithId = {
                    navController.navigate(
                        HomeScreens.Details(
                            noteId = it
                        )
                    )
                },
                connectivityState = connectivityState,
                notesList = notesList,
                noteUiState = noteUiState,
                username = username.value,
                accessToken = accessToken.value,
                refreshToken = refreshToken.value
            )
        }

        composable<HomeScreens.Details> {
            val notesViewModel: NotesViewModel = hiltViewModel()
            val notes = notesViewModel.notePagingFlow.collectAsLazyPagingItems()
            val argument = it.toRoute<HomeScreens.Details>()

            DetailsScreen(
                navController = navController,
                noteId = argument.noteId,
                notes = notes
            )
        }
        composable<HomeScreens.EditNote> {
            val argument = it.toRoute<HomeScreens.EditNote>()
            EditNoteScreen(
                onNavigateHome = {
                    navController.navigate(HomeScreens.Home) {
                        popUpTo(HomeScreens.Home) {
                            inclusive = true
                        }
                    }
                },
                onNavigateUp = {
                    navController.navigateUp()
                },
                noteId = argument.noteId,
            )
        }

        composable<HomeScreens.CreateNote> {
            CreateNoteScreen(navController = navController)
        }

        composable<HomeScreens.Settings> {
            SettingsScreen(
                onNavigateToLanding = {
                    navController.navigate(AuthScreens.Landing) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                },
                onNavigateUp = {
                    navController.navigateUp()
                }
            )
        }
    }
}
