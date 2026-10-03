package com.example.musiczone.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteGroupDao {

    @Query("SELECT * FROM favorite_groups ORDER BY name COLLATE NOCASE")
    fun observeGroups(): Flow<List<FavoriteGroupEntity>>

    @Query("SELECT * FROM favorite_groups WHERE id = :groupId")
    suspend fun getGroup(groupId: Long): FavoriteGroupEntity?

    @Insert
    suspend fun insertGroup(group: FavoriteGroupEntity): Long

    @Query("UPDATE favorite_groups SET name = :name WHERE id = :groupId")
    suspend fun renameGroup(
        groupId: Long, name: String
    )

    @Query("DELETE FROM favorite_groups WHERE id = :groupId")
    suspend fun deleteGroup(groupId: Long)

    @Insert
    suspend fun addSongToGroup(song: FavoriteGroupSongEntity)

    @Delete
    suspend fun removeSongFromGroup(song: FavoriteGroupSongEntity)

    @Query(
        """
        SELECT songId
        FROM favorite_group_songs
        WHERE groupId = :groupId
        ORDER BY songId
        """
    )
    fun observeSongIds(groupId: Long): Flow<List<Long>>

    @Query(
        """
        SELECT EXISTS(
            SELECT 1
            FROM favorite_group_songs
            WHERE groupId = :groupId
            AND songId = :songId
        )
        """
    )
    suspend fun isSongInGroup(
        groupId: Long, songId: Long
    ): Boolean

    @Query(
        """
        DELETE FROM favorite_group_songs
        WHERE groupId = :groupId
        """
    )
    suspend fun removeAllSongsFromGroup(groupId: Long)


    @Query("SELECT * FROM favorite_groups WHERE name = :name LIMIT 1")
    suspend fun getGroupByName(name: String): FavoriteGroupEntity?

    @Query(
        """
    SELECT groupId
    FROM favorite_group_songs
    WHERE songId = :songId
    """
    )
    suspend fun getGroupIdsForSong(
        songId: Long
    ): List<Long>
}