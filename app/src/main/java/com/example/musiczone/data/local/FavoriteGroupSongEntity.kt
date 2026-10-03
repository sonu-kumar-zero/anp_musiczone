package com.example.musiczone.data.local

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "favorite_group_songs",
    primaryKeys = ["groupId", "songId"],
    foreignKeys = [ForeignKey(
        entity = FavoriteGroupEntity::class,
        parentColumns = ["id"],
        childColumns = ["groupId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class FavoriteGroupSongEntity(
    val groupId: Long, val songId: Long
)