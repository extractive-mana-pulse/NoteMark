package com.example.notemark.auth.data.remote

object HttpRoutes {

    private const val BASE_URL = "https://notemark.pl-coding.com"

    const val REGISTER = "$BASE_URL/api/auth/register"
    const val LOGIN = "$BASE_URL/api/auth/login"
    const val REFRESH_TOKEN = "$BASE_URL/api/auth/refresh"

}