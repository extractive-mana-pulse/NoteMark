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
    object Settings : HomeScreens("settings")

}