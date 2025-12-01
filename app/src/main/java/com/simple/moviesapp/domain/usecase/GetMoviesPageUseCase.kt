package com.simple.moviesapp.domain.usecase

import com.simple.moviesapp.domain.model.Movie
import com.simple.moviesapp.domain.repository.MoviesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class GetMoviesPageUseCase(
    private val repository: MoviesRepository
) {
    operator fun invoke(
        genre: String?,
        from: Int
    ): Flow<List<Movie>> = flow {
        val movies = withContext(Dispatchers.IO) {
            repository.getMovies(genre = genre, from = from)
        }
        emit(movies)
    }
}


