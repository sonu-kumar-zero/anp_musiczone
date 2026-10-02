package com.example.musiczone.data

import android.content.Context
import android.net.Uri
import com.example.musiczone.model.Song
import org.json.JSONArray
import org.json.JSONObject
import androidx.core.content.edit

class SongCache(
    context: Context
) {

    private val preferences = context.getSharedPreferences(
        "music_cache",
        Context.MODE_PRIVATE
    )

    fun saveSongs(songs: List<Song>) {
        val jsonArray = JSONArray()

        songs.forEach { song ->
            jsonArray.put(
                JSONObject().apply {
                    put("id", song.id)
                    put("title", song.title)
                    put("artist", song.artist)
                    put("album", song.album)
                    put("duration", song.duration)
                    put("uri", song.uri.toString())
                }
            )
        }

        preferences.edit {
            putString("songs", jsonArray.toString())
        }
    }

    fun getSongs(): List<Song> {
        val json = preferences.getString("songs", null)
            ?: return emptyList()

        return try {
            val jsonArray = JSONArray(json)
            val songs = mutableListOf<Song>()

            for (index in 0 until jsonArray.length()) {
                val song = jsonArray.getJSONObject(index)

                songs.add(
                    Song(
                        id = song.getLong("id"),
                        title = song.getString("title"),
                        artist = song.getString("artist"),
                        album = song.getString("album"),
                        duration = song.getLong("duration"),
                        uri = Uri.parse(song.getString("uri"))
                    )
                )
            }

            songs
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun clear() {
        preferences.edit {
            clear()
        }
    }
}