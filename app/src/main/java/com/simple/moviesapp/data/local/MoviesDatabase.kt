package com.simple.moviesapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [GenreEntity::class, MovieEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MoviesDatabase : RoomDatabase() {
    abstract fun moviesDao(): MoviesDao
}
