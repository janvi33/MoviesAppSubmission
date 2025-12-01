package com.simple.moviesapp.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.contentOrNull
import java.net.HttpURLConnection
import java.net.URL

private const val BASE_URL = "https://movies-app-backend.replit.app"

@Serializable
data class GenreDto(
    val name: String,
    val count: Int
)

@Serializable
data class MovieDto(
    val id: String,
    val genres: List<String>,
    @SerialName("release_date") val releaseDate: String,
    val title: String,
    val overview: String,
    val url: String
)

class MoviesApi(
    private val json: Json = defaultJson
) {

    suspend fun fetchGenres(): List<GenreDto> {
        val raw = get("$BASE_URL/api/genres")
        val element: JsonElement = json.parseToJsonElement(raw)

        return element.jsonArray.mapNotNull { item ->
            val tuple = item.jsonArray
            val name = tuple.getOrNull(0)?.jsonPrimitive?.contentOrNull
            val count = tuple.getOrNull(1)?.jsonPrimitive?.intOrNull
            if (name != null && count != null) {
                GenreDto(name = name, count = count)
            } else {
                null
            }
        }
    }

    suspend fun fetchMovies(
        limit: Int,
        from: Int,
        genre: String?
    ): List<MovieDto> {
        val params = buildString {
            append("?limit=$limit&from=$from")
            if (!genre.isNullOrEmpty()) {
                append("&genre=")
                append(encode(genre))
            }
        }
        val raw = get("$BASE_URL/api/movies$params")
        return json.decodeFromString(raw)
    }

    private fun encode(value: String): String =
        java.net.URLEncoder.encode(value, Charsets.UTF_8.name())

    private fun get(url: String): String {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.connectTimeout = 10_000
        connection.readTimeout = 10_000
        connection.requestMethod = "GET"
        connection.doInput = true
        try {
            val code = connection.responseCode
            val stream = if (code in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream ?: connection.inputStream
            }
            val body = stream.bufferedReader().use { it.readText() }
            if (code !in 200..299) {
                throw RuntimeException("HTTP $code: $body")
            }
            return body
        } finally {
            connection.disconnect()
        }
    }
}

val defaultJson: Json = Json {
    ignoreUnknownKeys = true
    isLenient = true
}
