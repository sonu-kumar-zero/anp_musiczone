package com.example.musiczone.playback

import android.content.ComponentName
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.musiczone.model.Song
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class MusicController(
    private val context: Context
) {

    private val _currentMediaItem = MutableStateFlow<MediaItem?>(null)
    val currentMediaItem: StateFlow<MediaItem?> = _currentMediaItem.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _repeatMode = MutableStateFlow(Player.REPEAT_MODE_OFF)
    val repeatMode: StateFlow<Int> = _repeatMode.asStateFlow()

    private val _isShuffleEnabled = MutableStateFlow(false)
    val isShuffleEnabled: StateFlow<Boolean> =
        _isShuffleEnabled.asStateFlow()

    private var positionJob: Job? = null

    private val sessionToken = SessionToken(
        context,
        ComponentName(
            context,
            MusicService::class.java
        )
    )

    private var controller: MediaController? = null

    private val playerListener = object : Player.Listener {

        override fun onMediaItemTransition(
            mediaItem: MediaItem?,
            reason: Int
        ) {
            _currentMediaItem.value = mediaItem
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_READY) {
                _duration.value = controller?.duration ?: 0L
            }
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            _isShuffleEnabled.value = shuffleModeEnabled
        }

        override fun onRepeatModeChanged(
            repeatMode: Int
        ) {
            _repeatMode.value = repeatMode
        }
    }

    fun togglePlayPause() {
        controller?.let { mediaController ->
            if (mediaController.isPlaying) {
                mediaController.pause()
            } else {
                mediaController.play()
            }
        }
    }

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
                val mediaController = controllerFuture.get()

                if (controller == null) {
                    controller = mediaController
                    controller?.addListener(playerListener)
                }

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

                controller?.setMediaItems(
                    mediaItems,
                    startIndex,
                    0L
                )

                _currentMediaItem.value = mediaItems[startIndex]

                controller?.prepare()
                controller?.play()
                startPositionUpdates()
            },
            ContextCompat.getMainExecutor(context)
        )
    }

    private fun startPositionUpdates() {
        positionJob?.cancel()

        positionJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                _currentPosition.value = controller?.currentPosition ?: 0L
                delay(500.milliseconds)
            }
        }
    }

    fun seekTo(position: Long) {
        controller?.seekTo(position)
    }

    fun previousSong() {
        controller?.seekToPreviousMediaItem()
    }

    fun nextSong() {
        controller?.seekToNextMediaItem()
    }

    fun toggleShuffle() {
        controller?.let { mediaController ->
            mediaController.shuffleModeEnabled =
                !mediaController.shuffleModeEnabled
        }
    }

    fun cycleRepeatMode() {
        controller?.let { mediaController ->
            mediaController.repeatMode = when (mediaController.repeatMode) {
                Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
                Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
                else -> Player.REPEAT_MODE_OFF
            }
        }
    }

    fun release() {
        positionJob?.cancel()
        controller?.removeListener(playerListener)
        MediaController.releaseFuture(controllerFuture)
    }
}