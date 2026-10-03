package com.example.musiczone.ui.navigation.tabs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.musiczone.model.Song
import com.example.musiczone.ui.MusicViewModel
import com.example.musiczone.ui.player.AlbumArtwork
import com.example.musiczone.ui.player.SongItem
import com.example.musiczone.ui.theme.MusicZoneSurface
import com.example.musiczone.ui.theme.MusicZoneTextPrimary
import com.example.musiczone.ui.theme.MusicZoneTextSecondary
import kotlin.collections.get
import kotlin.collections.orEmpty

@Composable
fun AlbumsTab(
    songs: List<Song>, viewModel: MusicViewModel, favoriteSongIds: Set<Long>
) {
    var selectedAlbum by remember {
        mutableStateOf<String?>(null)
    }

    val albums = songs.filter { it.album.isNotBlank() }.groupBy { it.album }

    if (selectedAlbum == null) {
        if (albums.isEmpty()) {
            Text(
                text = "No albums found", modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    horizontal = 12.dp, vertical = 8.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    items = albums.entries.toList(), key = { entry -> entry.key }) { entry ->

                    val album = entry.key
                    val albumSongs = entry.value
                    val firstSong = albumSongs.first()

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedAlbum = album
                            }, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(
                            containerColor = MusicZoneSurface
                        ), elevation = CardDefaults.cardElevation(
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
                        horizontal = 8.dp, vertical = 8.dp
                    ), verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        selectedAlbum = null
                    }) {
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
                    items = albumSongs, key = { song -> song.id }) { song ->
                    SongItem(song = song, onClick = {
                        viewModel.playSong(song)
                    }, onPlay = {
                        viewModel.playSong(song)
                    }, isFavorite = song.id in favoriteSongIds, onFavorite = {
                        viewModel.toggleFavorite(song.id)
                    })
                }
            }
        }
    }
}