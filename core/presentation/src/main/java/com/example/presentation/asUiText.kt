package com.example.presentation

import com.example.notemark.core.presentation.R
import com.example.domain.DataError

fun DataError.asUiText(): UiText {
    return when (this) {
        DataError.Network.REQUEST_TIMEOUT -> UiText.StringResource(
            R.string.the_request_timed_out
        )

        DataError.Network.TOO_MANY_REQUESTS -> UiText.StringResource(
            R.string.youve_hit_your_rate_limit
        )

        DataError.Network.NO_INTERNET -> UiText.StringResource(
            R.string.no_internet
        )

        DataError.Network.SERVER_ERROR -> UiText.StringResource(
            R.string.server_error
        )

        DataError.Network.UNKNOWN -> UiText.StringResource(
            R.string.unknown_error
        )

        DataError.Local.DISK_FULL -> UiText.StringResource(
            R.string.disk_full
        )

        DataError.ValidationErrors.INVALID_FORMAT -> UiText.StringResource(
            R.string.invalid_format
        )

        DataError.Note.NOTE_IS_NOT_DELETED -> UiText.StringResource(
            R.string.note_is_not_deleted
        )
    }
}

//fun Result.Error<*, DataError>.asErrorUiText(): UiText {
//    return error.asUiText()
//}