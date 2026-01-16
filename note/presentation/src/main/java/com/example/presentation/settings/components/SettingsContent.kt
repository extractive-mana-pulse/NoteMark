package com.example.presentation.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.notemark.core.presentation.R
import com.example.presentation.SyncInterval

@Composable
internal fun SettingsContent(
    onLogoutClick: () -> Unit,
    onSyncClick: () -> Unit,
    onSyncIntervalClick: () -> Unit,
    showSyncIntervalDropdown: Boolean,
    onDismissSyncIntervalDropdown: () -> Unit,
    selectedSyncInterval: SyncInterval,
    onSyncIntervalSelected: (SyncInterval) -> Unit
) {
    SingleItem(
        onEndIconAndTextClick = onSyncIntervalClick,
        supportingText = null,
        headline = stringResource(R.string.sync_interval),
        headlineTextColor = MaterialTheme.colorScheme.onSurface,
        leadingIcon = ImageVector.vectorResource(R.drawable.clock),
        iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
        endIcon = ImageVector.vectorResource(R.drawable.chevron_right),
        endIconTint = MaterialTheme.colorScheme.onSurfaceVariant,
        endText = selectedSyncInterval.displayName,
        endTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        showDropdown = showSyncIntervalDropdown,
        onDismissDropdown = onDismissSyncIntervalDropdown,
        dropdownContent = {
            SyncInterval.entries.forEach { interval ->
                DropdownMenuItem(
                    modifier = Modifier
                        .width(190.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = interval.displayName
                            )

                            if (interval == selectedSyncInterval) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.check),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    },
                    onClick = {
                        onSyncIntervalSelected(interval)
                    }
                )
            }
        }
    )

    SingleItem(
        onClick = onSyncClick,
        supportingText = stringResource(R.string.last_sync),
        headline = stringResource(R.string.sync_data),
        headlineTextColor = MaterialTheme.colorScheme.onSurface,
        supportingTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        leadingIcon = ImageVector.vectorResource(R.drawable.sync_icon),
        iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
        endIcon = null,
        endIconTint = null,
        endText = null,
        endTextColor = null
    )

    SingleItem(
        onClick = onLogoutClick,
        headline = stringResource(R.string.logout),
        supportingText = null,
        headlineTextColor = MaterialTheme.colorScheme.error,
        supportingTextColor = null,
        leadingIcon = ImageVector.vectorResource(R.drawable.logout),
        iconTint = MaterialTheme.colorScheme.error,
        endIcon = null,
        endIconTint = null,
        endText = null,
        endTextColor = null
    )
}