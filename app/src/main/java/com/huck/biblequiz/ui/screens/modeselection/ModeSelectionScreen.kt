package com.huck.biblequiz.ui.screens.modeselection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeSelectionScreen(
    selections: String,
    onStudy: () -> Unit,
    onQuiz: (shuffle: Boolean, timerSeconds: Int) -> Unit,
    onBack: () -> Unit
) {
    val chapterCount = selections.split(",").size
    var shuffleEnabled by remember { mutableStateOf(true) }
    var timedEnabled by remember { mutableStateOf(false) }
    var timerSeconds by remember { mutableFloatStateOf(30f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Choose Mode") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("<", style = MaterialTheme.typography.titleLarge)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "$chapterCount chapter${if (chapterCount > 1) "s" else ""} selected",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(Modifier.height(48.dp))

            Button(
                onClick = onStudy,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Study", style = MaterialTheme.typography.titleLarge)
            }

            Spacer(Modifier.height(16.dp))

            OutlinedButton(
                onClick = { onQuiz(shuffleEnabled, if (timedEnabled) timerSeconds.roundToInt() else 0) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Quiz", style = MaterialTheme.typography.titleLarge)
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Randomize verse order",
                    style = MaterialTheme.typography.bodyLarge
                )
                Switch(
                    checked = shuffleEnabled,
                    onCheckedChange = { shuffleEnabled = it }
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Timed mode",
                    style = MaterialTheme.typography.bodyLarge
                )
                Switch(
                    checked = timedEnabled,
                    onCheckedChange = { timedEnabled = it }
                )
            }

            if (timedEnabled) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "${timerSeconds.roundToInt()} seconds per question",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Slider(
                    value = timerSeconds,
                    onValueChange = { timerSeconds = it },
                    valueRange = 1f..59f,
                    steps = 57,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
