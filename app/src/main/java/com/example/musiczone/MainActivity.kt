package com.example.musiczone

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.musiczone.data.MusicPermission
import com.example.musiczone.ui.player.MusicLibraryScreen
import com.example.musiczone.ui.theme.MusicZoneTheme

class MainActivity : ComponentActivity() {

    private var hasPermission by mutableStateOf(false)

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

    companion object {
        init {
            System.loadLibrary("musiczone")
        }
    }

    private external fun createEqualizer(): Long

    private external fun destroyEqualizer(
        handle: Long
    )

    private external fun setEqualizerBandGain(
        handle: Long, band: Int, gainDb: Double
    )

    private external fun processEqualizer(
        handle: Long, input: FloatArray
    ): FloatArray?

    private external fun resetEqualizer(
        handle: Long
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        val equalizerHandle = createEqualizer()

        setEqualizerBandGain(
            equalizerHandle, 0, 12.0
        )

        fun generateSineWave(
            frequency: Double, sampleRate: Double, frameCount: Int
        ): FloatArray {
            val samples = FloatArray(frameCount * 2)

            for (frame in 0 until frameCount) {
                val sample = kotlin.math.sin(
                    2.0 * Math.PI * frequency * frame / sampleRate
                ).toFloat()

                samples[frame * 2] = sample
                samples[frame * 2 + 1] = sample
            }

            return samples
        }

        fun measurePeak(
            samples: FloatArray
        ): Float {
            var peak = 0.0f

            for (sample in samples) {
                val magnitude = kotlin.math.abs(sample)

                if (magnitude > peak) {
                    peak = magnitude
                }
            }

            return peak
        }

        val sampleRate = 48000.0
        val frameCount = 48000

        val frequencies = listOf(
            60.0, 1000.0, 14000.0
        )

        for (frequency in frequencies) {
            resetEqualizer(equalizerHandle)

            val input = generateSineWave(
                frequency, sampleRate, frameCount
            )

            val inputPeak = measurePeak(input)

            val output = processEqualizer(
                equalizerHandle, input
            )

            val outputPeak = output?.let {
                measurePeak(it)
            }

            Log.d(
                "MusicZoneNative",
                "frequency=$frequency Hz " + "inputPeak=$inputPeak " + "outputPeak=$outputPeak"
            )
        }

        destroyEqualizer(equalizerHandle)


        hasPermission = MusicPermission.isGranted(this)

        setContent {
            MusicZoneTheme {
                if (hasPermission) {
                    MusicLibraryScreen()
                } else {
                    PermissionScreen(
                        onRequestPermission = {
                            permissionLauncher.launch(
                                MusicPermission.permission()
                            )
                        })
                }
            }
        }
    }
}


@Composable
private fun PermissionScreen(
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("MusicZone needs access to your music library.")

        Button(
            onClick = onRequestPermission
        ) {
            Text("Allow Access")
        }
    }
}
