package com.simple.moviesapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MoviesDao {

    @Query("SELECT * FROM genres ORDER BY name")
    suspend fun getGenres(): List<GenreEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenres(genres: List<GenreEntity>)

    @Query(
        """
        SELECT * FROM movies
        WHERE (:genre IS NULL OR genres LIKE '%' || :genre || '%')
        ORDER BY title
        LIMIT :limit OFFSET :offset
        """
    )
    suspend fun getMovies(
        genre: String?,
        limit: Int,
        offset: Int
    ): List<MovieEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieEntity>)
}


