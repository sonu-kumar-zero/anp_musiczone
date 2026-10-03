package com.example.musiczone.ui.components

import android.graphics.Bitmap
import android.os.Build
import android.util.Size
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.example.musiczone.model.Song
import com.example.musiczone.ui.theme.MusicZoneElevated
import com.example.musiczone.ui.theme.MusicZonePurple
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import coil3.compose.AsyncImage
import com.example.musiczone.data.artwork.ArtworkCache
import com.example.musiczone.data.artwork.ArtworkRepository

@Composable
fun AlbumArtwork(
    song: Song,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var bitmap by remember(song.uri) {
        mutableStateOf<Bitmap?>(null)
    }

    var onlineArtworkUrl by remember(song.artist, song.album) {
        mutableStateOf<String?>(null)
    }

    val artworkRepository = remember(context) {
        ArtworkRepository(
            artworkCache = ArtworkCache(context)
        )
    }

    LaunchedEffect(song.uri) {
        bitmap = withContext(Dispatchers.IO) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    context.contentResolver.loadThumbnail(
                        song.uri,
                        Size(800, 800),
                        null
                    )
                } else {
                    null
                }
            } catch (_: Exception) {
                null
            }
        }
    }

    LaunchedEffect(song.artist, song.album, bitmap) {
        if (bitmap == null && onlineArtworkUrl == null) {
            onlineArtworkUrl = withContext(Dispatchers.IO) {
                artworkRepository.findArtwork(
                    artist = song.artist,
                    album = song.album
                )
            }
        }
    }

    when {
        bitmap != null -> {
            Image(
                bitmap = bitmap!!.asImageBitmap(),
                contentDescription = "Album artwork",
                modifier = modifier,
                contentScale = ContentScale.Crop
            )
        }

        onlineArtworkUrl != null -> {
            AsyncImage(
                model = onlineArtworkUrl,
                contentDescription = "Album artwork",
                modifier = modifier,
                contentScale = ContentScale.Crop
            )
        }

        else -> {
            BoxWithConstraints(
                modifier = modifier.background(MusicZoneElevated),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "♪",
                    color = MusicZonePurple,
                    fontSize = (maxWidth.value * 0.45f).sp
                )
            }
        }
    }
}