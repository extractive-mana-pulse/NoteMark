package com.example.domain

import com.example.domain.model.LoginModel
import com.example.domain.model.LoginResponse

interface LoginService {

    suspend fun login(body: LoginModel): LoginResponse?

}