package com.huck.biblequiz.ui.theme

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

data class AppColors(
    val background: Color = Cream,
    val primary: Color = Navy,
    val onPrimary: Color = LightText,
    val onBackground: Color = DarkText,
    val outline: Color = Navy
)

@Singleton
class ThemePreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)

    fun save(colors: AppColors) {
        prefs.edit()
            .putInt("background", colors.background.toArgb())
            .putInt("primary", colors.primary.toArgb())
            .putInt("onPrimary", colors.onPrimary.toArgb())
            .putInt("onBackground", colors.onBackground.toArgb())
            .putInt("outline", colors.outline.toArgb())
            .apply()
    }

    fun load(): AppColors {
        val d = AppColors()
        return AppColors(
            background = Color(prefs.getInt("background", d.background.toArgb())),
            primary = Color(prefs.getInt("primary", d.primary.toArgb())),
            onPrimary = Color(prefs.getInt("onPrimary", d.onPrimary.toArgb())),
            onBackground = Color(prefs.getInt("onBackground", d.onBackground.toArgb())),
            outline = Color(prefs.getInt("outline", d.outline.toArgb()))
        )
    }
}
