package com.simple.moviesapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieDto(
    val id: String,
    val genres: List<String>,
    @SerialName("release_date") val releaseDate: String,
    val title: String,
    val overview: String,
    val url: String
)

