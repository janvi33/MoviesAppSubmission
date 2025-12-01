package com.simple.moviesapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "genres")
data class GenreEntity(
    @PrimaryKey val name: String,
    val count: Int
)

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val id: String,
    val title: String,
    val overview: String,
    val releaseDate: String,
    val genres: String,
    val url: String
)


