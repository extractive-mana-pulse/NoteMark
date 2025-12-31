package com.example.notemark.core.di

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.Room
import com.example.notemark.AndroidConnectivityObserver
import com.example.notemark.ConnectivityObserver
import com.example.notemark.ConnectivityViewModel
import com.example.notemark.core.manager.SessionManager
import com.example.notemark.main.data.local.NoteDatabase
import com.example.notemark.main.data.local.NoteEntity
import com.example.notemark.main.data.remote.NoteRemoteMediator
import com.example.notemark.main.data.remote.api.NoteService
import com.example.notemark.main.data.remote.repositoryImpl.NoteServiceImpl
import com.example.notemark.main.domain.repository.NotesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@OptIn(ExperimentalPagingApi::class)
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNoteDatabase(@ApplicationContext context: Context) : NoteDatabase {
        return Room.databaseBuilder(
            context,
            NoteDatabase::class.java,
            "note_db"
        )
            .fallbackToDestructiveMigration(false)
            .build()

    }

    @Provides
    @Singleton
    fun provideNotesPager(notesDb: NoteDatabase, noteService: NoteService): Pager<Int, NoteEntity> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            remoteMediator = NoteRemoteMediator(
                noteDatabase = notesDb,
                noteApi = noteService
            ),
            pagingSourceFactory = {
                notesDb.dao.pagingSource()
            }
        )
    }

    @Provides
    @Singleton
    fun provideConnectivityObserver(
        @ApplicationContext context: Context
    ): ConnectivityObserver = AndroidConnectivityObserver(context)

    @Provides
    @Singleton
    fun provideNoteService(
        client: HttpClient,
        sessionManager: SessionManager,
        @ApplicationContext applicationContext: Context,
        connectivityObserver: ConnectivityObserver
    ): NoteService {
        return NoteServiceImpl(
            client,
            sessionManager,
            provideNoteDatabase(applicationContext),
            connectivityObserver
        )
    }

    @Provides
    @Singleton
    fun provideNoteRepository(api: NoteServiceImpl): NotesRepository {
        return NotesRepository(api)
    }

    @Provides
    @Singleton
    fun provideNoteServiceImpl(
        api: HttpClient,
        sessionManager: SessionManager,
        @ApplicationContext applicationContext: Context,
        connectivityObserver: ConnectivityObserver
    ): NoteServiceImpl {
        return NoteServiceImpl(
            api,
            sessionManager,
            provideNoteDatabase(applicationContext),
            connectivityObserver
        )
    }
}