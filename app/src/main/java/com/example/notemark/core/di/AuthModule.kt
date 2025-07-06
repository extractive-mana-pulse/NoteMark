package com.example.notemark.core.di

import android.content.Context
import com.example.notemark.auth.data.remote.api.LoginService
import com.example.notemark.auth.data.remote.api.LogoutService
import com.example.notemark.auth.data.remote.repositoryImpl.LogoutServiceImpl
import com.example.notemark.auth.data.remote.api.RegistrationService
import com.example.notemark.auth.data.remote.repositoryImpl.LoginServiceImpl
import com.example.notemark.auth.data.remote.repositoryImpl.RegistrationServiceImpl
import com.example.notemark.core.manager.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideSessionManager(@ApplicationContext context: Context): SessionManager = SessionManager(context)


    @Provides
    @Singleton
    fun provideRegistrationService(client: HttpClient): RegistrationService = RegistrationServiceImpl(client)

    @Provides
    @Singleton
    fun provideLoginService(client: HttpClient): LoginService = LoginServiceImpl(client)

    @Provides
    @Singleton
    fun provideLogoutService(client: HttpClient): LogoutService = LogoutServiceImpl(client)
}