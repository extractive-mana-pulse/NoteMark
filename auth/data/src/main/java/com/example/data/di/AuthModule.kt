package com.example.data.di

import com.example.data.SessionManager
import com.example.data.remote.LoginServiceImpl
import com.example.data.remote.LogoutServiceImpl
import com.example.data.remote.RegistrationServiceImpl
import com.example.domain.LoginService
import com.example.domain.LogoutService
import com.example.domain.RegistrationService
import com.example.domain.SessionRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindSessionRepository(
        sessionManager: SessionManager
    ): SessionRepository

    companion object {

        @Provides
        @Singleton
        fun provideHttpClient(): HttpClient = HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
            install(Logging) {
                level = LogLevel.ALL
            }
        }

        @Provides
        @Singleton
        fun provideRegistrationService(client: HttpClient): RegistrationService =
            RegistrationServiceImpl(client)

        @Provides
        @Singleton
        fun provideLoginService(client: HttpClient): LoginService =
            LoginServiceImpl(client)

        @Provides
        @Singleton
        fun provideLogoutService(client: HttpClient): LogoutService =
            LogoutServiceImpl(client)
    }
}