package com.example.notemark.auth.presentation.login.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notemark.auth.data.remote.api.LoginService
import com.example.notemark.auth.data.remote.api.LogoutService
import com.example.notemark.auth.domain.model.LoginModel
import com.example.notemark.auth.domain.model.LoginResponse
import com.example.notemark.core.manager.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginService: LoginService,
    private val logoutService: LogoutService,
    private val sessionManager: SessionManager
): ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: MutableStateFlow<LoginState> = _loginState

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            try {
                _loginState.value = LoginState.Loading

                val loginModel = LoginModel(
                    email = email,
                    password = password
                )

                val result = loginService.login(loginModel)

                if (result != null) {
                    sessionManager.saveTokens(
                        accessToken = result.accessToken,
                        refreshToken = result.refreshToken
                    )
                    sessionManager.saveUserName(result.username)
                    _loginState.value = LoginState.Success(result)
                } else {
                    _loginState.value = LoginState.Error("Registration failed, please try again!")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private val _logoutState = MutableStateFlow<LogoutState>(LogoutState.Idle)
    val logoutState: StateFlow<LogoutState> = _logoutState.asStateFlow()

    fun logoutUser() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _logoutState.value = LogoutState.Loading

                // Call logout API
                val result = logoutService.logout()

                // Clear local data regardless of API result
                sessionManager.clearTokens()

                if (result != null) {
                    _logoutState.value = LogoutState.Success("Logged out successfully")
                } else {
                    // Even if API fails, we cleared local data, so consider it success
                    _logoutState.value = LogoutState.Success("Logged out successfully")
                }
            } catch (e: Exception) {
                // Even if API fails, clear local data and consider it success
                sessionManager.clearTokens()
                _logoutState.value = LogoutState.Success("Logged out successfully")
            }
        }
    }

    fun clearState() {
        _logoutState.value = LogoutState.Idle
    }
}

sealed class LogoutState {
    object Idle : LogoutState()
    object Loading : LogoutState()
    data class Success(val message: String) : LogoutState()
    data class Error(val message: String) : LogoutState()
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val tokens: LoginResponse) : LoginState()
    data class Error(val message: String) : LoginState()
}