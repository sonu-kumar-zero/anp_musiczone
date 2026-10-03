package com.example.musiczone.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import com.example.musiczone.ui.navigation.LibraryTab
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.ui.Alignment
import com.example.musiczone.ui.navigation.tabs.AlbumsTab
import com.example.musiczone.ui.navigation.tabs.ArtistsTab
import com.example.musiczone.ui.navigation.tabs.FavoritesTab
import com.example.musiczone.ui.navigation.tabs.SongsTab
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.musiczone.ui.player.AddToGroupDialog
import com.example.musiczone.model.Song

private const val ROUTE_LIBRARY = "library"
private const val ROUTE_NOW_PLAYING = "now_playing"


@Composable
fun MusicLibraryScreen(
    viewModel: MusicViewModel = viewModel()
) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    var songToAddToGroup by remember {
        mutableStateOf<Song?>(null)
    }
    var selectedGroupIds by remember {
        mutableStateOf<Set<Long>>(emptySet())
    }

    val pagerState = rememberPagerState(
        initialPage = LibraryTab.SONGS.ordinal, pageCount = {
            LibraryTab.entries.size
        })

    val selectedTab = LibraryTab.entries[pagerState.currentPage]

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
    val favoriteGroups by viewModel.favoriteGroups.collectAsState()
    val queue by viewModel.queue.collectAsState()
    val currentMediaItem by viewModel.currentMediaItem.collectAsState()
    var showQueue by remember { mutableStateOf(false) }

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
                    modifier = Modifier.fillMaxSize()
                ) {

                    // Library content
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

                                HorizontalPager(
                                    state = pagerState, modifier = Modifier.fillMaxSize()
                                ) { page ->

                                    when (LibraryTab.entries[page]) {

                                        LibraryTab.SONGS -> {
                                            SongsTab(
                                                searchQuery = searchQuery,
                                                viewModel = viewModel,
                                                filteredSongs = filteredSongs,
                                                favoriteSongIds = favoriteSongIds,
                                                onAddToGroup = { song ->
                                                    songToAddToGroup = song
                                                    selectedGroupIds = emptySet()

                                                    viewModel.getFavoriteGroupIdsForSong(song.id) { groupIds ->
                                                        selectedGroupIds = groupIds
                                                    }
                                                })
                                        }

                                        LibraryTab.ALBUMS -> {
                                            AlbumsTab(
                                                viewModel = viewModel,
                                                songs = songs,
                                                favoriteSongIds = favoriteSongIds
                                            )
                                        }

                                        LibraryTab.ARTISTS -> {
                                            ArtistsTab(
                                                songs = songs,
                                                viewModel = viewModel,
                                                favoriteSongIds = favoriteSongIds
                                            )
                                        }

                                        LibraryTab.FAVORITES -> {
                                            FavoritesTab(
                                                songs = songs, viewModel = viewModel
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Bottom navigation
                    NavigationBar {

                        LibraryTab.entries.forEach { tab ->

                            NavigationBarItem(
                                selected = selectedTab == tab,

                                onClick = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(
                                            tab.ordinal
                                        )
                                    }
                                },

                                icon = {
                                    Icon(
                                        imageVector = when (tab) {
                                            LibraryTab.SONGS -> Icons.Default.LibraryMusic

                                            LibraryTab.ALBUMS -> Icons.Default.Album

                                            LibraryTab.ARTISTS -> Icons.Default.Person

                                            LibraryTab.FAVORITES -> Icons.Default.Favorite
                                        }, contentDescription = tab.title
                                    )
                                },

                                label = {
                                    Text(tab.title)
                                })
                        }
                    }
                }

                // Mini player
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
                    },
                    onQueueClick = {
                        showQueue = true
                    }
                )

                if (showQueue) {
                    QueueScreen(
                        queue = queue,
                        currentMediaItem = currentMediaItem,
                        onRemove = { index ->
                            viewModel.removeFromQueue(index)
                        },
                        onDismiss = {
                            showQueue = false
                        },
                        onMove = { fromIndex, toIndex ->
                            viewModel.moveInQueue(fromIndex, toIndex)
                        },
                        onPlay = {index ->
                            viewModel.playQueueItem(index)
                        }
                    )
                }
            }
        }
    }

    if (songToAddToGroup != null) {

        AddToGroupDialog(
            groups = favoriteGroups,
            selectedGroupIds = selectedGroupIds,
            onGroupToggle = { groupId ->
                selectedGroupIds = if (groupId in selectedGroupIds) {
                    selectedGroupIds - groupId
                } else {
                    selectedGroupIds + groupId
                }
            },
            onDismiss = {
                songToAddToGroup = null
            },
            onConfirm = {
                val song = songToAddToGroup

                if (song != null) {
                    favoriteGroups.forEach { group ->
                        if (group.id in selectedGroupIds) {
                            viewModel.addSongToFavoriteGroup(
                                groupId = group.id, songId = song.id
                            )
                        } else {
                            viewModel.removeSongFromFavoriteGroup(
                                groupId = group.id, songId = song.id
                            )
                        }
                    }
                }

                songToAddToGroup = null
            }
        )
    }
}