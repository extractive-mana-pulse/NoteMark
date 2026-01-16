package com.example.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.example.domain.Note
import com.example.presentation.DeviceConfiguration
import com.example.presentation.getFormattedCreatedAt
import com.example.presentation.truncateAtWord

@Composable
internal fun NoteItem(
    modifier: Modifier,
    onNavigateToDetailsWithId: (String) -> Unit,
    note: Note,
    notes: LazyPagingItems<Note>
) {

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val deviceConfiguration = DeviceConfiguration.Companion.fromWindowSizeClass(windowSizeClass)
    val isPhone = deviceConfiguration == DeviceConfiguration.MOBILE_PORTRAIT

    var contextMenuNoteId by rememberSaveable { mutableStateOf<String?>(null) }
    val haptics = LocalHapticFeedback.current

    Column(
        modifier = modifier
            .combinedClickable(
                onClick = { onNavigateToDetailsWithId(note.id) },
                onLongClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    contextMenuNoteId = note.id
                },
            )
            .background(
                MaterialTheme.colorScheme.surfaceContainerLowest,
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
    ) {

        Text(
            text = note.getFormattedCreatedAt(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = note.title,
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Text(
            text = note.content.truncateAtWord(if (isPhone) 150 else 250),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    if (contextMenuNoteId != null) {
        NoteActionSheet(
            isVisible = note.id == contextMenuNoteId,
            onDismissSheet = { contextMenuNoteId = null },

            noteId = contextMenuNoteId ?: "",
            notes = notes
        )
    }
}
