package com.example.musiczone.ui.player

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.musiczone.data.local.FavoriteGroupEntity
import com.example.musiczone.ui.theme.MusicZoneTextPrimary

@Composable
fun AddToGroupDialog(
    groups: List<FavoriteGroupEntity>,
    selectedGroupIds: Set<Long>,
    onGroupToggle: (Long) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add to groups",
                color = MusicZoneTextPrimary
            )
        },
        text = {
            Column {
                groups.forEach { group ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = group.id in selectedGroupIds,
                            onCheckedChange = {
                                onGroupToggle(group.id)
                            }
                        )

                        Text(
                            text = group.name,
                            color = MusicZoneTextPrimary
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text("Done")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}