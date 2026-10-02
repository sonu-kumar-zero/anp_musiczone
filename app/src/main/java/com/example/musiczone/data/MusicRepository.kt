
package com.example.musiczone.data

import android.content.ContentResolver
import android.provider.MediaStore
import com.example.musiczone.model.Song

class MusicRepository(
    private val contentResolver: ContentResolver
) {

    fun getSongs(): List<Song> {
        val songs = mutableListOf<Song>()

        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

        contentResolver.query(
            collection,
            projection,
            selection,
            null,
            "${MediaStore.Audio.Media.TITLE} ASC"
        )?.use { cursor ->

            val idColumn = cursor.getColumnIndexOrThrow(
                MediaStore.Audio.Media._ID
            )

            val titleColumn = cursor.getColumnIndexOrThrow(
                MediaStore.Audio.Media.TITLE
            )

            val artistColumn = cursor.getColumnIndexOrThrow(
                MediaStore.Audio.Media.ARTIST
            )

            val albumColumn = cursor.getColumnIndexOrThrow(
                MediaStore.Audio.Media.ALBUM
            )

            val durationColumn = cursor.getColumnIndexOrThrow(
                MediaStore.Audio.Media.DURATION
            )

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)

                val songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    .buildUpon()
                    .appendPath(id.toString())
                    .build()

                songs.add(
                    Song(
                        id = id,
                        title = cursor.getString(titleColumn),
                        artist = cursor.getString(artistColumn),
                        album = cursor.getString(albumColumn),
                        duration = cursor.getLong(durationColumn),
                        uri = songUri,
                    )
                )
            }
        }

        return songs
    }
}
