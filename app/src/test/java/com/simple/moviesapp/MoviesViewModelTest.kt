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
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MoviesViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }


    // Test 1 — Selecting a genre loads filtered movies

    @Test
    fun `selectGenre updates state and loads movies for that genre`() = runTest(dispatcher) {
        val repo = FakeMoviesRepository()
        val vm = MoviesViewModel(GetGenresUseCase(repo), GetMoviesPageUseCase(repo))

        runCurrent()

        vm.selectGenre("Fantasy")
        runCurrent()

        val state = vm.state.value

        assertEquals("Fantasy", state.selectedGenre)
        assertEquals(1, state.movies.size)
        assertEquals("Fantasy Movie", state.movies.first().title)
    }

    // Test 2 — Initial refresh loads genres and first movie page
    @Test
    fun `initial refresh loads genres and movies`() = runTest(dispatcher) {
        val repo = FakeMoviesRepository()
        val vm = MoviesViewModel(GetGenresUseCase(repo), GetMoviesPageUseCase(repo))

        runCurrent()

        val state = vm.state.value

        assertTrue(state.genres.isNotEmpty())
        assertEquals(1, state.movies.size)
        assertEquals("Fantasy Movie", state.movies.first().title)
    }


    // Test 3 — loadMore appends movies to existing list
    @Test
    fun `loadMore appends movies to the existing list`() = runTest(dispatcher) {
        val repo = FakeMoviesRepositoryWithPagination()
        val vm = MoviesViewModel(GetGenresUseCase(repo), GetMoviesPageUseCase(repo))

        runCurrent() // initial load = 1 movie

        vm.loadMore()
        runCurrent() // second page

        val state = vm.state.value

        // Should now have two movies stacked
        assertEquals(2, state.movies.size)
        assertEquals("Movie 1", state.movies[0].title)
        assertEquals("Movie 2", state.movies[1].title)
    }


    // Test 4 — Repository throws → ViewModel exposes error
    @Test
    fun `when repository throws error state should contain error`() = runTest(dispatcher) {
        val repo = FakeErrorRepository()
        val vm = MoviesViewModel(GetGenresUseCase(repo), GetMoviesPageUseCase(repo))

        runCurrent() // init should fail

        val state = vm.state.value

        assertNotNull(state.error)
        assertTrue(state.error!!.contains("boom"))
        assertTrue(state.movies.isEmpty())
    }


    // Fake repositories used for tests
    private class FakeMoviesRepository : MoviesRepository {
        override suspend fun getGenres(): List<Genre> =
            listOf(Genre("Fantasy", 1))

        override suspend fun getMovies(genre: String?, from: Int): List<Movie> =
            listOf(
                Movie(
                    id = "1",
                    genres = listOf(genre ?: "Fantasy"),
                    releaseDate = "2020",
                    title = "Fantasy Movie",
                    overview = "A test movie",
                    url = "https://example.com"
                )
            )
    }

    // Fake repository that returns 2 pages: movie1 then movie2
    private class FakeMoviesRepositoryWithPagination : MoviesRepository {
        override suspend fun getGenres(): List<Genre> =
            listOf(Genre("Test", 2))

        override suspend fun getMovies(genre: String?, from: Int): List<Movie> =
            when (from) {
                0 -> listOf(
                    Movie("1", listOf("Test"), "2020", "Movie 1", "Overview", "")
                )
                else -> listOf(
                    Movie("2", listOf("Test"), "2020", "Movie 2", "Overview", "")
                )
            }
    }

    // Fake error repo
    private class FakeErrorRepository : MoviesRepository {
        override suspend fun getGenres(): List<Genre> {
            throw RuntimeException("boom genres")
        }

        override suspend fun getMovies(genre: String?, from: Int): List<Movie> {
            throw RuntimeException("boom movies")
        }
    }
}
