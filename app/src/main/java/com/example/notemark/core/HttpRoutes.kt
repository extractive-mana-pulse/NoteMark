package com.example.notemark.core

object HttpRoutes {

    private const val BASE_URL = "https://notemark.pl-coding.com"
    const val REGISTER = "$BASE_URL/api/auth/register"
    const val LOGIN = "$BASE_URL/api/auth/login"
    const val REFRESH_TOKEN = "$BASE_URL/api/auth/refresh"
    const val EMAIL = "invoker1441@gmail.com"
    const val GET_NOTES = "$BASE_URL/api/notes"
    const val LOGOUT = "$BASE_URL/api/auth/logout"

}