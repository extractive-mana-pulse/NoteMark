package com.example.notemark.auth.domain

sealed interface DataError: Error {

    enum class Network: DataError {
        REQUEST_TIMEOUT,
        TOO_MANY_REQUESTS,
        NO_INTERNET,
        SERVER_ERROR,
        UNKNOWN
    }

    enum class Local: DataError {
        DISK_FULL
    }

    enum class ValidationErrors: DataError {
        INVALID_FORMAT
    }

    // here we can past all errors from our app
}