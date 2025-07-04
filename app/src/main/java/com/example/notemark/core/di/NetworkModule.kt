package com.example.notemark.core.di

import com.example.notemark.core.factory.HttpClientFactory
import com.example.notemark.core.manager.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClientFactory(sessionManager: SessionManager): HttpClientFactory {
        return HttpClientFactory(sessionManager)
    }

    @Provides
    @Singleton
    fun provideHttpClient(httpClientFactory: HttpClientFactory): HttpClient {
        return httpClientFactory.build()
    }
}