package com.simple.moviesapp.domain.model

data class Genre(
    val name: String,
    val count: Int
)

data class Movie(
    val id: String,
    val genres: List<String>,
    val releaseDate: String,
    val title: String,
    val overview: String,
    val url: String
)


