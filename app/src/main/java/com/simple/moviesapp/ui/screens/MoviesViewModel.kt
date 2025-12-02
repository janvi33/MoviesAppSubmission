package com.simple.moviesapp.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simple.moviesapp.domain.model.Genre
import com.simple.moviesapp.domain.model.Movie
import com.simple.moviesapp.domain.usecase.GetGenresUseCase
import com.simple.moviesapp.domain.usecase.GetMoviesPageUseCase
import com.simple.moviesapp.ui.MoviesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

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
        loadJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                // Load genres and movies in parallel
                val genresDeferred = async {
                    getGenres()
                        .catch { t ->
                            _state.value = _state.value.copy(
                                isLoading = false,
                                error = t.message ?: "Unknown error"
                            )
                            emptyList<Genre>()
                        }
                        .first()
                }
                
                val moviesDeferred = async {
                    getMoviesPage(
                        genre = _state.value.selectedGenre,
                        from = 0
                    )
                        .catch { t ->
                            _state.value = _state.value.copy(
                                isLoading = false,
                                error = t.message ?: "Unknown error"
                            )
                            emptyList<Movie>()
                        }
                        .first()
                }

                // Await both results
                val (genres, movies) = awaitAll(genresDeferred, moviesDeferred)
                
                _state.value = _state.value.copy(
                    isLoading = false,
                    genres = genres as List<Genre>,
                    movies = movies as List<Movie>,
                    hasMore = movies.isNotEmpty()
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
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
