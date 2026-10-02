package com.example.musiczone.data

import android.content.Context
import androidx.core.content.edit

class FavoritesRepository(
    context: Context
) {
    private val preference = context.applicationContext.getSharedPreferences(
        "favorites",
        Context.MODE_PRIVATE
    )

    fun getFavoriteIds(): Set<Long> {
        return preference
            .getStringSet("song_ids", emptySet())
            .orEmpty()
            .mapNotNull { it.toLongOrNull() }
            .toSet()
    }

    fun isFavorite(songId: Long): Boolean {
        return getFavoriteIds().contains(songId)
    }

    fun addFavorite(songId: Long) {
        val ids = getFavoriteIds()
            .map { it.toString() }
            .toMutableSet()

        ids.add(songId.toString())

        preference.edit {
            putStringSet("song_ids", ids)
        }
    }

    fun removeFavorite(songId: Long) {
        val ids = getFavoriteIds()
            .map { it.toString() }
            .toMutableSet()

        ids.remove(songId.toString())

        preference.edit {
            putStringSet("song_ids", ids)
        }
    }

    fun toggleFavorite(songId: Long): Boolean {
        return if (isFavorite(songId)) {
            removeFavorite(songId)
            false
        } else {
            addFavorite(songId)
            true
        }
    }

}