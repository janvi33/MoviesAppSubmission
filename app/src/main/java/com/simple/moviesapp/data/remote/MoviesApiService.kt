package com.simple.moviesapp.data.remote

import com.simple.moviesapp.data.remote.dto.MovieDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesApiService {
    @GET("api/genres")
    suspend fun getGenres(): Response<ResponseBody>

    @GET("api/movies")
    suspend fun getMovies(
        @Query("limit") limit: Int,
        @Query("from") from: Int,
        @Query("genre") genre: String? = null
    ): List<MovieDto>
}

