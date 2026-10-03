package com.example.musiczone.ui.player

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import com.example.musiczone.ui.theme.MusicZoneBackground
import com.example.musiczone.ui.theme.MusicZonePurple
import com.example.musiczone.ui.theme.MusicZoneTextPrimary
import com.example.musiczone.ui.theme.MusicZoneTextSecondary
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.getValue
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItemDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import com.example.musiczone.ui.theme.MusicZoneElevated
import com.example.musiczone.ui.theme.MusicZoneSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueScreen(
    queue: List<MediaItem>,
    currentMediaItem: MediaItem?,
    onRemove: (Int) -> Unit,
    onMove: (Int, Int) -> Unit,
    onDismiss: () -> Unit,
    onPlay: (Int) -> Unit,
    isPlaying: Boolean,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MusicZoneBackground,
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 24.dp, end = 16.dp, bottom = 12.dp
                    ), verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Queue",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MusicZoneTextPrimary,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onDismiss
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MusicZoneTextSecondary
                    )
                }
            }

            val listState = rememberLazyListState()

            val reorderableState = rememberReorderableLazyListState(
                lazyListState = listState, onMove = { from, to ->
                    onMove(from.index, to.index)
                })

            LazyColumn(
                state = listState, modifier = Modifier.fillMaxHeight()
            ) {
                itemsIndexed(
                    items = queue, key = { _, item ->
                        item.mediaId
                    }) { index, item ->

                    ReorderableItem(
                        state = reorderableState, key = item.mediaId
                    ) { isDragging ->

                        val elevation by animateDpAsState(
                            targetValue = if (isDragging) 8.dp else 0.dp, label = "dragElevation"
                        )

                        val isCurrent = item.mediaId == currentMediaItem?.mediaId

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                .shadow(
                                    elevation = elevation, shape = MaterialTheme.shapes.medium
                                ),
                            colors = CardDefaults.cardColors(
                                containerColor = when {
                                    isDragging -> MusicZoneElevated
                                    isCurrent -> MusicZoneSurface
                                    else -> MusicZoneBackground
                                }
                            )
                        ) {


                            ListItem(
                                modifier = Modifier
                                    .longPressDraggableHandle()
                                    .clickable {
                                        onPlay(index)
                                    },
                                colors = ListItemDefaults.colors(
                                    containerColor = Color.Transparent
                                ),
                                leadingContent = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        if (isCurrent) {
                                            PlayingWaveform(
                                                isPlaying = isPlaying
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.DragHandle,
                                                contentDescription = "Drag to reorder",
                                                tint = MusicZoneTextSecondary,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                    }
                                },
                                headlineContent = {
                                    Text(
                                        text = item.mediaMetadata.title?.toString()
                                            ?: "Unknown song",
                                        color = MusicZoneTextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                supportingContent = {
                                    Column {
                                        Text(
                                            text = item.mediaMetadata.artist?.toString()
                                                ?: "Unknown artist",
                                            color = MusicZoneTextSecondary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                },
                                trailingContent = {
                                    IconButton(
                                        onClick = {
                                            onRemove(index)
                                        }) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove from queue",
                                            tint = MusicZoneTextSecondary
                                        )
                                    }
                                })
                        }
                    }
                }
            }
        }
    }
}