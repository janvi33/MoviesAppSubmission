package com.simple.moviesapp.ui

import com.simple.moviesapp.domain.model.Genre
import com.simple.moviesapp.domain.model.Movie

data class MoviesUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val genres: List<Genre> = emptyList(),
    val selectedGenre: String? = null,
    val movies: List<Movie> = emptyList(),
    val hasMore: Boolean = true,
    val isLoadingMore: Boolean = false
)