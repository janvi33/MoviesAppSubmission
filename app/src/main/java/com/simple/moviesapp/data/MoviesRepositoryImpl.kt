package com.simple.moviesapp.data

import com.simple.moviesapp.data.local.GenreEntity
import com.simple.moviesapp.data.local.MovieEntity
import com.simple.moviesapp.data.local.MoviesDao
import com.simple.moviesapp.domain.model.Genre
import com.simple.moviesapp.domain.model.Movie
import com.simple.moviesapp.domain.repository.MoviesRepository

class MoviesRepositoryImpl(
    private val api: MoviesApi,
    private val dao: MoviesDao
) : MoviesRepository {
    private val pageSize = 100

    override suspend fun getGenres(): List<Genre> =
        dao.getGenres().takeIf { it.isNotEmpty() }?.map { it.toDomain() }
            ?: api.fetchGenres()
                .map { it.toDomain() }
                .also { genres ->
                    dao.insertGenres(genres.map { it.toEntity() })
                }

    override suspend fun getMovies(
        genre: String?,
        from: Int
    ): List<Movie> {
        val local = dao.getMovies(
            genre = genre,
            limit = pageSize,
            offset = from
        )
        if (local.isNotEmpty()) {
            return local.map { it.toDomain() }
        }

        val remote = api.fetchMovies(
            limit = pageSize,
            from = from,
            genre = genre
        )
        if (remote.isNotEmpty()) {
            dao.insertMovies(remote.map { it.toEntity() })
        }
        return remote.map { it.toDomain() }
    }
}

private fun GenreDto.toDomain(): Genre = Genre(
    name = name,
    count = count
)

private fun Genre.toEntity(): GenreEntity = GenreEntity(
    name = name,
    count = count
)

private fun GenreEntity.toDomain(): Genre = Genre(
    name = name,
    count = count
)

private fun MovieDto.toDomain(): Movie = Movie(
    id = id,
    genres = genres,
    releaseDate = releaseDate,
    title = title,
    overview = overview,
    url = url
)

private fun MovieDto.toEntity(): MovieEntity = MovieEntity(
    id = id,
    title = title,
    overview = overview,
    releaseDate = releaseDate,
    genres = genres.joinToString(","),
    url = url
)

private fun MovieEntity.toDomain(): Movie = Movie(
    id = id,
    title = title,
    overview = overview,
    releaseDate = releaseDate,
    genres = genres.split(",").filter { it.isNotBlank() },
    url = url
)



