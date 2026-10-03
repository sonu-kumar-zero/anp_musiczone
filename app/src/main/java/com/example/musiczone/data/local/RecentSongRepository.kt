package com.example.musiczone.data.local

import android.content.Context
import androidx.room.Room

class RecentSongRepository(
    context: Context
) {

    private val database = Room.databaseBuilder(
        context.applicationContext,
        MusicDatabase::class.java,
        "musiczone.db"
    )
        .addMigrations(MusicDatabase.MIGRATION_1_2)
        .build()

    private val dao = database.recentSongDao()

    suspend fun recordSongPlayed(songId: Long) {
        dao.insert(
            RecentSongEntity(
                songId = songId,
                lastPlayedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun getRecentSongs(
        limit: Int = 20
    ): List<RecentSongEntity> {
        return dao.getRecentSongs(limit)
    }

    suspend fun clear() {
        dao.clear()
    }

    fun close() {
        database.close()
    }
}