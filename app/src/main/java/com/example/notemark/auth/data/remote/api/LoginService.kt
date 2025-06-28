package com.example.notemark.auth.data.remote.api

import com.example.notemark.auth.domain.model.LoginModel

interface LoginService {

    suspend fun login(body: LoginModel): LoginModel?

}