package com.example.musiczone.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.musiczone.ui.MusicViewModel
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import com.example.musiczone.ui.theme.MusicZoneElevated
import com.example.musiczone.ui.theme.MusicZonePurple
import com.example.musiczone.ui.theme.MusicZoneSurface
import com.example.musiczone.ui.theme.MusicZoneTextPrimary
import com.example.musiczone.ui.theme.MusicZoneTextSecondary
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.musiczone.ui.navigation.LibraryTab
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem

private const val ROUTE_LIBRARY = "library"
private const val ROUTE_NOW_PLAYING = "now_playing"

@Composable
public fun MusicLibraryScreen(
    viewModel: MusicViewModel = viewModel()
) {
    val navController = rememberNavController()

    var selectedTab by remember {
        mutableStateOf(LibraryTab.SONGS)
    }

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
    val favoriteSongIds by viewModel.favoriteSongIds.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadSongs()
    }

    NavHost(
        navController = navController,
        startDestination = ROUTE_LIBRARY
    ) {
        composable(ROUTE_LIBRARY) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Box(
                    modifier = Modifier.weight(1f)
                ) {
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
                            when (selectedTab) {
                                LibraryTab.SONGS -> {

                                    Column(
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        OutlinedTextField(
                                            value = searchQuery,
                                            onValueChange = { query ->
                                                viewModel.updateSearchQuery(query)
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(
                                                    horizontal = 16.dp,
                                                    vertical = 8.dp
                                                ),
                                            placeholder = {
                                                Text(
                                                    text = "Search songs, artists, albums...",
                                                    color = MusicZoneTextSecondary
                                                )
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.Search,
                                                    contentDescription = "Search",
                                                    tint = MusicZonePurple
                                                )
                                            },
                                            trailingIcon = {
                                                if (searchQuery.isNotEmpty()) {
                                                    IconButton(
                                                        onClick = {
                                                            viewModel.updateSearchQuery("")
                                                        }
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Clear,
                                                            contentDescription = "Clear search",
                                                            tint = MusicZoneTextSecondary
                                                        )
                                                    }
                                                }
                                            },
                                            singleLine = true,
                                            shape = RoundedCornerShape(28.dp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedContainerColor = MusicZoneSurface,
                                                unfocusedContainerColor = MusicZoneSurface,
                                                focusedBorderColor = MusicZonePurple,
                                                unfocusedBorderColor = MusicZoneElevated,
                                                cursorColor = MusicZonePurple,
                                                focusedTextColor = MusicZoneTextPrimary,
                                                unfocusedTextColor = MusicZoneTextPrimary
                                            )
                                        )

                                        if (filteredSongs.isEmpty()) {
                                            Text(
                                                text = "No matching songs",
                                                modifier = Modifier.padding(16.dp)
                                            )
                                        } else {
                                            LazyColumn(
                                                modifier = Modifier.fillMaxSize()
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
                                                        },
                                                        onPlay = {
                                                            viewModel.playSong(song)
                                                        },
                                                        isFavorite = song.id in favoriteSongIds,
                                                        onFavorite = {
                                                            viewModel.toggleFavorite(song.id)
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                LibraryTab.FAVORITES -> {
                                    val favoriteSongs = songs.filter { song ->
                                        song.id in favoriteSongIds
                                    }

                                    if (favoriteSongs.isEmpty()) {
                                        Text(
                                            text = "No favorite songs",
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    } else {
                                        LazyColumn(
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            items(
                                                items = favoriteSongs,
                                                key = { song -> song.id }
                                            ) { song ->
                                                SongItem(
                                                    song = song,
                                                    onClick = {
                                                        viewModel.playSong(song)
                                                    },
                                                    onPlay = {
                                                        viewModel.playSong(song)
                                                    },
                                                    isFavorite = true,
                                                    onFavorite = {
                                                        viewModel.toggleFavorite(song.id)
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }

                                LibraryTab.ARTISTS -> {
                                    Text(
                                        text = "Albums coming soon",
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }

                                LibraryTab.ALBUMS -> {
                                    Text(
                                        text = "Artists coming soon",
                                        modifier = Modifier.padding(16.dp)
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

                NavigationBar {
                    LibraryTab.entries.forEach { tab ->
                        NavigationBarItem(
                            selected = selectedTab == tab,
                            onClick = {
                                selectedTab = tab
                            },
                            icon = {
                                Icon(
                                    imageVector = when (tab) {
                                        LibraryTab.SONGS -> Icons.Default.LibraryMusic
                                        LibraryTab.ALBUMS -> Icons.Default.Album
                                        LibraryTab.ARTISTS -> Icons.Default.Person
                                        LibraryTab.FAVORITES -> Icons.Default.Favorite
                                    },
                                    contentDescription = tab.title
                                )
                            },
                            label = {
                                Text(tab.title)
                            }
                        )
                    }
                }

            }
        }

        composable(ROUTE_NOW_PLAYING) {
            currentSong?.let { song ->
                NowPlayingScreen(
                    song = song,
                    isPlaying = isPlaying,
                    currentPosition = currentPosition,
                    duration = duration,
                    isShuffleEnabled = isShuffleEnabled,
                    repeatMode = repeatMode,
                    onPlayPause = {
                        viewModel.togglePlayPause()
                    },
                    onPrevious = {
                        viewModel.skipPrevious()
                    },
                    onNext = {
                        viewModel.skipNext()
                    },
                    onSeek = { position ->
                        viewModel.seekTo(position)
                    },
                    onShuffle = {
                        viewModel.toggleShuffle()
                    },
                    onRepeat = {
                        viewModel.cycleRepeatMode()
                    },
                    onBack = {
                        navController.popBackStack()
                    },
                    isFavorite = viewModel.isFavorite(song.id),
                    onFavorite = {
                        viewModel.toggleFavorite(song.id)
                    }
                )
            }
        }
    }
}
