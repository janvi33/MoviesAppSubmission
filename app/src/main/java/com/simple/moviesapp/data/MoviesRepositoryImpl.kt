package com.simple.moviesapp.data

import com.simple.moviesapp.data.local.MoviesDao
import com.simple.moviesapp.data.mapper.toDomain
import com.simple.moviesapp.data.mapper.toEntity
import com.simple.moviesapp.data.remote.MoviesApiService
import com.simple.moviesapp.data.remote.dto.GenreDto
import com.simple.moviesapp.domain.model.Genre
import com.simple.moviesapp.domain.model.Movie
import com.simple.moviesapp.domain.repository.MoviesRepository
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.contentOrNull

class MoviesRepositoryImpl(
    private val api: MoviesApiService,
    private val dao: MoviesDao,
    private val json: Json
) : MoviesRepository {
    private val pageSize = 100

    override suspend fun getGenres(): List<Genre> =
        dao.getGenres().takeIf { it.isNotEmpty() }?.map { it.toDomain() }
            ?: run {
                val response = api.getGenres()
                if (response.isSuccessful) {
                    val body = response.body()?.string() ?: return emptyList()
                    val element: JsonElement = json.parseToJsonElement(body)
                    val genres = element.jsonArray.mapNotNull { item ->
                        val tuple = item.jsonArray
                        val name = tuple.getOrNull(0)?.jsonPrimitive?.contentOrNull
                        val count = tuple.getOrNull(1)?.jsonPrimitive?.intOrNull
                        if (name != null && count != null) {
                            GenreDto(name = name, count = count)
                        } else {
                            null
                        }
                    }.map { it.toDomain() }
                    dao.insertGenres(genres.map { it.toEntity() })
                    genres
                } else {
                    emptyList()
                }
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

        val remote = api.getMovies(
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



