package com.simple.moviesapp.data.mapper

import com.simple.moviesapp.data.local.MovieEntity
import com.simple.moviesapp.data.remote.dto.MovieDto
import com.simple.moviesapp.domain.model.Movie

fun MovieDto.toDomain(): Movie = Movie(
    id = id,
    genres = genres,
    releaseDate = releaseDate,
    title = title,
    overview = overview,
    url = url
)

fun MovieDto.toEntity(): MovieEntity = MovieEntity(
    id = id,
    title = title,
    overview = overview,
    releaseDate = releaseDate,
    genres = genres.joinToString(","),
    url = url
)

fun MovieEntity.toDomain(): Movie = Movie(
    id = id,
    title = title,
    overview = overview,
    releaseDate = releaseDate,
    genres = genres.split(",").filter { it.isNotBlank() },
    url = url
)

