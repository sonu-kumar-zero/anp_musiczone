package com.example.musiczone.playback

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.audio.AudioSink
import androidx.media3.exoplayer.audio.DefaultAudioSink
import com.example.musiczone.playback.equalizer.NativeEqualizerAudioProcessor

@UnstableApi
class MusicRenderersFactory(
    context: Context
) : DefaultRenderersFactory(context) {

    private val equalizerProcessor = NativeEqualizerAudioProcessor()

    override fun buildAudioSink(
        context: Context, enableFloatOutput: Boolean, enableAudioOutputPlaybackParameters: Boolean
    ): AudioSink {

        return DefaultAudioSink.Builder(context).setAudioProcessors(
                arrayOf(equalizerProcessor)
            ).setEnableFloatOutput(
                enableFloatOutput
            ).setEnableAudioOutputPlaybackParameters(
                enableAudioOutputPlaybackParameters
            ).build()
    }

    fun setBandGain(
        band: Int, gainDb: Double
    ) {
        equalizerProcessor.setBandGain(
            band, gainDb
        )
    }
}
