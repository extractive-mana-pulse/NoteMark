package com.example.presentation.vm

import androidx.lifecycle.ViewModel
import com.example.domain.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val sessionManager: SessionRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init { checkAuthenticationStatus() }

    private fun checkAuthenticationStatus() {

        val newState = if (sessionManager.isLoggedIn()) {
            AuthState.Authenticated
        } else {
            AuthState.Unauthenticated
        }
        _authState.value = newState
    }
    fun refreshAuthState() { checkAuthenticationStatus() }
}

sealed class AuthState {
    object Loading : AuthState()
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
}