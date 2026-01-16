package com.example.notemark.navigation.screens

import kotlinx.serialization.Serializable

@Serializable
sealed interface AuthScreens {

    @Serializable
    object Landing: AuthScreens

    @Serializable
    object LogIn : AuthScreens

    @Serializable
    object Registration : AuthScreens

}