package com.example.presentation

import android.util.Patterns

fun isValidEmail(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()