package com.example.musiczone.ui.navigation.tabs

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.musiczone.model.Song
import com.example.musiczone.ui.MusicViewModel
import com.example.musiczone.ui.player.SongItem
import com.example.musiczone.ui.theme.MusicZonePurple
import com.example.musiczone.ui.theme.MusicZoneTextPrimary
import com.example.musiczone.ui.theme.MusicZoneTextSecondary
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import com.example.musiczone.ui.components.MusicZoneHeader
import com.example.musiczone.ui.player.AlbumArtwork
import com.example.musiczone.ui.theme.MusicZoneSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesTab(
    songs: List<Song>, viewModel: MusicViewModel
) {
    val favoriteGroups by viewModel.favoriteGroups.collectAsState()

    var selectedGroupId by remember {
        mutableStateOf<Long?>(null)
    }

    BackHandler(
        enabled = selectedGroupId != null
    ) {
        selectedGroupId = null
    }

    var showCreateDialog by remember {
        mutableStateOf(false)
    }
    var groupToRename by remember {
        mutableStateOf<Long?>(null)
    }

    var renameGroupName by remember {
        mutableStateOf("")
    }

    var groupName by remember {
        mutableStateOf("")
    }
    var groupOptionsId by remember { mutableStateOf<Long?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (selectedGroupId == null) {
            MusicZoneHeader(
                title = "Favorites", subtitle = "Your favorite music", actions = {
                    IconButton(
                        onClick = {
                            groupName = ""
                            showCreateDialog = true
                        }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Create favorite group",
                            tint = MusicZonePurple
                        )
                    }
                })

            if (favoriteGroups.isEmpty()) {
                Text(
                    text = "No favorite groups",
                    color = MusicZoneTextSecondary,
                    modifier = Modifier.padding(top = 24.dp)
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        horizontal = 12.dp, vertical = 8.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        items = favoriteGroups, key = { group -> group.id }) { group ->

                        val groupSongIds by viewModel.observeFavoriteGroupSongIds(group.id)
                            .collectAsState()

                        val groupSongs = songs.filter {
                            it.id in groupSongIds
                        }

                        val firstSong = groupSongs.firstOrNull()

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedGroupId = group.id
                                },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MusicZoneSurface
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 2.dp
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp)
                            ) {

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                        .clip(
                                            RoundedCornerShape(12.dp)
                                        )
                                ) {

                                    if (firstSong != null) {
                                        AlbumArtwork(
                                            song = firstSong, modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Favorite,
                                                contentDescription = null,
                                                tint = MusicZonePurple
                                            )
                                        }
                                    }


                                    IconButton(
                                        onClick = {
                                            groupOptionsId = group.id
                                        }, modifier = Modifier.align(
                                            Alignment.TopEnd
                                        )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.MoreVert,
                                            contentDescription = "Group options",
                                            tint = MusicZoneTextPrimary
                                        )
                                    }
                                }

                                Spacer(
                                    modifier = Modifier.height(10.dp)
                                )

                                Text(
                                    text = group.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MusicZoneTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(
                                    modifier = Modifier.height(2.dp)
                                )

                                Text(
                                    text = "${groupSongIds.size} songs",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MusicZoneTextSecondary,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                val optionsGroup = favoriteGroups.firstOrNull {
                    it.id == groupOptionsId
                }

                if (optionsGroup != null) {
                    ModalBottomSheet(
                        onDismissRequest = {
                            groupOptionsId = null
                        }, sheetState = rememberModalBottomSheetState(
                            skipPartiallyExpanded = true
                        ), containerColor = MusicZoneSurface
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 24.dp)
                        ) {

                            Text(
                                text = optionsGroup.name,
                                style = MaterialTheme.typography.headlineSmall,
                                color = MusicZoneTextPrimary,
                                modifier = Modifier.padding(
                                    horizontal = 24.dp, vertical = 16.dp
                                )
                            )

                            ListItem(
                                modifier = Modifier.clickable {
                                groupOptionsId = null
                                groupToRename = optionsGroup.id
                                renameGroupName = optionsGroup.name
                            }, colors = ListItemDefaults.colors(
                                containerColor = Color.Transparent
                            ), leadingContent = {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = MusicZoneTextPrimary
                                )
                            }, headlineContent = {
                                Text(
                                    text = "Rename", color = MusicZoneTextPrimary
                                )
                            })

                            ListItem(
                                modifier = Modifier.clickable(
                                enabled = optionsGroup.name != "Favorites"
                            ) {
                                groupOptionsId = null
                                viewModel.deleteFavoriteGroup(
                                    optionsGroup.id
                                )
                            }, colors = ListItemDefaults.colors(
                                containerColor = Color.Transparent
                            ), leadingContent = {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = if (optionsGroup.name == "Favorites") {
                                        MusicZoneTextSecondary
                                    } else {
                                        MusicZoneTextPrimary
                                    }
                                )
                            }, headlineContent = {
                                Text(
                                    text = "Delete",
                                    color = if (optionsGroup.name == "Favorites") {
                                        MusicZoneTextSecondary
                                    } else {
                                        MusicZoneTextPrimary
                                    }
                                )
                            })
                        }
                    }
                }
            }
        } else {
            val selectedGroup = favoriteGroups.firstOrNull {
                it.id == selectedGroupId
            }

            selectedGroup?.let { group ->
                val groupSongIds by viewModel.observeFavoriteGroupSongIds(group.id).collectAsState()

                val groupSongs = songs.filter { song ->
                    song.id in groupSongIds
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            selectedGroupId = null
                        }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MusicZoneTextPrimary
                        )
                    }

                    Text(
                        text = group.name, color = MusicZoneTextPrimary
                    )
                }

                if (groupSongs.isEmpty()) {
                    Text(
                        text = "No songs in this group",
                        color = MusicZoneTextSecondary,
                        modifier = Modifier.padding(top = 24.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = groupSongs, key = { song -> song.id }) { song ->
                            SongItem(song = song, onClick = {
                                viewModel.playSong(song)
                            }, onPlay = {
                                viewModel.playSong(song)
                            }, isFavorite = viewModel.isFavorite(song.id), onFavorite = {
                                viewModel.toggleFavorite(song.id)
                            }, onAddToGroup = {}, onRemoveFromGroup = {
                                viewModel.removeSongFromFavoriteGroup(
                                    groupId = group.id, songId = song.id
                                )
                            }, onAddToQueue = {
                                viewModel.addToQueue(song)
                            })
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        val trimmedGroupName = groupName.trim()

        AlertDialog(onDismissRequest = {
            showCreateDialog = false
        }, containerColor = MusicZoneSurface, shape = RoundedCornerShape(24.dp), title = {
            Text(
                text = "Create favorite group",
                style = MaterialTheme.typography.headlineSmall,
                color = MusicZoneTextPrimary
            )
        }, text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Create a new collection for your favorite songs.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MusicZoneTextSecondary
                )

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                OutlinedTextField(
                    value = groupName,
                    onValueChange = {
                        groupName = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    label = {
                        Text("Group name")
                    },
                    placeholder = {
                        Text("e.g. Workout")
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    )
                )
            }
        }, confirmButton = {
            Button(
                onClick = {
                    if (trimmedGroupName != "Favorites") {
                        viewModel.createFavoriteGroup(
                            trimmedGroupName
                        )
                        groupName = ""
                        showCreateDialog = false
                    }
                },
                enabled = trimmedGroupName.isNotEmpty() && trimmedGroupName != "Favorites",
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MusicZonePurple
                )
            ) {
                Text("Create")
            }
        }, dismissButton = {
            TextButton(
                onClick = {
                    showCreateDialog = false
                    groupName = ""
                }) {
                Text(
                    text = "Cancel", color = MusicZoneTextSecondary
                )
            }
        })
    }

    if (groupToRename != null) {
        val trimmedGroupName = renameGroupName.trim()

        AlertDialog(onDismissRequest = {
            groupToRename = null
        }, containerColor = MusicZoneSurface, shape = RoundedCornerShape(24.dp), title = {
            Text(
                text = "Rename favorite group",
                style = MaterialTheme.typography.headlineSmall,
                color = MusicZoneTextPrimary
            )
        }, text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Choose a new name for this collection.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MusicZoneTextSecondary
                )

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                OutlinedTextField(
                    value = renameGroupName,
                    onValueChange = {
                        renameGroupName = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    label = {
                        Text("Group name")
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    )
                )
            }
        }, confirmButton = {
            Button(
                onClick = {
                    val groupId = groupToRename

                    if (groupId != null && trimmedGroupName != "Favorites") {
                        viewModel.renameFavoriteGroup(
                            groupId = groupId, name = trimmedGroupName
                        )
                    }

                    groupToRename = null
                    renameGroupName = ""
                },
                enabled = trimmedGroupName.isNotEmpty() && trimmedGroupName != "Favorites",
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MusicZonePurple
                )
            ) {
                Text("Rename")
            }
        }, dismissButton = {
            TextButton(
                onClick = {
                    groupToRename = null
                    renameGroupName = ""
                }) {
                Text(
                    text = "Cancel", color = MusicZoneTextSecondary
                )
            }
        })
    }
}