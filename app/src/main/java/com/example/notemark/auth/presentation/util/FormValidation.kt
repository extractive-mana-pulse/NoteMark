package com.example.notemark.auth.presentation.util

import android.util.Patterns

object FormValidation {
    
    fun validateUsername(username: String): ValidationResult {
        return when {
            username.length < 3 -> ValidationResult.Error("Username must be at least 3 characters.")
            username.length > 20 -> ValidationResult.Error("Username can't be longer than 20 characters.")
            else -> ValidationResult.Valid
        }
    }

    fun validateEmail(email: String): ValidationResult {
        val emailPattern = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        return if (emailPattern) {
            ValidationResult.Valid
        } else {
            ValidationResult.Error("Invalid email provided")
        }
    }

    fun validatePassword(password: String): ValidationResult {
        val hasNumberOrSymbol = password.any { it.isDigit() || !it.isLetterOrDigit() }
        return when {
            password.length < 8 -> ValidationResult.Error("Password must be at least 8 characters and include a number or symbol.")
            !hasNumberOrSymbol -> ValidationResult.Error("Password must be at least 8 characters and include a number or symbol.")
            else -> ValidationResult.Valid
        }
    }
    
    fun validateRepeatPassword(password: String, repeatPassword: String): ValidationResult {
        return if (password == repeatPassword) {
            ValidationResult.Valid
        } else {
            ValidationResult.Error("Passwords do not match")
        }
    }
    
    fun isFormValid(
        username: String,
        email: String,
        password: String,
        repeatPassword: String
    ): Boolean {
        return validateUsername(username) is ValidationResult.Valid &&
                validateEmail(email) is ValidationResult.Valid &&
                validatePassword(password) is ValidationResult.Valid &&
                validateRepeatPassword(password, repeatPassword) is ValidationResult.Valid
    }
}

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}