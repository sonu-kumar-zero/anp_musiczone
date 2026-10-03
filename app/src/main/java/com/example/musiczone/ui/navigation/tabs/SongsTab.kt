package com.example.musiczone.ui.navigation.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.musiczone.model.Song
import com.example.musiczone.ui.MusicViewModel
import com.example.musiczone.ui.player.SongItem
import com.example.musiczone.ui.theme.MusicZoneElevated
import com.example.musiczone.ui.theme.MusicZonePurple
import com.example.musiczone.ui.theme.MusicZoneSurface
import com.example.musiczone.ui.theme.MusicZoneTextPrimary
import com.example.musiczone.ui.theme.MusicZoneTextSecondary

@Composable
fun SongsTab(
    searchQuery: String,
    viewModel: MusicViewModel,
    filteredSongs: List<Song>,
    favoriteSongIds: Set<Long>,
    onAddToGroup: (Song) -> Unit
) {

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
                    text = "Search songs, artists, albums...", color = MusicZoneTextSecondary
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
                text = "No matching songs", modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = filteredSongs, key = { song ->
                        song.id
                    }) { song ->
                    SongItem(song = song, onClick = {
                        viewModel.playSong(song)
                    }, onPlay = {
                        viewModel.playSong(song)
                    }, isFavorite = song.id in favoriteSongIds, onFavorite = {
                        viewModel.toggleFavorite(song.id)
                    }, onAddToGroup = {
                        onAddToGroup(song)
                    })
                }
            }
        }
    }
}