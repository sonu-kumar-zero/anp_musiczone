package com.example.musiczone.data

import android.content.Context
import androidx.room.Room
import com.example.musiczone.data.local.FavoriteGroupEntity
import com.example.musiczone.data.local.FavoriteGroupSongEntity
import com.example.musiczone.data.local.MusicDatabase
import kotlinx.coroutines.flow.Flow

class FavoriteGroupsRepository(
    context: Context
) {
    private val database = Room.databaseBuilder(
        context.applicationContext, MusicDatabase::class.java, "musiczone.db"
    )
        .addMigrations(MusicDatabase.MIGRATION_1_2)
        .build()

    private val dao = database.favoriteGroupDao()

    fun observeGroups(): Flow<List<FavoriteGroupEntity>> {
        return dao.observeGroups()
    }

    fun observeSongIds(groupId: Long): Flow<List<Long>> {
        return dao.observeSongIds(groupId)
    }

    suspend fun createGroup(name: String): Long {
        return dao.insertGroup(
            FavoriteGroupEntity(name = name)
        )
    }

    suspend fun renameGroup(
        groupId: Long, name: String
    ) {
        dao.renameGroup(groupId, name)
    }

    suspend fun deleteGroup(groupId: Long) {
        dao.deleteGroup(groupId)
    }

    suspend fun addSongToGroup(
        groupId: Long, songId: Long
    ) {
        if (!dao.isSongInGroup(groupId, songId)) {
            dao.addSongToGroup(
                FavoriteGroupSongEntity(
                    groupId = groupId, songId = songId
                )
            )
        }
    }

    suspend fun removeSongFromGroup(
        groupId: Long, songId: Long
    ) {
        dao.removeSongFromGroup(
            FavoriteGroupSongEntity(
                groupId = groupId, songId = songId
            )
        )
    }

    suspend fun ensureDefaultGroup() {
        val existingGroup = dao.getGroupByName("Favorites")

        if (existingGroup == null) {
            dao.insertGroup(
                FavoriteGroupEntity(name = "Favorites")
            )
        }
    }

    suspend fun getGroupIdsForSong(
        songId: Long
    ): List<Long> {
        return dao.getGroupIdsForSong(songId)
    }

    suspend fun getGroupByName(name: String): FavoriteGroupEntity? {
        return dao.getGroupByName(name)
    }

    suspend fun getGroup(groupId: Long): FavoriteGroupEntity? {
        return dao.getGroup(groupId)
    }

    fun close() {
        database.close()
    }
}