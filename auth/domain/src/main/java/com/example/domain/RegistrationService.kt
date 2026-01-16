package com.example.domain

import com.example.domain.model.RegistrationModel

interface RegistrationService {

    suspend fun register(body: RegistrationModel): RegistrationModel?

}