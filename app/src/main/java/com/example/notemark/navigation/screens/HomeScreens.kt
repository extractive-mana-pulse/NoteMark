package com.example.notemark.navigation.screens

import kotlinx.serialization.Serializable

@Serializable
sealed class HomeScreens(val route: String) {

    @Serializable
    object Home : HomeScreens("home")

    @Serializable
    object Profile : HomeScreens("profile")

    @Serializable
    object CreateNote : HomeScreens("create_note")

    @Serializable
    data class EditNote(
        val noteId: String
    ): HomeScreens("edit_note")

    @Serializable
    object Settings : HomeScreens("settings")

    @Serializable
    data class Details(
        val noteId: String
    ): HomeScreens("details")

}