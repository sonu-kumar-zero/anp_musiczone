package com.example.musiczone.ui.navigation.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import com.example.musiczone.ui.player.SongItem
import com.example.musiczone.ui.theme.MusicZoneElevated
import com.example.musiczone.ui.theme.MusicZonePurple
import com.example.musiczone.ui.theme.MusicZoneSurface
import com.example.musiczone.ui.theme.MusicZoneTextPrimary
import com.example.musiczone.ui.theme.MusicZoneTextSecondary
import kotlin.collections.get
import kotlin.collections.orEmpty

@Composable
fun ArtistsTab(
    songs: List<Song>, viewModel: MusicViewModel, favoriteSongIds: Set<Long>
) {
    val artists = songs.filter { it.artist.isNotBlank() }.groupBy { it.artist }
    var selectedArtist by remember { mutableStateOf<String?>(null) }

    if (selectedArtist == null) {
        if (artists.isEmpty()) {
            Text(
                text = "No artists found", modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(
                    horizontal = 12.dp, vertical = 8.dp
                ), verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = artists.entries.toList(), key = { entry -> entry.key }) { entry ->
                    val artist = entry.key
                    val artistSongs = entry.value

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedArtist = artist
                            }, shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(
                            containerColor = MusicZoneSurface
                        ), elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 16.dp, vertical = 12.dp
                                ), verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(
                                        MusicZoneElevated
                                    ), contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = artist.trim().firstOrNull()?.uppercase() ?: "?",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MusicZonePurple
                                )
                            }

                            Spacer(
                                modifier = Modifier.width(16.dp)
                            )

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = artist,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MusicZoneTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(
                                    modifier = Modifier.height(3.dp)
                                )

                                Text(
                                    text = "${artistSongs.size} songs",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MusicZoneTextSecondary
                                )
                            }

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Open artist",
                                tint = MusicZoneTextSecondary
                            )
                        }
                    }
                }
            }
        }
    } else {
        val artistSongs = artists[selectedArtist].orEmpty()

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
                        selectedArtist = null
                    }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to artists",
                        tint = MusicZoneTextPrimary
                    )
                }

                Text(
                    text = selectedArtist.orEmpty(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MusicZoneTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(
                    items = artistSongs, key = { song -> song.id }) { song ->
                    SongItem(song = song, onClick = {
                        viewModel.playSong(song)
                    }, onPlay = {
                        viewModel.playSong(song)
                    }, isFavorite = song.id in favoriteSongIds, onFavorite = {
                        viewModel.toggleFavorite(song.id)
                    }, onAddToGroup = {}, onAddToQueue = {
                        viewModel.addToQueue(song)
                    })
                }
            }
        }
    }
}