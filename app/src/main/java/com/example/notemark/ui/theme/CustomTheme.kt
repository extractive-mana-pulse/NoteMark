package com.example.notemark.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class CustomTheme(
    val onSurface: Color,
    val onSurfaceVar: Color,
    val opacity12: Color,
    val surface: Color,
    val surfaceLowest: Color,
    val error: Color,
    val primary : Color,
    val opacity10: Color,
    val onPrimary: Color,
    val opacity012: Color,
)

val lightThemeColors = CustomTheme(
    onSurface = Color(0xFF1B1B1C),
    onSurfaceVar = Color(0xFF535364),
    opacity12 = Color(0x1F1B1B1C),
    surface = Color(0xFFEFEFF2),
    surfaceLowest = Color(0xFFFFFFFF),
    error = Color(0xFFE1294B),
    primary = Color(0xFF5977F7),
    opacity10 = Color(0x1A5977F7),
    onPrimary = Color(0xFFFFFFFF),
    opacity012 = Color(0x1FFFFFFF),
)

val darkThemeColors = CustomTheme(
    onSurface = Color(0xFF1B1B1C),
    onSurfaceVar = Color(0xFF535364),
    opacity12 = Color(0x1F1B1B1C),
    surface = Color(0xFFEFEFF2),
    surfaceLowest = Color(0xFFFFFFFF),
    error = Color(0xFFE1294B),
    primary = Color(0xFF5977F7),
    opacity10 = Color(0x1A5977F7),
    onPrimary = Color(0xFFFFFFFF),
    opacity012 = Color(0x1FFFFFFF),
)

val LocalTheme = staticCompositionLocalOf<CustomTheme> { error("No local colors provided.") }