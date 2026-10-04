package com.example.musiczone.playback.equalizer

import androidx.media3.common.C
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.util.UnstableApi
import java.nio.ByteBuffer
import java.nio.ByteOrder

@UnstableApi
class NativeEqualizerAudioProcessor : AudioProcessor {

    private var inputAudioFormat = AudioProcessor.AudioFormat.NOT_SET

    private var outputAudioFormat = AudioProcessor.AudioFormat.NOT_SET

    private var outputBuffer = AudioProcessor.EMPTY_BUFFER

    private var inputEnded = false

    private var equalizerHandle: Long = 0L

    private var floatBuffer: FloatArray? = null

    private var outputCapacity = 0

    override fun configure(
        inputAudioFormat: AudioProcessor.AudioFormat
    ): AudioProcessor.AudioFormat {

        if (inputAudioFormat.encoding != C.ENCODING_PCM_16BIT || inputAudioFormat.channelCount != 2) {
            throw AudioProcessor.UnhandledAudioFormatException(
                inputAudioFormat
            )
        }

        this.inputAudioFormat = inputAudioFormat

        if (equalizerHandle != 0L) {
            destroyEqualizer(equalizerHandle)
        }

        equalizerHandle = createEqualizer(
            inputAudioFormat.sampleRate
        )

        outputAudioFormat = inputAudioFormat

        return outputAudioFormat
    }

    override fun isActive(): Boolean {
        return inputAudioFormat != AudioProcessor.AudioFormat.NOT_SET
    }

    override fun queueInput(
        inputBuffer: ByteBuffer
    ) {
        if (!inputBuffer.hasRemaining()) {
            return
        }

        val sampleCount = inputBuffer.remaining() / Short.SIZE_BYTES

        if (floatBuffer == null || floatBuffer!!.size < sampleCount) {
            floatBuffer = FloatArray(sampleCount)
        }

        val samples = floatBuffer!!

        val input = inputBuffer.duplicate().order(ByteOrder.nativeOrder())

        for (index in 0 until sampleCount) {
            samples[index] = input.short.toFloat() / 32768.0f
        }

        inputBuffer.position(inputBuffer.limit())

        processEqualizer(
            equalizerHandle, samples
        )

        val requiredCapacity = sampleCount * Short.SIZE_BYTES

        if (outputBuffer === AudioProcessor.EMPTY_BUFFER || outputCapacity < requiredCapacity) {
            outputBuffer =
                ByteBuffer.allocateDirect(requiredCapacity).order(ByteOrder.nativeOrder())

            outputCapacity = requiredCapacity
        } else {
            outputBuffer.clear()
        }

        for (index in 0 until sampleCount) {

            val clamped = samples[index].coerceIn(-1.0f, 1.0f)

            val pcm = (clamped * 32767.0f).toInt().toShort()

            outputBuffer.putShort(pcm)
        }

        outputBuffer.flip()
    }

    override fun queueEndOfStream() {
        inputEnded = true
    }

    override fun getOutput(): ByteBuffer {
        val buffer = outputBuffer

        outputBuffer = AudioProcessor.EMPTY_BUFFER

        return buffer
    }

    override fun isEnded(): Boolean {
        return inputEnded && outputBuffer === AudioProcessor.EMPTY_BUFFER
    }

    override fun flush() {
        outputBuffer = AudioProcessor.EMPTY_BUFFER

        inputEnded = false

        if (equalizerHandle != 0L) {
            resetEqualizer(equalizerHandle)
        }
    }

    override fun reset() {
        outputBuffer = AudioProcessor.EMPTY_BUFFER

        inputEnded = false

        if (equalizerHandle != 0L) {
            destroyEqualizer(equalizerHandle)
            equalizerHandle = 0L
        }

        floatBuffer = null
        outputCapacity = 0

        inputAudioFormat = AudioProcessor.AudioFormat.NOT_SET

        outputAudioFormat = AudioProcessor.AudioFormat.NOT_SET
    }

    fun setBandGain(
        band: Int,
        gainDb: Double
    ) {
        if (equalizerHandle == 0L) {
            return
        }

        setEqualizerBandGain(
            equalizerHandle,
            band,
            gainDb
        )
    }

    private external fun setEqualizerBandGain(
        handle: Long,
        band: Int,
        gainDb: Double
    )

    private external fun createEqualizer(
        sampleRate: Int
    ): Long

    private external fun destroyEqualizer(
        handle: Long
    )

    private external fun processEqualizer(
        handle: Long, input: FloatArray
    )

    private external fun resetEqualizer(
        handle: Long
    )

    companion object {
        init {
            System.loadLibrary("musiczone")
        }
    }
}