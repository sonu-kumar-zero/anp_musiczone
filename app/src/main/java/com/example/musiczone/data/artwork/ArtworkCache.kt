package com.example.musiczone.data.artwork

import android.content.Context
import androidx.core.content.edit

class ArtworkCache(
    context: Context
) {
    private val preferences = context.applicationContext
        .getSharedPreferences(
            "artwork_cache",
            Context.MODE_PRIVATE
        )

    fun getArtworkUrl(
        artist: String,
        album: String
    ): String? {
        return preferences.getString(
            createKey(artist, album),
            null
        )
    }

    fun saveArtworkUrl(
        artist: String,
        album: String,
        artworkUrl: String
    ) {
        preferences.edit {
            putString(
                createKey(artist, album),
                artworkUrl
            )
        }
    }

    private fun createKey(
        artist: String,
        album: String
    ): String {
        return "${artist.trim().lowercase()}::${album.trim().lowercase()}"
    }
}