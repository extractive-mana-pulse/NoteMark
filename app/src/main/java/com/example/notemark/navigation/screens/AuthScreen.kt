package com.example.notemark.navigation.screens

import kotlinx.serialization.Serializable

@Serializable
sealed class AuthScreens(val route: String) {

    @Serializable
    object Splash: AuthScreens("splash_screen")

    @Serializable
    object Landing: AuthScreens("landing_screen")

    @Serializable
    object LogIn : AuthScreens("sign_in")

    @Serializable
    object Registration : AuthScreens("sign_up")

}