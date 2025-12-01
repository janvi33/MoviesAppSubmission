package com.simple.moviesapp.domain.repository

import com.simple.moviesapp.domain.model.Genre
import com.simple.moviesapp.domain.model.Movie

interface MoviesRepository {
    suspend fun getGenres(): List<Genre>
    suspend fun getMovies(
        genre: String?,
        from: Int
    ): List<Movie>
}


