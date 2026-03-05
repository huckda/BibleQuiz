package com.huck.biblequiz.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.huck.biblequiz.ui.theme.AppColors
import com.huck.biblequiz.ui.theme.ThemeViewModel

private data class ColorEntry(
    val label: String,
    val description: String,
    val get: AppColors.() -> Color,
    val set: AppColors.(Color) -> AppColors
)

private val colorEntries = listOf(
    ColorEntry("Background", "Screen background color",
        get = { background },
        set = { copy(background = it) }
    ),
    ColorEntry("Button Color", "Fill color for buttons",
        get = { primary },
        set = { copy(primary = it) }
    ),
    ColorEntry("Button Text", "Text color on buttons",
        get = { onPrimary },
        set = { copy(onPrimary = it) }
    ),
    ColorEntry("Text Color", "General text color",
        get = { onBackground },
        set = { copy(onBackground = it) }
    ),
    ColorEntry("Button Outline", "Border color for outlined buttons and cards",
        get = { outline },
        set = { copy(outline = it) }
    ),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    themeViewModel: ThemeViewModel,
    onBack: () -> Unit
) {
    val colors by themeViewModel.colors.collectAsStateWithLifecycle()
    var activeEntry by remember { mutableStateOf<ColorEntry?>(null) }

    // Show the color picker dialog for whichever entry is active
    activeEntry?.let { entry ->
        ColorPickerDialog(
            title = entry.label,
            initialColor = entry.get(colors),
            onColorSelected = { themeViewModel.updateColors(entry.set(colors, it)) },
            onDismiss = { activeEntry = null }
        )
    }

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
        ) {
            colorEntries.forEachIndexed { index, entry ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { activeEntry = entry }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(entry.label, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            entry.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(entry.get(colors))
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outline,
                                RoundedCornerShape(8.dp)
                            )
                    )
                }
                if (index < colorEntries.lastIndex) {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
}
