package com.simple.moviesapp

import com.simple.moviesapp.domain.model.Genre
import com.simple.moviesapp.domain.model.Movie
import com.simple.moviesapp.domain.repository.MoviesRepository
import com.simple.moviesapp.domain.usecase.GetGenresUseCase
import com.simple.moviesapp.domain.usecase.GetMoviesPageUseCase
import com.simple.moviesapp.ui.MoviesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MoviesViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `selectGenre updates state and loads movies for that genre`() = runTest {
        val repository = FakeMoviesRepository()
        val viewModel = MoviesViewModel(
            getGenres = GetGenresUseCase(repository),
            getMoviesPage = GetMoviesPageUseCase(repository)
        )

        // Allow initial refresh to complete
        dispatcher.scheduler.advanceUntilIdle()

        // Select a specific genre
        viewModel.selectGenre("Fantasy")
        dispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value

        assertEquals("Fantasy", state.selectedGenre)
        assertEquals(1, state.movies.size)
        assertEquals("Fantasy Movie", state.movies.first().title)
    }

    private class FakeMoviesRepository : MoviesRepository {
        override suspend fun getGenres(): List<Genre> =
            listOf(Genre(name = "Fantasy", count = 1))

        override suspend fun getMovies(genre: String?, from: Int): List<Movie> {
            val movie = Movie(
                id = "1",
                title = "Fantasy Movie",
                overview = "A test movie in the Fantasy genre.",
                genres = listOf(genre ?: "Fantasy"),
                releaseDate = "2020-01-01",
                url = "https://example.com"
            )
            return listOf(movie)
        }
    }
}


