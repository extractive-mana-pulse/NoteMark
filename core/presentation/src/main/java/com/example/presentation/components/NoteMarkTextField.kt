package com.example.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.notemark.core.presentation.R

@Composable
fun NoteMarkTextField(
    text: String,
    onValueChange: (String) -> Unit,
    label: String,
    hint: String,
    isInputSecret: Boolean,
    modifier: Modifier = Modifier,
    focusManager: FocusManager,
    focusDirection: FocusDirection,
    imeAction: ImeAction,
    showFocusText: String? = null,
    errorText: String? = null,
    isError: Boolean = false
) {
    var isPasswordVisible by remember {
        mutableStateOf(false)
    }

    var isFocused by remember { mutableStateOf(false) }

    // Show error only when field is not focused, not empty, and has error
    val shouldShowError = isError && !isFocused && text.isNotEmpty()

    Column(
        modifier = modifier
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = text,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    isFocused = it.isFocused
                },
            visualTransformation = if(isInputSecret && !isPasswordVisible) {
                PasswordVisualTransformation(mask = '*')
            } else VisualTransformation.None,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                cursorColor = if (shouldShowError) Color.Red else MaterialTheme.colorScheme.primary,
                focusedBorderColor = if (shouldShowError) Color.Red else MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = if (shouldShowError) Color.Red else Color.Transparent,
                errorBorderColor = Color.Red,
                errorCursorColor = Color.Red
            ),
            placeholder = {
                Text(
                    text = hint,
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            textStyle = MaterialTheme.typography.bodyLarge,
            shape = RoundedCornerShape(10.dp),
            isError = shouldShowError,
            trailingIcon = {
                if(isInputSecret) {
                    IconButton(
                        onClick = {
                            isPasswordVisible = !isPasswordVisible
                        }
                    ) {
                        when {
                            isPasswordVisible -> {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.eye),
                                    contentDescription = "Hide password"
                                )
                            }
                            !isPasswordVisible -> {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.eye_off),
                                    contentDescription = "Show password"
                                )
                            }
                        }
                    }
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(
                        focusDirection = focusDirection
                    )
                }
            )
        )

        when {
            shouldShowError && errorText != null -> {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = errorText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.Red
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
            isFocused && showFocusText != null && !shouldShowError -> {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = showFocusText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}