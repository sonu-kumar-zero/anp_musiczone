package com.example.musiczone.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [FavoriteGroupEntity::class, FavoriteGroupSongEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MusicDatabase : RoomDatabase() {

    abstract fun favoriteGroupDao(): FavoriteGroupDao
}