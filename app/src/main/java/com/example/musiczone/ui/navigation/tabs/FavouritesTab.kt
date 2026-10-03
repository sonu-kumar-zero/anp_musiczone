package com.example.musiczone.ui.navigation.tabs

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.musiczone.model.Song
import com.example.musiczone.ui.MusicViewModel
import com.example.musiczone.ui.player.SongItem

@Composable
fun FavoritesTab(
    songs: List<Song>, favoriteSongIds: Set<Long>, viewModel: MusicViewModel
) {
    val favoriteSongs = songs.filter { song ->
        song.id in favoriteSongIds
    }

    if (favoriteSongs.isEmpty()) {
        Text(
            text = "No favorite songs", modifier = Modifier.padding(16.dp)
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = favoriteSongs, key = { song -> song.id }) { song ->
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