package com.huck.biblequiz.ui.screens.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.huck.biblequiz.ui.theme.CorrectGreen
import com.huck.biblequiz.ui.theme.IncorrectRed
import com.huck.biblequiz.util.BookNames
import com.huck.biblequiz.util.QuizGenerator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    selections: String,
    onFinished: (score: Int, total: Int) -> Unit,
    onBack: () -> Unit,
    viewModel: QuizViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("<", style = MaterialTheme.typography.titleLarge)
                    }
                }
            )
        }
    ) { padding ->
        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            state.error != null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: ${state.error}")
                }
            }
            state.questions.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No questions available")
                }
            }
            else -> {
                val question = state.currentQuestion ?: return@Scaffold
                val blankCount = question.blanks.size
                val userAnswers = remember(state.currentIndex) {
                    mutableStateListOf(*Array(blankCount) { "" })
                }

                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                ) {
                    LinearProgressIndicator(
                        progress = state.progress,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        "Question ${state.currentIndex + 1} of ${state.questions.size}",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Text(
                            "${BookNames.getShortName(question.verse.bookId)} ${question.verse.chapter}:${question.verse.verse}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            question.displayText,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(Modifier.height(24.dp))

                        question.blanks.forEachIndexed { i, blank ->
                            val label = "Blank ${i + 1}"

                            if (state.showResult) {
                                val userAnswer = userAnswers.getOrNull(i) ?: ""
                                val isCorrect = QuizGenerator.checkAnswer(userAnswer, blank.answer)
                                Row(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        if (isCorrect) "Correct!" else "Wrong",
                                        color = if (isCorrect) CorrectGreen else IncorrectRed,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                    if (!isCorrect) {
                                        Text(
                                            "Your answer: \"$userAnswer\" | Correct: \"${blank.answer}\"",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            } else {
                                OutlinedTextField(
                                    value = userAnswers.getOrElse(i) { "" },
                                    onValueChange = { userAnswers[i] = it },
                                    label = { Text(label) },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        if (state.showResult) {
                            Button(onClick = {
                                val finished = viewModel.nextQuestion()
                                if (finished) {
                                    onFinished(state.totalCorrect, viewModel.getTotalBlanks())
                                }
                            }) {
                                Text(if (state.isLastQuestion) "See Results" else "Next")
                            }
                        } else {
                            Button(
                                onClick = { viewModel.submitAnswers(userAnswers.toList()) },
                                enabled = userAnswers.any { it.isNotBlank() }
                            ) {
                                Text("Submit")
                            }
                        }
                    }
                }
            }
        }
    }
}
