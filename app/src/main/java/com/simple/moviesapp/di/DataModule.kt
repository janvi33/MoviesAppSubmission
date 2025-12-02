package com.simple.moviesapp.di

import android.content.Context
import androidx.room.Room
import com.simple.moviesapp.data.MoviesRepositoryImpl
import com.simple.moviesapp.data.local.MoviesDao
import com.simple.moviesapp.data.local.MoviesDatabase
import com.simple.moviesapp.data.remote.MoviesApiService
import com.simple.moviesapp.domain.repository.MoviesRepository
import com.simple.moviesapp.domain.usecase.GetGenresUseCase
import com.simple.moviesapp.domain.usecase.GetMoviesPageUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.MediaType.Companion.toMediaType
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    private const val BASE_URL = "https://movies-app-backend.replit.app/"

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideMoviesApiService(retrofit: Retrofit): MoviesApiService =
        retrofit.create(MoviesApiService::class.java)

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
        api: MoviesApiService,
        dao: MoviesDao,
        json: Json
    ): MoviesRepository = MoviesRepositoryImpl(api, dao, json)

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


