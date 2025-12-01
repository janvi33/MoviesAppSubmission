package com.simple.moviesapp.di

import android.content.Context
import androidx.room.Room
import com.simple.moviesapp.data.MoviesApi
import com.simple.moviesapp.data.MoviesRepositoryImpl
import com.simple.moviesapp.data.local.MoviesDao
import com.simple.moviesapp.data.local.MoviesDatabase
import com.simple.moviesapp.domain.repository.MoviesRepository
import com.simple.moviesapp.domain.usecase.GetGenresUseCase
import com.simple.moviesapp.domain.usecase.GetMoviesPageUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideJson(): Json = com.simple.moviesapp.data.defaultJson

    @Provides
    @Singleton
    fun provideMoviesApi(json: Json): MoviesApi = MoviesApi(json)

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): MoviesDatabase = Room.databaseBuilder(
        context,
        MoviesDatabase::class.java,
        "movies.db"
    ).build()

    @Provides
    @Singleton
    fun provideMoviesDao(db: MoviesDatabase): MoviesDao = db.moviesDao()

    @Provides
    @Singleton
    fun provideMoviesRepository(
        api: MoviesApi,
        dao: MoviesDao
    ): MoviesRepository = MoviesRepositoryImpl(api, dao)

    @Provides
    @Singleton
    fun provideGetGenresUseCase(
        repository: MoviesRepository
    ): GetGenresUseCase = GetGenresUseCase(repository)

    @Provides
    @Singleton
    fun provideGetMoviesPageUseCase(
        repository: MoviesRepository
    ): GetMoviesPageUseCase = GetMoviesPageUseCase(repository)
}


