package com.example.notemark.auth.data.remote.api

import com.example.notemark.auth.domain.model.RegistrationModel

interface RegistrationService {

    suspend fun register(body: RegistrationModel): RegistrationModel?

}