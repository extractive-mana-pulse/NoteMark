package com.example.notemark.auth.domain

import android.util.Patterns
import com.example.notemark.auth.presentation.util.FormValidation
import com.example.notemark.auth.presentation.util.ValidationResult

class UserDataValidator {

    fun validatePassword(password: String): Result<Unit, PasswordError> {
        if(password.length < 9) {
            return Result.Error(PasswordError.TOO_SHORT)
        }

        val hasUppercaseChar = password.any { it.isUpperCase() }
        if(!hasUppercaseChar) {
            return Result.Error(PasswordError.NO_UPPERCASE)
        }

        val hasDigit = password.any { it.isDigit() }
        if(!hasDigit) {
            return Result.Error(PasswordError.NO_DIGIT)
        }

        return Result.Success(Unit)
    }

    fun validateEmail(email: String): Result<Unit, EmailError> {
        val emailPattern = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        return if (emailPattern) {
            Result.Success(Unit)
        } else {
            Result.Error(EmailError.INVALID_FORMAT)
        }
    }

    fun validateUsername(username: String): Result<Unit, UsernameError> {
        if(username.length < 3) {
            return Result.Error(UsernameError.TOO_SHORT)
        }
        if(username.length > 20) {
            return Result.Error(UsernameError.TOO_LONG)
        }
        return Result.Success(Unit)
    }

    fun validateRepeatPassword(password: String, repeatPassword: String): Result<Unit, Error> {
        if(password != repeatPassword) {
            return Result.Error(PasswordMatchError.PASSWORDS_DO_NOT_MATCH)
        }
        return Result.Success(Unit)
    }

    fun isFormValid(
        username: String,
        email: String,
        password: String,
        repeatPassword: String
    ): Boolean {
        return FormValidation.validateUsername(username) is ValidationResult.Valid &&
                FormValidation.validateEmail(email) is ValidationResult.Valid &&
                FormValidation.validatePassword(password) is ValidationResult.Valid &&
                FormValidation.validateRepeatPassword(password, repeatPassword) is ValidationResult.Valid
    }

    enum class PasswordError: Error {
        TOO_SHORT,
        NO_UPPERCASE,
        NO_DIGIT
    }

    enum class EmailError: Error {
        INVALID_FORMAT,
    }

    enum class UsernameError: Error {
        TOO_SHORT,
        TOO_LONG
    }

    enum class PasswordMatchError : Error {
        PASSWORDS_DO_NOT_MATCH
    }
}