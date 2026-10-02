package com.example.musiczone.playback

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.musiczone.model.Song

class MusicPlayer(
    context: Context
) {

    private val player = ExoPlayer.Builder(context).build()

    fun play(song: Song) {
        val mediaItem = MediaItem.fromUri(song.uri)

        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    fun release() {
        player.release()
    }
}