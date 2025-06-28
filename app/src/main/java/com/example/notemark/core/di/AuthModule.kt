package com.example.notemark.core.di

import com.example.notemark.auth.data.remote.api.LoginService
import com.example.notemark.auth.data.remote.api.RegistrationService
import com.example.notemark.auth.data.remote.repositoryImpl.LoginServiceImpl
import com.example.notemark.auth.data.remote.repositoryImpl.RegistrationServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient(Android) {
            install(ContentNegotiation) {
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            }
        }
    }

    @Provides
    @Singleton
    fun provideRegistrationService(client: HttpClient): RegistrationService {
        return RegistrationServiceImpl(client)
    }

    @Provides
    @Singleton
    fun provideLoginService(client: HttpClient): LoginService {
        return LoginServiceImpl(client)
    }
}