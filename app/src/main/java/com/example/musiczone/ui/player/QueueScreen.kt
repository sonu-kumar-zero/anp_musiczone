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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueScreen(
    queue: List<MediaItem>,
    currentMediaItem: MediaItem?,
    onRemove: (Int) -> Unit,
    onMove: (Int, Int) -> Unit,
    onDismiss: () -> Unit
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
                        start = 24.dp,
                        end = 16.dp,
                        bottom = 12.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
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
                lazyListState = listState,
                onMove = { from, to ->
                    onMove(from.index, to.index)
                })

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxHeight()
            ) {
                itemsIndexed(
                    items = queue,
                    key = { _, item ->
                        item.mediaId
                    }
                ) { index, item ->

                    ReorderableItem(
                        state = reorderableState,
                        key = item.mediaId
                    ) {
                        val isCurrent =
                            item.mediaId == currentMediaItem?.mediaId

                        ListItem(
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Default.DragHandle,
                                    contentDescription = "Drag to reorder",
                                    tint = MusicZoneTextSecondary,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .longPressDraggableHandle()
                                )
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

                                    if (isCurrent) {
                                        Text(
                                            text = "Playing",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MusicZonePurple
                                        )
                                    }
                                }
                            },
                            trailingContent = {
                                IconButton(
                                    onClick = {
                                        onRemove(index)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove from queue",
                                        tint = MusicZoneTextSecondary
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}