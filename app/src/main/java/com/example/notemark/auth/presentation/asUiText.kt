package com.example.notemark.auth.presentation

import com.example.notemark.R
import com.example.notemark.auth.domain.DataError
import com.example.notemark.auth.domain.Result
import com.example.notemark.auth.presentation.UiText.StringResource

fun DataError.asUiText(): UiText {
    return when (this) {
        DataError.Network.REQUEST_TIMEOUT -> StringResource(
            R.string.the_request_timed_out
        )

        DataError.Network.TOO_MANY_REQUESTS -> StringResource(
            R.string.youve_hit_your_rate_limit
        )

        DataError.Network.NO_INTERNET -> StringResource(
            R.string.no_internet
        )

        DataError.Network.SERVER_ERROR -> StringResource(
            R.string.server_error
        )

        DataError.Network.UNKNOWN -> StringResource(
            R.string.unknown_error
        )

        DataError.Local.DISK_FULL -> StringResource(
            R.string.disk_full
        )

        DataError.ValidationErrors.INVALID_FORMAT -> StringResource(
            R.string.invalid_format
        )
    }
}

fun Result.Error<*, DataError>.asErrorUiText(): UiText {
    return error.asUiText()
}