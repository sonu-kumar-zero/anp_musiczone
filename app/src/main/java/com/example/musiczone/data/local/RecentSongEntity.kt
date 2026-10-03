package com.example.musiczone.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_songs")
data class RecentSongEntity(
    @PrimaryKey
    val songId: Long,
    val lastPlayedAt: Long
)