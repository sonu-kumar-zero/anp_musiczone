package com.example.musiczone.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        FavoriteGroupEntity::class,
        FavoriteGroupSongEntity::class,
        RecentSongEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class MusicDatabase : RoomDatabase() {

    abstract fun favoriteGroupDao(): FavoriteGroupDao
    abstract fun recentSongDao(): RecentSongDao

    companion object {

        val MIGRATION_1_2 = object : Migration(1, 2) {

            override fun migrate(
                database: SupportSQLiteDatabase
            ) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS recent_songs (
                        songId INTEGER NOT NULL,
                        lastPlayedAt INTEGER NOT NULL,
                        PRIMARY KEY(songId)
                    )
                    """.trimIndent()
                )
            }
        }
    }
}