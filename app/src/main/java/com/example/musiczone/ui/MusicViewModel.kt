package com.example.musiczone.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musiczone.data.MusicRepository
import com.example.musiczone.model.Song
import com.example.musiczone.playback.MusicPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MusicViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = MusicRepository(
        application.contentResolver
    )

    private val musicPlayer = MusicPlayer(application)

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadSongs() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true

            try {
                _songs.value = repository.getSongs()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun playSong(song: Song) {
        musicPlayer.play(song)
    }

    override fun onCleared() {
        musicPlayer.release()
        super.onCleared()
    }
}