package com.example.presentation.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.presentation.DeleteNoteUiState
import com.example.notemark.core.presentation.R

@Composable
internal fun ActionContent(
    deleteNoteState: DeleteNoteUiState,
    onDeleteClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (deleteNoteState is DeleteNoteUiState.Success) {
            SuccessContent()
        } else {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .clickable(
                    enabled = deleteNoteState !is DeleteNoteUiState.Loading
                ) {
                    onDeleteClick()
                },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            ListItem(
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent
                ),
                headlineContent = {
                    Text(
                        text = when (deleteNoteState) {
                            is DeleteNoteUiState.Loading -> "Deleting..."
                            else -> "Delete Note?"
                        },
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = if (deleteNoteState is DeleteNoteUiState.Loading) {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            } else {
                                MaterialTheme.colorScheme.error
                            }
                        )
                    )
                },
                supportingContent = {
                    Text(
                        text = "Are you sure you want to delete this note? This action cannot be undone.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                leadingContent = {
                    Box(
                        modifier = Modifier.size(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (deleteNoteState is DeleteNoteUiState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.outline_delete_outline_24),
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        }
    }
}