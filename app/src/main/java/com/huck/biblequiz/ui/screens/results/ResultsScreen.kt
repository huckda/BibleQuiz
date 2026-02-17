package com.huck.biblequiz.ui.screens.results

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.huck.biblequiz.ui.theme.CorrectGreen
import com.huck.biblequiz.ui.theme.Gold
import com.huck.biblequiz.ui.theme.IncorrectRed

@Composable
fun ResultsScreen(
    score: Int,
    total: Int,
    selections: String,
    onRetry: () -> Unit,
    onStudy: () -> Unit,
    onHome: () -> Unit
) {
    val percentage = if (total == 0) 0 else (score * 100) / total
    val message = when {
        percentage >= 90 -> "Excellent!"
        percentage >= 70 -> "Great job!"
        percentage >= 50 -> "Good effort!"
        else -> "Keep studying!"
    }
    val messageColor = when {
        percentage >= 70 -> CorrectGreen
        percentage >= 50 -> Gold
        else -> IncorrectRed
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                message,
                style = MaterialTheme.typography.headlineLarge,
                color = messageColor
            )

            Spacer(Modifier.height(16.dp))

            Text(
                "$score / $total",
                style = MaterialTheme.typography.headlineLarge
            )

            Text(
                "$percentage%",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(48.dp))

            Button(
                onClick = onRetry,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Try Again")
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onStudy,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Study Verses")
            }

            Spacer(Modifier.height(12.dp))

            TextButton(
                onClick = onHome,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Home")
            }
        }
    }
}
