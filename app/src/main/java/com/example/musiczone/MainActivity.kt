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

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

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
