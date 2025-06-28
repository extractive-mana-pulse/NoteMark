package com.example.notemark.auth.presentation.registration.vm

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notemark.auth.data.remote.api.RegistrationService
import com.example.notemark.auth.domain.model.RegistrationModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val registrationService: RegistrationService
): ViewModel() {

    private val _registrationState = mutableStateOf<RegistrationState>(RegistrationState.Idle)
    val registrationState: State<RegistrationState> = _registrationState

    fun registerUser(username: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                _registrationState.value = RegistrationState.Loading

                val registrationModel = RegistrationModel(
                    username = username,
                    email = email,
                    password = password
                )

                val result = registrationService.register(registrationModel)
                if (result != null) {
                    _registrationState.value = RegistrationState.Success(result)
                    println("User registered: ${result.username}")
                } else {
                    _registrationState.value = RegistrationState.Error("Registration failed")
                    println("Registration failed")
                }
            } catch (e: Exception) {
                _registrationState.value = RegistrationState.Error(e.message ?: "Unknown error")
                println("Registration error: ${e.message}")
            }
        }
    }

    fun clearState() {
        _registrationState.value = RegistrationState.Idle
    }
}

sealed class RegistrationState {
    object Idle : RegistrationState()
    object Loading : RegistrationState()
    data class Success(val user: RegistrationModel) : RegistrationState()
    data class Error(val message: String) : RegistrationState()
}