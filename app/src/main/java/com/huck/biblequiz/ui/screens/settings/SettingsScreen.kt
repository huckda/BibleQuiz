package com.huck.biblequiz.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.huck.biblequiz.ui.theme.AppColors
import com.huck.biblequiz.ui.theme.Cream
import com.huck.biblequiz.ui.theme.DarkNavy
import com.huck.biblequiz.ui.theme.DarkText
import com.huck.biblequiz.ui.theme.Gold
import com.huck.biblequiz.ui.theme.LightText
import com.huck.biblequiz.ui.theme.Navy
import com.huck.biblequiz.ui.theme.ThemeViewModel

private data class ColorOption(val color: Color, val label: String)

private val backgroundOptions = listOf(
    ColorOption(Cream, "Cream"),
    ColorOption(Color(0xFFFFFFFF), "White"),
    ColorOption(Color(0xFFF0F0F0), "Light Gray"),
    ColorOption(Color(0xFFE8F4FD), "Pale Blue"),
    ColorOption(Color(0xFFE8F5E9), "Pale Green"),
    ColorOption(Navy, "Navy"),
    ColorOption(Color(0xFF2D2D2D), "Charcoal"),
    ColorOption(Color(0xFF121212), "Black"),
)

private val primaryOptions = listOf(
    ColorOption(Navy, "Navy"),
    ColorOption(Gold, "Gold"),
    ColorOption(Color(0xFF2E7D32), "Forest"),
    ColorOption(Color(0xFF6A1B9A), "Purple"),
    ColorOption(Color(0xFFC62828), "Crimson"),
    ColorOption(Color(0xFF1565C0), "Royal Blue"),
    ColorOption(Color(0xFF00695C), "Teal"),
    ColorOption(Color(0xFFE65100), "Orange"),
    ColorOption(Color(0xFF455A64), "Slate"),
    ColorOption(Color(0xFF37474F), "Dark Slate"),
)

private val onPrimaryOptions = listOf(
    ColorOption(LightText, "White"),
    ColorOption(DarkText, "Black"),
    ColorOption(Gold, "Gold"),
    ColorOption(Cream, "Cream"),
)

private val textOptions = listOf(
    ColorOption(DarkText, "Dark"),
    ColorOption(LightText, "White"),
    ColorOption(Navy, "Navy"),
    ColorOption(Gold, "Gold"),
    ColorOption(Color(0xFF5F5F5F), "Gray"),
    ColorOption(DarkNavy, "Dark Navy"),
)

private val outlineOptions = listOf(
    ColorOption(Navy, "Navy"),
    ColorOption(Gold, "Gold"),
    ColorOption(Color(0xFF9E9E9E), "Gray"),
    ColorOption(Color(0xFF000000), "Black"),
    ColorOption(Color(0xFFFFFFFF), "White"),
    ColorOption(Color(0x00000000), "None"),
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    themeViewModel: ThemeViewModel,
    onBack: () -> Unit
) {
    val colors by themeViewModel.colors.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Appearance") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { themeViewModel.updateColors(AppColors()) }) {
                        Text("Reset")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            ColorSection(
                title = "Background",
                options = backgroundOptions,
                selected = colors.background,
                onSelect = { themeViewModel.updateColors(colors.copy(background = it)) }
            )

            ColorSection(
                title = "Button Color",
                options = primaryOptions,
                selected = colors.primary,
                onSelect = { themeViewModel.updateColors(colors.copy(primary = it)) }
            )

            ColorSection(
                title = "Button Text",
                options = onPrimaryOptions,
                selected = colors.onPrimary,
                onSelect = { themeViewModel.updateColors(colors.copy(onPrimary = it)) }
            )

            ColorSection(
                title = "Text Color",
                options = textOptions,
                selected = colors.onBackground,
                onSelect = { themeViewModel.updateColors(colors.copy(onBackground = it)) }
            )

            ColorSection(
                title = "Button Outline",
                options = outlineOptions,
                selected = colors.outline,
                onSelect = { themeViewModel.updateColors(colors.copy(outline = it)) }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ColorSection(
    title: String,
    options: List<ColorOption>,
    selected: Color,
    onSelect: (Color) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            options.forEach { option ->
                val isSelected = option.color == selected
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(option.color)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outline,
                                shape = CircleShape
                            )
                            .clickable { onSelect(option.color) }
                    )
                    Text(
                        option.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}
