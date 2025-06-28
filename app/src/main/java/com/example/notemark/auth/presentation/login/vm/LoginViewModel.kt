package com.example.notemark.auth.presentation.login.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notemark.auth.data.remote.api.LoginService
import com.example.notemark.auth.domain.model.LoginModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginService: LoginService
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
                    _loginState.value = LoginState.Success(result)
                    println("User registered: ${result.email}")
                } else {
                    _loginState.value = LoginState.Error("Registration failed")
                    println("Registration failed")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Unknown error")
                println("Registration error: ${e.message}")
            }
        }
    }
    fun clearState() {
        _loginState.value = LoginState.Idle
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val user: LoginModel) : LoginState()
    data class Error(val message: String) : LoginState()
}