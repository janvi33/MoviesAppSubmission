package com.simple.moviesapp.data.mapper

import com.simple.moviesapp.data.local.GenreEntity
import com.simple.moviesapp.data.remote.dto.GenreDto
import com.simple.moviesapp.domain.model.Genre

fun GenreDto.toDomain(): Genre = Genre(
    name = name,
    count = count
)

fun Genre.toEntity(): GenreEntity = GenreEntity(
    name = name,
    count = count
)

fun GenreEntity.toDomain(): Genre = Genre(
    name = name,
    count = count
)

