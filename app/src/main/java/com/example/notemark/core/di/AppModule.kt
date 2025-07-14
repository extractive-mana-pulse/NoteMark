package com.example.notemark.core.di

import com.example.notemark.core.manager.SessionManager
import com.example.notemark.main.data.remote.api.NoteService
import com.example.notemark.main.data.remote.repositoryImpl.NoteServiceImpl
import com.example.notemark.main.domain.repository.NotesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNoteService(client: HttpClient, sessionManager: SessionManager): NoteService {
        return NoteServiceImpl(client, sessionManager)
    }

    @Provides
    @Singleton
    fun provideNoteRepository(api: NoteServiceImpl): NotesRepository {
        return NotesRepository(api)
    }

    @Provides
    @Singleton
    fun provideNoteServiceImpl(api: HttpClient, sessionManager: SessionManager): NoteServiceImpl {
        return NoteServiceImpl(api, sessionManager)
    }
}