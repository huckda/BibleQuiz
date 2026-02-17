package com.huck.biblequiz.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Navy,
    onPrimary = LightText,
    secondary = Gold,
    onSecondary = DarkText,
    tertiary = DarkNavy,
    background = Cream,
    onBackground = DarkText,
    surface = Cream,
    onSurface = DarkText,
    surfaceVariant = LightGold,
    onSurfaceVariant = DarkText
)

private val DarkColorScheme = darkColorScheme(
    primary = Gold,
    onPrimary = DarkNavy,
    secondary = Navy,
    onSecondary = LightText,
    tertiary = LightGold,
    background = DarkNavy,
    onBackground = LightText,
    surface = DarkNavy,
    onSurface = LightText,
    surfaceVariant = Navy,
    onSurfaceVariant = LightText
)

@Composable
fun BibleQuizTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
