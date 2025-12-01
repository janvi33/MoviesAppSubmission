package com.simple.moviesapp.domain.usecase

import com.simple.moviesapp.domain.model.Genre
import com.simple.moviesapp.domain.repository.MoviesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class GetGenresUseCase(
    private val repository: MoviesRepository
) {
    operator fun invoke(): Flow<List<Genre>> = flow {
        val genres = withContext(Dispatchers.IO) {
            repository.getGenres()
        }
        emit(genres)
    }
}


