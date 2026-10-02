package com.example.musiczone.ui.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musiczone.model.Song
import androidx.compose.ui.layout.ContentScale
import android.graphics.Bitmap
import android.os.Build
import android.util.Size
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun NowPlayingScreen(
    song: Song
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        AlbumArtwork(
            song = song,
            modifier = Modifier.size(300.dp)
        )

        Text(
            text = song.title,
            fontSize = 24.sp,
            modifier = Modifier.padding(top = 24.dp)
        )

        Text(
            text = song.artist,
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun AlbumArtwork(
    song: Song,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var bitmap by remember(song.uri) {
        mutableStateOf<Bitmap?>(null)
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

    if (bitmap != null) {
        Image(
            bitmap = bitmap!!.asImageBitmap(),
            contentDescription = "Album artwork",
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = modifier.background(Color.DarkGray),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "♪",
                color = Color.White,
                fontSize = 64.sp
            )
        }
    }
}