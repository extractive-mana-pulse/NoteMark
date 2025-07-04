package com.example.notemark.core.manager

import android.content.Context
import android.content.SharedPreferences

class SessionManager(
    private val context: Context
) {

    companion object {
        const val PREF_NAME = "NoteMarkPrefs"
        const val ACCESS_TOKEN = "access_token"
        const val REFRESH_TOKEN = "refresh_token"
    }

    private val sharedPrefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPrefs.edit()


    // we are not using commit() because it's synchronous, so we don't need to wait for the result
    // we use apply() because it's asynchronous and saves the data asynchronously
    fun saveTokens(accessToken: String, refreshToken: String) {
        editor.apply {
            putString(ACCESS_TOKEN, accessToken)
            putString(REFRESH_TOKEN, refreshToken)
            apply()
        }
    }

    fun getAccessToken(): String? = sharedPrefs.getString(ACCESS_TOKEN, null)

    fun getRefreshToken(): String? = sharedPrefs.getString(REFRESH_TOKEN, null)

    fun clearTokens() {
        editor.apply {
            remove(ACCESS_TOKEN)
            remove(REFRESH_TOKEN)
            apply()
        }
    }

    fun isLoggedIn(): Boolean = !getAccessToken().isNullOrEmpty()
}