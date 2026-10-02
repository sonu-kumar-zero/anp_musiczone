package com.example.musiczone.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.Alignment
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.draw.clip
import androidx.media3.container.MdtaMetadataEntry

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
    var selectedAlbum by remember {
        mutableStateOf<String?>(null)
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
        navController = navController, startDestination = ROUTE_LIBRARY
    ) {
        composable(ROUTE_LIBRARY) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        when {
                            isLoading -> {
                                Text(
                                    text = "Scanning music...", modifier = Modifier.padding(16.dp)
                                )
                            }

                            songs.isEmpty() -> {
                                Text(
                                    text = "No music found", modifier = Modifier.padding(16.dp)
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
                                                        horizontal = 16.dp, vertical = 8.dp
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
                                                        IconButton(onClick = {
                                                            viewModel.updateSearchQuery("")
                                                        }) {
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
                                                        items = filteredSongs, key = { song ->
                                                            song.id
                                                        }) { song ->
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
                                                            })
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
                                                    key = { song -> song.id }) { song ->
                                                    SongItem(song = song, onClick = {
                                                        viewModel.playSong(song)
                                                    }, onPlay = {
                                                        viewModel.playSong(song)
                                                    }, isFavorite = true, onFavorite = {
                                                        viewModel.toggleFavorite(song.id)
                                                    })
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
                                        val albums =
                                            songs.filter { it.album.isNotBlank() }
                                                .groupBy { it.album }

                                        if (selectedAlbum == null) {
                                            if (albums.isEmpty()) {
                                                Text(
                                                    text = "No albums found",
                                                    modifier = Modifier.padding(16.dp)
                                                )
                                            } else {
                                                LazyVerticalGrid(
                                                    columns = GridCells.Fixed(2),
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentPadding = PaddingValues(
                                                        horizontal = 12.dp,
                                                        vertical = 8.dp
                                                    ),
                                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                                ) {
                                                    items(
                                                        items = albums.entries.toList(),
                                                        key = { entry -> entry.key }
                                                    ) { entry ->

                                                        val album = entry.key
                                                        val albumSongs = entry.value
                                                        val firstSong = albumSongs.first()

                                                        Card(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .clickable {
                                                                    selectedAlbum = album
                                                                },
                                                            shape = RoundedCornerShape(16.dp),
                                                            colors = CardDefaults.cardColors(
                                                                containerColor = MusicZoneSurface
                                                            ),
                                                            elevation = CardDefaults.cardElevation(
                                                                defaultElevation = 2.dp
                                                            )
                                                        ) {
                                                            Column(
                                                                modifier = Modifier.padding(10.dp)
                                                            ) {
                                                                AlbumArtwork(
                                                                    song = firstSong,
                                                                    modifier = Modifier
                                                                        .fillMaxWidth()
                                                                        .aspectRatio(1f)
                                                                        .clip(RoundedCornerShape(12.dp))
                                                                )

                                                                Spacer(
                                                                    modifier = Modifier.height(10.dp)
                                                                )

                                                                Text(
                                                                    text = album,
                                                                    style = MaterialTheme.typography.titleMedium,
                                                                    color = MusicZoneTextPrimary,
                                                                    maxLines = 1,
                                                                    overflow = TextOverflow.Ellipsis
                                                                )

                                                                Spacer(
                                                                    modifier = Modifier.height(2.dp)
                                                                )

                                                                Text(
                                                                    text = "${albumSongs.size} songs",
                                                                    style = MaterialTheme.typography.bodySmall,
                                                                    color = MusicZoneTextSecondary,
                                                                    maxLines = 1
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            val albumSongs = albums[selectedAlbum].orEmpty()

                                            Column(
                                                modifier = Modifier.fillMaxSize()
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(
                                                            horizontal = 8.dp,
                                                            vertical = 8.dp
                                                        ),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    IconButton(
                                                        onClick = {
                                                            selectedAlbum = null
                                                        }
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                            contentDescription = "Back to albums",
                                                            tint = MusicZoneTextPrimary
                                                        )
                                                    }

                                                    Text(
                                                        text = selectedAlbum.orEmpty(),
                                                        style = MaterialTheme.typography.headlineSmall,
                                                        color = MusicZoneTextPrimary
                                                    )
                                                }

                                                LazyColumn(
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    items(
                                                        items = albumSongs,
                                                        key = { song -> song.id }) { song ->
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
                                                            })
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    NavigationBar {
                        LibraryTab.entries.forEach { tab ->
                            NavigationBarItem(selected = selectedTab == tab, onClick = {
                                selectedTab = tab
                            }, icon = {
                                Icon(
                                    imageVector = when (tab) {
                                        LibraryTab.SONGS -> Icons.Default.LibraryMusic
                                        LibraryTab.ALBUMS -> Icons.Default.Album
                                        LibraryTab.ARTISTS -> Icons.Default.Person
                                        LibraryTab.FAVORITES -> Icons.Default.Favorite
                                    }, contentDescription = tab.title
                                )
                            }, label = {
                                Text(tab.title)
                            })
                        }
                    }

                }

                currentSong?.let { song ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 80.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
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
                            })
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
                    })
            }
        }
    }
}
