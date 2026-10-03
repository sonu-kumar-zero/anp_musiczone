package com.example.musiczone.ui.player

import android.graphics.Bitmap
import android.os.Build
import android.util.Size
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.musiczone.model.Song
import com.example.musiczone.ui.theme.MusicZoneElevated
import com.example.musiczone.ui.theme.MusicZoneTextPrimary
import com.example.musiczone.ui.theme.MusicZoneTextSecondary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.musiczone.ui.theme.MusicZonePurple
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongItem(
    song: Song,
    onClick: () -> Unit,
    onPlay: () -> Unit,
    isFavorite: Boolean,
    onFavorite: () -> Unit,
    onAddToGroup: () -> Unit,
    onRemoveFromGroup: (() -> Unit)? = null,
    onAddToQueue: () -> Unit
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
                        song.uri, Size(160, 160), null
                    )
                } else {
                    null
                }
            } catch (_: Exception) {
                null
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                horizontal = 16.dp, vertical = 8.dp
            ), verticalAlignment = Alignment.CenterVertically
    ) {

        // Album artwork
        if (bitmap != null) {
            Image(
                bitmap = bitmap!!.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MusicZoneElevated), contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "♪",
                    style = MaterialTheme.typography.titleLarge,
                    color = MusicZoneTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Song information
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.bodyLarge,
                color = MusicZoneTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "${song.artist} • ${song.album}",
                style = MaterialTheme.typography.bodyMedium,
                color = MusicZoneTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        IconButton(
            onClick = onFavorite
        ) {
            Icon(
                imageVector = if (isFavorite) {
                    Icons.Filled.Favorite
                } else {
                    Icons.Outlined.FavoriteBorder
                }, contentDescription = if (isFavorite) {
                    "Remove from favorites"
                } else {
                    "Add to favorites"
                }, tint = if (isFavorite) {
                    MusicZonePurple
                } else {
                    MusicZoneTextSecondary
                }
            )
        }

        // Menu
        var menuExpanded by remember {
            mutableStateOf(false)
        }

        Spacer(modifier = Modifier.height(2.dp))

        Box {


            IconButton(
                onClick = {
                    menuExpanded = true
                }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Song options",
                    tint = MusicZoneTextSecondary
                )
            }

            if (menuExpanded) {
                ModalBottomSheet(
                    onDismissRequest = {
                        menuExpanded = false
                    }) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = song.title,
                            color = MusicZoneTextPrimary,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(
                                horizontal = 24.dp, vertical = 12.dp
                            )
                        )

                        Text(
                            text = song.artist,
                            color = MusicZoneTextSecondary,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(
                                start = 24.dp, bottom = 12.dp, end = 24.dp
                            )
                        )

                        ListItem(
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null
                                )
                            },
                            headlineContent = {
                                Text("Play")
                            },
                            modifier = Modifier.clickable {
                                menuExpanded = false
                                onPlay()
                            }
                        )

                        ListItem(
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                                    contentDescription = null
                                )
                            },
                            headlineContent = {
                                Text("Add to queue")
                            },
                            modifier = Modifier.clickable {
                                menuExpanded = false
                                onAddToQueue()
                            }
                        )

                        ListItem(
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                                    contentDescription = null
                                )
                            },
                            headlineContent = {
                                Text("Add to group")
                            },
                            modifier = Modifier.clickable {
                                menuExpanded = false
                                onAddToGroup()
                            }
                        )

                        if (onRemoveFromGroup != null) {
                            ListItem(
                                leadingContent = {
                                    Icon(
                                        imageVector = Icons.Default.RemoveCircleOutline,
                                        contentDescription = null
                                    )
                                },
                                headlineContent = {
                                    Text("Remove from group")
                                },
                                modifier = Modifier.clickable {
                                    menuExpanded = false
                                    onRemoveFromGroup()
                                }
                            )
                        }

                        ListItem(
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null
                                )
                            },
                            headlineContent = {
                                Text("Song info")
                            },
                            modifier = Modifier.clickable {
                                menuExpanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}