package com.example.musiczone.playback

import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import android.os.Bundle
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.Futures

@UnstableApi
class MusicService : MediaSessionService() {

    private lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaSession
    private lateinit var renderersFactory: MusicRenderersFactory

    companion object {
        const val COMMAND_SET_EQ_BAND = "com.example.musiczone.SET_EQ_BAND"

        const val EXTRA_EQ_BAND = "eq_band"
        const val EXTRA_EQ_GAIN_DB = "eq_gain_db"
    }

    private val sessionCallback = object : MediaSession.Callback {

        override fun onConnect(
            session: MediaSession, controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult {

            val connectionResult = super.onConnect(
                session, controller
            )

            val commands = connectionResult.availableSessionCommands.buildUpon().add(
                SessionCommand(
                    COMMAND_SET_EQ_BAND, Bundle.EMPTY
                )
            ).build()

            return MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                .setAvailableSessionCommands(
                    commands
                ).build()
        }

        override fun onCustomCommand(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            customCommand: SessionCommand,
            args: Bundle
        ) = when (customCommand.customAction) {

            COMMAND_SET_EQ_BAND -> {

                val band = args.getInt(
                    EXTRA_EQ_BAND, -1
                )

                val gainDb = args.getDouble(
                    EXTRA_EQ_GAIN_DB, 0.0
                )

                renderersFactory.setBandGain(
                    band, gainDb
                )

                Futures.immediateFuture(
                    SessionResult(
                        SessionResult.RESULT_SUCCESS
                    )
                )
            }

            else -> super.onCustomCommand(
                session, controller, customCommand, args
            )
        }
    }

    override fun onCreate() {
        super.onCreate()

        renderersFactory = MusicRenderersFactory(this)

        player = ExoPlayer.Builder(
            this, renderersFactory
        ).build()

        val audioAttributes = AudioAttributes
            .Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()

        player.setAudioAttributes(
            audioAttributes, true
        )

        mediaSession = MediaSession
            .Builder(
                this, player
            )
            .setCallback(sessionCallback)
            .build()
    }

    override fun onGetSession(
        controllerInfo: MediaSession.ControllerInfo
    ): MediaSession {
        return mediaSession
    }

    override fun onDestroy() {
        mediaSession.release()
        player.release()

        super.onDestroy()
    }
}