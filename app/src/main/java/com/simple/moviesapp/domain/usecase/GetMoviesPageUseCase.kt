package com.simple.moviesapp.domain.usecase

import com.simple.moviesapp.domain.model.Movie
import com.simple.moviesapp.domain.repository.MoviesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetMoviesPageUseCase(
    private val repository: MoviesRepository
) {
    operator fun invoke(
        genre: String?,
        from: Int
    ): Flow<List<Movie>> = flow {
        emit(repository.getMovies(genre, from))
    }
}


