package com.example.musiczone.ui.navigation.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
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
import com.example.musiczone.ui.theme.MusicZoneElevated
import com.example.musiczone.ui.theme.MusicZonePurple
import com.example.musiczone.ui.theme.MusicZoneTextPrimary
import com.example.musiczone.ui.theme.MusicZoneTextSecondary
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem

@Composable
fun FavoritesTab(
    songs: List<Song>, viewModel: MusicViewModel
) {
    val favoriteGroups by viewModel.favoriteGroups.collectAsState()

    var selectedGroupId by remember {
        mutableStateOf<Long?>(null)
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (selectedGroupId == null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Favorite Groups", color = MusicZoneTextPrimary
                )

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
            }

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
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = favoriteGroups, key = { group -> group.id }) { group ->
                        var menuExpanded by remember(group.id) {
                            mutableStateOf(false)
                        }
                        Card(
                            modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
                                containerColor = MusicZoneElevated
                            ), onClick = {
                                selectedGroupId = group.id
                            }) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = MusicZonePurple
                                )

                                Text(
                                    text = group.name,
                                    color = MusicZoneTextPrimary,
                                    modifier = Modifier.padding(top = 12.dp)
                                )

                                val groupSongIds by viewModel.observeFavoriteGroupSongIds(group.id)
                                    .collectAsState()

                                Text(
                                    text = "${groupSongIds.size} songs",
                                    color = MusicZoneTextSecondary,
                                    modifier = Modifier.padding(top = 4.dp)
                                )

                                IconButton(
                                    onClick = {
                                        menuExpanded = true
                                    }) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "Group options",
                                        tint = MusicZoneTextSecondary
                                    )
                                }

                                DropdownMenu(
                                    expanded = menuExpanded, onDismissRequest = {
                                        menuExpanded = false
                                    }) {
                                    DropdownMenuItem(text = {
                                        Text("Rename")
                                    }, onClick = {
                                        menuExpanded = false
                                        groupToRename = group.id
                                        renameGroupName = group.name
                                    })

                                    DropdownMenuItem(
                                        text = { Text("Delete") },
                                        enabled = group.name != "Favorites",
                                        onClick = {
                                            menuExpanded = false
                                            viewModel.deleteFavoriteGroup(group.id)
                                        })
                                }
                            }
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
        AlertDialog(onDismissRequest = {
            showCreateDialog = false
        }, title = {
            Text("New Favorite Group")
        }, text = {
            OutlinedTextField(value = groupName, onValueChange = {
                groupName = it
            }, singleLine = true, label = {
                Text("Group name")
            })
        }, confirmButton = {
            Button(
                onClick = {
                    viewModel.createFavoriteGroup(groupName)
                    showCreateDialog = false
                }, enabled = groupName.trim().isNotEmpty()
            ) {
                Text("Create")
            }
        }, dismissButton = {
            TextButton(
                onClick = {
                    showCreateDialog = false
                }) {
                Text("Cancel")
            }
        })
    }

    if (groupToRename != null) {
        AlertDialog(onDismissRequest = {
            groupToRename = null
        }, title = {
            Text("Rename Favorite Group")
        }, text = {
            OutlinedTextField(value = renameGroupName, onValueChange = {
                renameGroupName = it
            }, singleLine = true, label = {
                Text("Group name")
            })
        }, confirmButton = {
            Button(
                onClick = {
                    val groupId = groupToRename

                    if (groupId != null) {
                        viewModel.renameFavoriteGroup(
                            groupId = groupId, name = renameGroupName
                        )
                    }

                    groupToRename = null
                }, enabled = renameGroupName.trim().isNotEmpty()
            ) {
                Text("Rename")
            }
        }, dismissButton = {
            TextButton(
                onClick = {
                    groupToRename = null
                }) {
                Text("Cancel")
            }
        })
    }
}