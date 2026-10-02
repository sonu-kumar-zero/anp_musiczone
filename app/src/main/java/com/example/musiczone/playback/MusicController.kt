package com.example.musiczone.playback

import android.content.ComponentName
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.musiczone.model.Song
import com.google.common.util.concurrent.ListenableFuture

class MusicController(
    private val context: Context
) {

    private val sessionToken = SessionToken(
        context,
        ComponentName(
            context,
            MusicService::class.java
        )
    )

    private val controllerFuture: ListenableFuture<MediaController> =
        MediaController.Builder(
            context,
            sessionToken
        ).buildAsync()

    fun play(
        songs: List<Song>,
        startIndex: Int
    ) {
        controllerFuture.addListener(
            {
                val controller = controllerFuture.get()

                val mediaItems = songs.map { song ->
                    MediaItem.Builder()
                        .setMediaId(song.id.toString())
                        .setUri(song.uri)
                        .setMediaMetadata(
                            MediaMetadata.Builder()
                                .setTitle(song.title)
                                .setArtist(song.artist)
                                .setAlbumTitle(song.album)
                                .build()
                        )
                        .build()
                }

                controller.setMediaItems(
                    mediaItems,
                    startIndex,
                    0L
                )

                controller.prepare()
                controller.play()
            },
            ContextCompat.getMainExecutor(context)
        )
    }

    fun release() {
        MediaController.releaseFuture(controllerFuture)
    }
}