package com.example.musiczone.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musiczone.data.MusicRepository
import com.example.musiczone.data.SongCache
import com.example.musiczone.model.Song
import com.example.musiczone.playback.MusicController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import com.example.musiczone.data.FavoritesRepository

class MusicViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val songCache = SongCache(application)
    private val repository = MusicRepository(
        contentResolver = application.contentResolver,
        songCache = songCache
    )

    private val favoritesRepository = FavoritesRepository(application)

    private val _favoriteSongIds = MutableStateFlow<Set<Long>>(emptySet())
    val favoriteSongIds: StateFlow<Set<Long>> = _favoriteSongIds.asStateFlow()

    private val musicController = MusicController(application)

    val isPlaying: StateFlow<Boolean> = musicController.isPlaying

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()

    val currentPosition: StateFlow<Long> = musicController.currentPosition
    val duration: StateFlow<Long> = musicController.duration

    val isShuffleEnabled: StateFlow<Boolean> =
        musicController.isShuffleEnabled

    val repeatMode: StateFlow<Int> =
        musicController.repeatMode


    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredSongs: StateFlow<List<Song>> =
        combine(
            _songs,
            _searchQuery
        ) { songs, query ->
            if (query.isBlank()) {
                songs
            } else {
                val search = query.trim()

                songs.filter { song ->
                    song.title.contains(search, ignoreCase = true) ||
                            song.artist.contains(search, ignoreCase = true) ||
                            song.album.contains(search, ignoreCase = true)
                }
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    init {
        viewModelScope.launch {
            _favoriteSongIds.value = favoritesRepository.getFavoriteIds()

            musicController.currentMediaItem.collect { mediaItem ->
                if (mediaItem == null) {
                    _currentSong.value = null
                    return@collect
                }

                val songId = mediaItem.mediaId.toLongOrNull()

                _currentSong.value = _songs.value.firstOrNull { song ->
                    song.id == songId
                }
            }
        }
    }

    fun loadSongs() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            val cachedSongs = repository.getCachedSongs()

            if (cachedSongs.isNotEmpty()) {
                _songs.value = cachedSongs
                _isLoading.value = false
            } else {
                _isLoading.value = true
            }

            try {
                val freshSongs = repository.getSongs()

                if (freshSongs != cachedSongs) {
                    _songs.value = freshSongs
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun playSong(song: Song) {
        val currentSongs = _songs.value

        val startIndex = currentSongs.indexOfFirst { it.id == song.id }

        if (startIndex == -1) {
            return
        }

        musicController.play(
            currentSongs,
            startIndex
        )
    }

    fun togglePlayPause() {
        musicController.togglePlayPause()
    }

    fun seekTo(position: Long) {
        musicController.seekTo(position)
    }

    fun previousSong() {
        musicController.previousSong()
    }

    fun nextSong() {
        musicController.nextSong()
    }

    fun toggleShuffle() {
        musicController.toggleShuffle()
    }

    fun cycleRepeatMode() {
        musicController.cycleRepeatMode()
    }

    fun skipPrevious() {
        musicController.skipPrevious()
    }

    fun skipNext() {
        musicController.skipNext()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleFavorite(songId: Long) {
        val isFavorite = favoritesRepository.toggleFavorite(songId)

        _favoriteSongIds.value = if (isFavorite) {
            _favoriteSongIds.value + songId
        } else {
            _favoriteSongIds.value - songId
        }
    }

    fun isFavorite(songId: Long): Boolean {
        return _favoriteSongIds.value.contains(songId)
    }

    override fun onCleared() {
        musicController.release()
    }
}