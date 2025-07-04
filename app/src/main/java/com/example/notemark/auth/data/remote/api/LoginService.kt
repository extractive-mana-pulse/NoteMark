package com.example.notemark.auth.data.remote.api

import com.example.notemark.auth.domain.model.LoginModel
import com.example.notemark.auth.domain.model.LoginResponse

interface LoginService {

    suspend fun login(body: LoginModel): LoginResponse?

}