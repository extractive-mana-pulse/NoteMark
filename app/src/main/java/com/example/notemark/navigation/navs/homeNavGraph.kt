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
import com.example.notemark.AndroidConnectivityObserver
import com.example.notemark.ConnectivityViewModel
import com.example.notemark.main.presentation.screens.ProfileScreen
import com.example.notemark.main.presentation.screens.SettingsScreen
import com.example.notemark.main.presentation.screens.note.CreateNoteScreen
import com.example.notemark.main.presentation.screens.note.DetailsScreen
import com.example.notemark.main.presentation.screens.note.EditNoteScreen
import com.example.notemark.main.presentation.screens.note.HomeScreen
import com.example.notemark.main.presentation.vm.NotesViewModel
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
            val context = LocalContext.current
            val connectivityViewModel = viewModel<ConnectivityViewModel> {
                ConnectivityViewModel(
                    connectivityObserver = AndroidConnectivityObserver(
                        context = context
                    )
                )
            }
            val viewModel: NotesViewModel = hiltViewModel()
            val notesList = viewModel.notePagingFlow.collectAsLazyPagingItems()
            val connectivityState = connectivityViewModel.isConnected.collectAsStateWithLifecycle()
            HomeScreen(
                navController = navController,
                connectivityState = connectivityState,
                notesList = notesList
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
                navController = navController,
                noteId = argument.noteId,
            )
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
