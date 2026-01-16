package com.example.presentation.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
internal fun SingleItem(
    modifier: Modifier = Modifier,
    onEndIconAndTextClick: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    supportingText: String? = null,
    supportingTextColor: Color? = null,
    headline: String,
    headlineTextColor: Color,
    leadingIcon: ImageVector? = null,
    iconTint: Color? = null,
    endIcon: ImageVector? = null,
    endIconTint: Color? = null,
    endText: String? = null,
    endTextColor: Color? = null,
    showDropdown: Boolean = false,
    onDismissDropdown: (() -> Unit)? = null,
    dropdownContent: (@Composable ColumnScope.() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        leadingIcon?.let { icon ->
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint ?: MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = headline,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = headlineTextColor
                )
            )

            if (!supportingText.isNullOrBlank()) {
                Text(
                    text = supportingText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = supportingTextColor ?: MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        val hasEndContent = !endText.isNullOrBlank() || endIcon != null
        if (hasEndContent) {
            Box {
                Row(
                    modifier = Modifier.then(
                        if (onEndIconAndTextClick != null) {
                            Modifier.clickable { onEndIconAndTextClick() }
                        } else {
                            Modifier
                        }
                    ),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!endText.isNullOrBlank()) {
                        Text(
                            text = endText,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = endTextColor ?: MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    endIcon?.let { icon ->
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = endIconTint ?: MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (dropdownContent != null) {
                    DropdownMenu(
                        expanded = showDropdown,
                        onDismissRequest = onDismissDropdown ?: {}
                    ) {
                        dropdownContent()
                    }
                }
            }
        }
    }
}