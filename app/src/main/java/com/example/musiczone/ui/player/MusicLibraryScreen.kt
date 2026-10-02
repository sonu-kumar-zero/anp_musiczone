package com.example.musiczone.ui.player

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.musiczone.model.Song
import com.example.musiczone.ui.MusicViewModel

private const val ROUTE_LIBRARY = "library"
private const val ROUTE_NOW_PLAYING = "now_playing"

@Composable
public fun MusicLibraryScreen(
    viewModel: MusicViewModel = viewModel()
) {
    val navController = rememberNavController()

    val songs by viewModel.songs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentPosition by viewModel.currentPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val isShuffleEnabled by viewModel.isShuffleEnabled.collectAsState()
    val repeatMode by viewModel.repeatMode.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredSongs by viewModel.filteredSongs.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadSongs()
    }

    NavHost(
        navController = navController,
        startDestination = ROUTE_LIBRARY
    ) {
        composable(ROUTE_LIBRARY) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "MusicZone",
                    modifier = Modifier.padding(16.dp)
                )

                when {
                    isLoading -> {
                        Text(
                            text = "Scanning music...",
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    songs.isEmpty() -> {
                        Text(
                            text = "No music found",
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    else -> {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { query ->
                                viewModel.updateSearchQuery(query) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            placeholder = {
                                Text("Search songs...")
                            },
                            singleLine = true
                        )

                        if (filteredSongs.isEmpty()) {
                            Text(
                                text = "No matching songs",
                                modifier = Modifier.padding(16.dp)
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.weight(1f)
                            ) {
                                items(
                                    items = filteredSongs,
                                    key = { song ->
                                        song.id
                                    }
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

                currentSong?.let { song ->
                    NowPlayingBar(
                        song = song,
                        isPlaying = isPlaying,
                        currentPosition = currentPosition,
                        duration = duration,
                        onPlayPause = {
                            viewModel.togglePlayPause()
                        },
                        onSeek = { position ->
                            viewModel.seekTo(position)
                        },
                        onPrevious = {
                            viewModel.previousSong()
                        },
                        onNext = {
                            viewModel.nextSong()
                        },
                        isShuffleEnabled = isShuffleEnabled,
                        onShuffle = {
                            viewModel.toggleShuffle()
                        },
                        repeatMode = repeatMode,
                        onRepeat = {
                            viewModel.cycleRepeatMode()
                        },
                        onOpenPlayer = {
                            navController.navigate(ROUTE_NOW_PLAYING)
                        }
                    )
                }
            }
        }

        composable(ROUTE_NOW_PLAYING) {
            currentSong?.let { song ->
                NowPlayingScreen(
                    song = song
                )
            }
        }
    }
}

@Composable
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

@Composable
private fun NowPlayingBar(
    song: Song,
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    onPlayPause: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSeek: (Long) -> Unit,
    isShuffleEnabled: Boolean,
    onShuffle: () -> Unit,
    repeatMode: Int,
    onRepeat: ()-> Unit,
    onOpenPlayer: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable(onClick = onOpenPlayer)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(song.title)
                Text(song.artist)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "⏮",
                modifier = Modifier
                    .clickable(onClick = onPrevious)
                    .padding(8.dp)
            )

            Text(
                text = if (isShuffleEnabled) "🔀" else "⤨",
                modifier = Modifier
                    .clickable(onClick = onShuffle)
                    .padding(8.dp)
            )

            Text(
                text = if (isPlaying) "⏸" else "▶",
                modifier = Modifier
                    .clickable(onClick = onPlayPause)
                    .padding(8.dp)
            )

            Text(
                text = when (repeatMode) {
                    androidx.media3.common.Player.REPEAT_MODE_ALL -> "🔁"
                    androidx.media3.common.Player.REPEAT_MODE_ONE -> "🔂"
                    else -> "↩"
                },
                modifier = Modifier
                    .clickable(onClick = onRepeat)
                    .padding(8.dp)
            )

            Text(
                text = "⏭",
                modifier = Modifier
                    .clickable(onClick = onNext)
                    .padding(8.dp)
            )
        }

        androidx.compose.material3.Slider(
            value = currentPosition.toFloat(),
            onValueChange = { value ->
                onSeek(value.toLong())
            },
            valueRange = 0f..duration.toFloat(),
            enabled = duration > 0L
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(formatTime(currentPosition))
            Text(formatTime(duration))
        }
    }
}

private fun formatTime(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    return "%d:%02d".format(
        minutes,
        seconds
    )
}