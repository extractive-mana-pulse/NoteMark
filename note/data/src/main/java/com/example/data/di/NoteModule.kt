package com.example.data.di

import NoteRemoteMediator
import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.Room
import com.example.data.local.LocalNoteDataSourceImpl
import com.example.data.local.NoteDatabase
import com.example.data.local.NoteEntity
import com.example.data.remote.NoteServiceImpl
import com.example.domain.ConnectivityObserver
import com.example.domain.LocalNoteDataSource
import com.example.domain.NoteService
import com.example.domain.SessionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NoteModule {

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
    fun provideLocalNoteDataSource(
        noteDatabase: NoteDatabase
    ): LocalNoteDataSource {
        return LocalNoteDataSourceImpl(noteDatabase)
    }

    @OptIn(ExperimentalPagingApi::class)
    @Provides
    @Singleton
    fun provideNotesPager(notesDb: NoteDatabase, noteService: NoteService): Pager<Int, com.example.domain.NoteEntity> {
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
    fun provideNoteService(
        client: HttpClient,
        sessionManager: SessionRepository,
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
    fun provideNoteServiceImpl(
        api: HttpClient,
        sessionManager: SessionRepository,
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