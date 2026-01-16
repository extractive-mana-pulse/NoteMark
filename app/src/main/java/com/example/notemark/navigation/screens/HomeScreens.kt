package com.example.notemark.navigation.screens

import kotlinx.serialization.Serializable

@Serializable
sealed interface HomeScreens {

    @Serializable
    data object Home : HomeScreens

    @Serializable
    object Profile : HomeScreens

    @Serializable
    object CreateNote : HomeScreens

    @Serializable
    data class EditNote(
        val noteId: String
    ): HomeScreens

    @Serializable
    object Settings : HomeScreens

    @Serializable
    data class Details(
        val noteId: String
    ): HomeScreens
}