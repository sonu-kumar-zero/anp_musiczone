package com.example.musiczone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.musiczone.data.MusicPermission
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musiczone.model.Song
import com.example.musiczone.ui.MusicViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {

    private var hasPermission by mutableStateOf(false)

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        hasPermission = MusicPermission.isGranted(this)

        setContent {
            if (hasPermission) {
                MusicLibraryScreen()
            } else {
                PermissionScreen(
                    onRequestPermission = {
                        permissionLauncher.launch(
                            MusicPermission.permission()
                        )
                    }
                )
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun PermissionScreen(
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
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

@androidx.compose.runtime.Composable
private fun MusicLibraryScreen(
    viewModel: MusicViewModel = viewModel()
) {
    val songs by viewModel.songs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadSongs()
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text("MusicZone")

        when {
            isLoading -> {
                Text("Scanning music...")
            }

            songs.isEmpty() -> {
                Text("No music found")
            }

            else -> {
                LazyColumn {
                    items(
                        items = songs,
                        key = { song -> song.id }
                    ) { song ->
                        SongItem(
                            song = song,
                            onClick = {
                                viewModel.playSong(song)
                            }
                        )
                    }
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun SongItem(
    song: Song,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(song.title)
        Text(song.artist)
    }
}