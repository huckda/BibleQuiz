package com.huck.biblequiz.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun BibleQuizTheme(
    appColors: AppColors = AppColors(),
    content: @Composable () -> Unit
) {
    val colorScheme = lightColorScheme(
        primary = appColors.primary,
        onPrimary = appColors.onPrimary,
        secondary = Gold,
        onSecondary = DarkText,
        tertiary = DarkNavy,
        background = appColors.background,
        onBackground = appColors.onBackground,
        surface = appColors.background,
        onSurface = appColors.onBackground,
        surfaceVariant = LightGold,
        onSurfaceVariant = appColors.onBackground,
        outline = appColors.outline
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
