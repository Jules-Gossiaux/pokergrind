package com.pokergrind.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PokerGrindColors = darkColorScheme(
    primary = Primary,
    onPrimary = Color(0xFF102112),
    secondary = Progress,
    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceElevated,
    onSurfaceVariant = TextSecondary,
    error = Error,
    onError = Color.White,
)

@Composable
fun PokerGrindTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PokerGrindColors,
        typography = PokerGrindTypography,
        content = content,
    )
}
