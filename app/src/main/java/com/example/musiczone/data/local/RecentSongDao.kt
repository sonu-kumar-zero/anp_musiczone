package com.example.musiczone.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RecentSongDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recentSong: RecentSongEntity)

    @Query(
        """
        SELECT * FROM recent_songs
        ORDER BY lastPlayedAt DESC
        LIMIT :limit
        """
    )
    suspend fun getRecentSongs(
        limit: Int
    ): List<RecentSongEntity>

    @Query("DELETE FROM recent_songs")
    suspend fun clear()
}