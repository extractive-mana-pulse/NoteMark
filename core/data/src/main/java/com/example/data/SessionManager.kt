package com.example.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.domain.SessionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SessionManager @Inject constructor(
    @ApplicationContext context: Context  // ✅ Add @ApplicationContext
): SessionRepository {

    companion object {
        private const val TAG = "SessionManager"
        const val PREF_NAME = "NoteMarkPrefs"
        const val ACCESS_TOKEN = "access_token"
        const val REFRESH_TOKEN = "refresh_token"
        const val USER_NAME = "user_name"
        const val USER_EMAIL = "user_email"
        const val IS_LOGGED_IN = "is_logged_in"
    }

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    override fun saveTokens(accessToken: String, refreshToken: String) {
        try {
            sharedPrefs.edit().apply {
                putString(ACCESS_TOKEN, accessToken)
                putString(REFRESH_TOKEN, refreshToken)
                putBoolean(IS_LOGGED_IN, true)
                apply()
            }
            Log.d(TAG, "Tokens saved successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving tokens: ${e.message}")
        }
    }

    override fun saveUserName(userName: String) {
        try {
            sharedPrefs.edit().apply {
                putString(USER_NAME, userName)
                apply()
            }
            Log.d(TAG, "Username saved successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving username: ${e.message}")
        }
    }

    fun saveUserEmail(email: String) {
        try {
            sharedPrefs.edit().apply {
                putString(USER_EMAIL, email)
                apply()
            }
            Log.d(TAG, "Email saved successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving email: ${e.message}")
        }
    }

    override fun getUserName(): String? {
        return try {
            sharedPrefs.getString(USER_NAME, null)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting username: ${e.message}")
            null
        }
    }

    fun getUserEmail(): String? {
        return try {
            sharedPrefs.getString(USER_EMAIL, null)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting email: ${e.message}")
            null
        }
    }

    override fun getAccessToken(): String? {
        return try {
            sharedPrefs.getString(ACCESS_TOKEN, null)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting access token: ${e.message}")
            null
        }
    }

    override fun getRefreshToken(): String? {
        return try {
            sharedPrefs.getString(REFRESH_TOKEN, null)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting refresh token: ${e.message}")
            null
        }
    }

    override fun clearUserName() {
        try {
            sharedPrefs.edit().apply {
                remove(USER_NAME)
                apply()
            }
            Log.d(TAG, "Username cleared successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing username: ${e.message}")
        }
    }


    fun clearUserEmail() {
        try {
            sharedPrefs.edit().apply {
                remove(USER_EMAIL)
                apply()
            }
            Log.d(TAG, "Email cleared successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing email: ${e.message}")
        }
    }

    override fun clearTokens() {
        try {
            sharedPrefs.edit().apply {
                remove(ACCESS_TOKEN)
                remove(REFRESH_TOKEN)
                putBoolean(IS_LOGGED_IN, false)
                apply()
            }
            Log.d(TAG, "Tokens cleared successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing tokens: ${e.message}")
        }
    }

    override fun clearAllData() {
        try {
            sharedPrefs.edit().clear().apply()
            Log.d(TAG, "All session data cleared successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing all data: ${e.message}")
        }
    }

    override fun isLoggedIn(): Boolean {
        return try {
            val hasToken = !getAccessToken().isNullOrEmpty()
            val isLoggedIn = sharedPrefs.getBoolean(IS_LOGGED_IN, false)
            hasToken && isLoggedIn
        } catch (e: Exception) {
            Log.e(TAG, "Error checking login status: ${e.message}")
            false
        }
    }

    fun getAllSessionData(): Map<String, String?> {
        return mapOf(
            "userName" to getUserName(),
            "userEmail" to getUserEmail(),
            "accessToken" to getAccessToken()?.take(10) + "...", // Don't log full token
            "refreshToken" to getRefreshToken()?.take(10) + "...", // Don't log full token
            "isLoggedIn" to isLoggedIn().toString()
        )
    }
}