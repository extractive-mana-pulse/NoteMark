package com.example.presentation.note_details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.notemark.core.presentation.R

@Composable
internal fun ExtendedFabFSheet(
    modifier: Modifier = Modifier,
    onEditClick: () -> Unit,
    onReaderModeClick: () -> Unit,
    isReaderMode: Boolean = false
) {
    Row(
        modifier = modifier
            .padding(16.dp)
            .background(
                MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.medium
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = onEditClick
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.edit),
                contentDescription = null
            )
        }

        IconButton(
            onClick = onReaderModeClick,
            modifier = Modifier
                .then(
                    if (isReaderMode) {
                        Modifier
                            .padding(4.dp)
                            .background(
                            color = Color(0x1A5977F7),
                            shape = MaterialTheme.shapes.medium
                        )
                    } else {
                        Modifier
                    }
                )
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.book_open),
                contentDescription = null,
                tint = if (isReaderMode) MaterialTheme.colorScheme.primary else LocalContentColor.current
            )
        }
    }
}