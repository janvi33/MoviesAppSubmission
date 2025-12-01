package com.simple.moviesapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simple.moviesapp.domain.model.Genre
import com.simple.moviesapp.domain.model.Movie
import com.simple.moviesapp.domain.usecase.GetGenresUseCase
import com.simple.moviesapp.domain.usecase.GetMoviesPageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MoviesUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val genres: List<Genre> = emptyList(),
    val selectedGenre: String? = null,
    val movies: List<Movie> = emptyList(),
    val hasMore: Boolean = true,
    val isLoadingMore: Boolean = false
)

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val getGenres: GetGenresUseCase,
    private val getMoviesPage: GetMoviesPageUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MoviesUiState(isLoading = true))
    val state: StateFlow<MoviesUiState> = _state.asStateFlow()

    private var loadJob: Job? = null

    init {
        refresh()
    }

    fun refresh() {
        loadJob?.cancel()
        _state.value = _state.value.copy(
            isLoading = true,
            error = null,
            movies = emptyList(),
            hasMore = true
        )
        loadJob = viewModelScope.launch {
            // Load genres
            getGenres()
                .catch { t ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = t.message ?: "Unknown error"
                    )
                }
                .collect { genres ->
                    _state.value = _state.value.copy(genres = genres)
                }

            // Load first page of movies for current genre
            getMoviesPage(
                genre = _state.value.selectedGenre,
                from = 0
            )
                .catch { t ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = t.message ?: "Unknown error"
                    )
                }
                .collect { movies ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        movies = movies,
                        hasMore = movies.isNotEmpty()
                    )
                }
        }
    }

    fun selectGenre(genre: String?) {
        _state.value = _state.value.copy(selectedGenre = genre)
        refresh()
    }

    //Loads the next page when scrolling.
    fun loadMore() {
        val current = _state.value
        if (current.isLoadingMore || !current.hasMore || current.isLoading) return
        _state.value = current.copy(isLoadingMore = true)
        viewModelScope.launch {
            getMoviesPage(
                genre = current.selectedGenre,
                from = current.movies.size
            )
                .catch { t ->
                    _state.value = _state.value.copy(
                        isLoadingMore = false,
                        error = t.message ?: "Unknown error"
                    )
                }
                .collect { more ->
                    val updated = _state.value
                    _state.value = updated.copy(
                        movies = updated.movies + more,
                        isLoadingMore = false,
                        hasMore = more.isNotEmpty()
                    )
                }
        }
    }
}


