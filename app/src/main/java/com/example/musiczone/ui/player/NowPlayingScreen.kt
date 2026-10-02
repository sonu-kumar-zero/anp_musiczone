package com.example.musiczone.ui.player

import android.graphics.Bitmap
import android.os.Build
import android.util.Size
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.Player
import com.example.musiczone.model.Song
import com.example.musiczone.ui.theme.MusicZoneElevated
import com.example.musiczone.ui.theme.MusicZoneOnPrimary
import com.example.musiczone.ui.theme.MusicZonePurple
import com.example.musiczone.ui.theme.MusicZoneSurface
import com.example.musiczone.ui.theme.MusicZoneTextPrimary
import com.example.musiczone.ui.theme.MusicZoneTextSecondary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.max
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder

private val FallbackBackgroundColors = listOf(
    Color(0xFF24143D),
    Color(0xFF142B4A),
    Color(0xFF123A3A),
    Color(0xFF3D2914),
    Color(0xFF3D1829)
)

@Composable
fun NowPlayingScreen(
    song: Song,
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    isShuffleEnabled: Boolean,
    repeatMode: Int,
    onPlayPause: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSeek: (Long) -> Unit,
    onShuffle: () -> Unit,
    onRepeat: () -> Unit,
    onBack: () -> Unit,
    isFavorite: Boolean,
    onFavorite: () -> Unit,
) {
    val context = LocalContext.current

    var bitmap by remember(song.uri) {
        mutableStateOf<Bitmap?>(null)
    }

    var isDragging by remember {
        mutableStateOf(false)
    }

    var dragProgress by remember {
        mutableFloatStateOf(0f)
    }

    LaunchedEffect(song.uri) {
        isDragging = false
        dragProgress = 0f

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

    val fallbackColor = remember(song.id) {
        FallbackBackgroundColors[
            (song.id.hashCode() and Int.MAX_VALUE) %
                    FallbackBackgroundColors.size
        ]
    }

    val playbackProgress = if (duration > 0L) {
        (currentPosition.toFloat() / duration.toFloat())
            .coerceIn(0f, 1f)
    } else {
        0f
    }

    val progress = if (isDragging) {
        dragProgress
    } else {
        playbackProgress
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(fallbackColor)
    ) {

        // Blurred album artwork
        AnimatedContent(
            targetState = bitmap,
            transitionSpec = {
                fadeIn(
                    animationSpec = tween(650)
                ) togetherWith fadeOut(
                    animationSpec = tween(650)
                )
            },
            label = "background_artwork_transition"
        ) { bitmap ->

            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(1.15f)
                        .blur(80.dp),
                    contentScale = ContentScale.Crop,
                    alpha = 0.55f
                )
            }
        }

        // Darken the artwork so UI remains readable
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.Black.copy(alpha = 0.52f)
                )
        )

        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {
            val artworkSize = when {
                maxHeight < 650.dp -> 220.dp
                maxHeight < 750.dp -> 260.dp
                else -> 300.dp
            }

            val verticalSpacing = when {
                maxHeight < 650.dp -> 8.dp
                maxHeight < 750.dp -> 12.dp
                else -> 18.dp
            }

            val playButtonSize = when {
                maxHeight < 650.dp -> 60.dp
                maxHeight < 750.dp -> 64.dp
                else -> 68.dp
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = 24.dp,
                        vertical = 20.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Top bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MusicZoneTextPrimary
                        )
                    }

                    Text(
                        text = "Now Playing",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = MusicZoneTextPrimary,
                        textAlign = TextAlign.Center
                    )

                    // Balances the back button width.
                    Spacer(modifier = Modifier.height(verticalSpacing))
                }

                Spacer(modifier = Modifier.height(verticalSpacing))

                // Album artwork
                AnimatedContent(
                    targetState = song,
                    transitionSpec = {
                        (fadeIn(
                            animationSpec = tween(400)
                        ) + scaleIn(
                            initialScale = 0.94f,
                            animationSpec = tween(400)
                        )) togetherWith
                                (fadeOut(
                                    animationSpec = tween(250)
                                ) + scaleOut(
                                    targetScale = 1.04f,
                                    animationSpec = tween(250)
                                ))
                    },
                    label = "main_artwork_transition"
                ) { targetSong ->

                    AlbumArtwork(
                        song = targetSong,
                        modifier = Modifier
                            .size(artworkSize)
                            .clip(RoundedCornerShape(24.dp))
                    )
                }

                Spacer(modifier = Modifier.height(verticalSpacing))

                // Song title
                Text(
                    text = song.title,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MusicZoneTextPrimary,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                // Artist
                Text(
                    text = song.artist,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MusicZoneTextPrimary.copy(alpha = 0.88f),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                // Album
                Text(
                    text = song.album,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MusicZoneTextSecondary,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(verticalSpacing))

                // Custom thin seekbar
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .align(Alignment.Center)
                    ) {
                        val trackHeight = 3.dp.toPx()
                        val y = size.height / 2f
                        val radius = trackHeight / 2f

                        drawRoundRect(
                            color = MusicZoneSurface.copy(alpha = 0.85f),
                            topLeft = androidx.compose.ui.geometry.Offset(
                                0f,
                                y - radius
                            ),
                            size = androidx.compose.ui.geometry.Size(
                                size.width,
                                trackHeight
                            ),
                            cornerRadius = CornerRadius(
                                radius,
                                radius
                            )
                        )

                        val activeWidth = size.width * progress

                        if (activeWidth > 0f) {
                            drawRoundRect(
                                color = MusicZonePurple,
                                topLeft = androidx.compose.ui.geometry.Offset(
                                    0f,
                                    y - radius
                                ),
                                size = androidx.compose.ui.geometry.Size(
                                    activeWidth,
                                    trackHeight
                                ),
                                cornerRadius = CornerRadius(
                                    radius,
                                    radius
                                )
                            )
                        }

                        val thumbRadius = if (isDragging) {
                            7.dp.toPx()
                        } else {
                            4.dp.toPx()
                        }

                        drawCircle(
                            color = MusicZonePurple,
                            radius = thumbRadius,
                            center = androidx.compose.ui.geometry.Offset(
                                activeWidth.coerceIn(
                                    thumbRadius,
                                    size.width - thumbRadius
                                ),
                                y
                            )
                        )
                    }

                    Slider(
                        value = progress,
                        onValueChange = { value ->
                            isDragging = true
                            dragProgress = value
                        },
                        onValueChangeFinished = {
                            isDragging = false

                            onSeek(
                                (dragProgress * duration).toLong()
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = Color.Transparent,
                            activeTrackColor = Color.Transparent,
                            inactiveTrackColor = Color.Transparent
                        )
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 2.dp
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatTime(currentPosition),
                        color = MusicZoneTextSecondary,
                        style = MaterialTheme.typography.labelSmall
                    )

                    Text(
                        text = formatTime(duration),
                        color = MusicZoneTextSecondary,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                // Main playback controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconButton(
                        onClick = onPrevious,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Previous",
                            tint = MusicZoneTextPrimary,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    Spacer(
                        modifier = Modifier.width(20.dp)
                    )

                    // Main play/pause button
                    Box(
                        modifier = Modifier
                            .size(playButtonSize)
                            .clip(CircleShape)
                            .background(MusicZonePurple)
                            .clickable(onClick = onPlayPause),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) {
                                Icons.Default.Pause
                            } else {
                                Icons.Default.PlayArrow
                            },
                            contentDescription = if (isPlaying) {
                                "Pause"
                            } else {
                                "Play"
                            },
                            tint = MusicZoneOnPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(
                        modifier = Modifier.width(20.dp)
                    )

                    IconButton(
                        onClick = onNext,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Next",
                            tint = MusicZoneTextPrimary,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                // Secondary controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconButton(
                        onClick = onShuffle,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shuffle,
                            contentDescription = "Shuffle",
                            tint = if (isShuffleEnabled) {
                                MusicZonePurple
                            } else {
                                MusicZoneTextSecondary
                            },
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(
                        modifier = Modifier.width(32.dp)
                    )

                    IconButton(
                        onClick = onRepeat,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            imageVector = when (repeatMode) {
                                Player.REPEAT_MODE_ONE ->
                                    Icons.Default.RepeatOne

                                else ->
                                    Icons.Default.Repeat
                            },
                            contentDescription = "Repeat",
                            tint = if (
                                repeatMode != Player.REPEAT_MODE_OFF
                            ) {
                                MusicZonePurple
                            } else {
                                MusicZoneTextSecondary
                            },
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(
                        modifier = Modifier.width(32.dp)
                    )

                    IconButton(
                        onClick = onFavorite
                    ) {
                        Icon(
                            imageVector = if (isFavorite) {
                                Icons.Filled.Favorite
                            } else {
                                Icons.Outlined.FavoriteBorder
                            },
                            contentDescription = if (isFavorite) {
                                "Remove from favorites"
                            } else {
                                "Add to favorites"
                            },
                            tint = if (isFavorite) {
                                MusicZonePurple
                            } else {
                                MusicZoneTextSecondary
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AlbumArtwork(
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
        BoxWithConstraints(
            modifier = modifier
                .background(MusicZoneElevated),
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

private fun formatTime(milliseconds: Long): String {
    val totalSeconds = max(0L, milliseconds) / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    return "%d:%02d".format(
        minutes,
        seconds
    )
}
