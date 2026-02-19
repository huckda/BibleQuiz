package com.huck.biblequiz.ui.screens.quiz

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huck.biblequiz.data.repository.BibleRepository
import com.huck.biblequiz.data.repository.ProgressRepository
import com.huck.biblequiz.model.QuizQuestion
import com.huck.biblequiz.model.Verse
import com.huck.biblequiz.util.BookNames
import com.huck.biblequiz.util.QuizGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizState(
    val questions: List<QuizQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val answers: Map<Int, List<String>> = emptyMap(),
    val showResult: Boolean = false,
    val totalCorrect: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null,
    val bookName: String = "",
    val chapter: Int = 0,
    val timeRemaining: Int = 0
) {
    val currentQuestion: QuizQuestion? get() = questions.getOrNull(currentIndex)
    val progress: Float get() = if (questions.isEmpty()) 0f else (currentIndex + 1).toFloat() / questions.size
    val isLastQuestion: Boolean get() = currentIndex >= questions.size - 1
}

@HiltViewModel
class QuizViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val bibleRepository: BibleRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(QuizState())
    val state: StateFlow<QuizState> = _state

    private val selections: String = savedStateHandle.get<String>("selections") ?: ""
    private val shuffle: Boolean = savedStateHandle.get<Boolean>("shuffle") ?: true
    val timerSeconds: Int = savedStateHandle.get<Int>("timerSeconds") ?: 0

    private var timerJob: Job? = null

    init {
        loadQuiz()
    }

    private fun loadQuiz() {
        viewModelScope.launch {
            try {
                val pairs = selections.split(",").mapNotNull { s ->
                    val parts = s.split(":")
                    if (parts.size == 2) Pair(parts[0].toInt(), parts[1].toInt()) else null
                }

                val allQuestions = mutableListOf<QuizQuestion>()
                var lastBookName = ""
                var lastChapter = 0

                for ((bookId, chapter) in pairs) {
                    val verseEntities = bibleRepository.getChapterVerses(bookId, chapter)
                    val verses = verseEntities.map {
                        Verse(it.bookId, it.chapter, it.verse, it.text)
                    }
                    allQuestions.addAll(QuizGenerator.generate(verses))
                    lastBookName = BookNames.getShortName(bookId)
                    lastChapter = chapter
                }

                _state.value = _state.value.copy(
                    questions = if (shuffle) allQuestions.shuffled() else allQuestions,
                    isLoading = false,
                    bookName = lastBookName,
                    chapter = lastChapter,
                    timeRemaining = timerSeconds
                )

                startTimer()
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    private fun startTimer() {
        if (timerSeconds <= 0) return
        timerJob?.cancel()
        _state.value = _state.value.copy(timeRemaining = timerSeconds)
        timerJob = viewModelScope.launch {
            var remaining = timerSeconds
            while (remaining > 0) {
                delay(1000)
                remaining--
                _state.value = _state.value.copy(timeRemaining = remaining)
            }
            if (!_state.value.showResult) {
                val question = _state.value.currentQuestion ?: return@launch
                val emptyAnswers = List(question.blanks.size) { "" }
                submitAnswers(emptyAnswers)
            }
        }
    }

    fun submitAnswers(answers: List<String>) {
        val question = _state.value.currentQuestion ?: return
        val idx = _state.value.currentIndex

        timerJob?.cancel()

        var correct = _state.value.totalCorrect
        question.blanks.forEachIndexed { i, blank ->
            val userAnswer = answers.getOrNull(i) ?: ""
            if (QuizGenerator.checkAnswer(userAnswer, blank.answer)) {
                correct++
            }
        }

        _state.value = _state.value.copy(
            answers = _state.value.answers + (idx to answers),
            totalCorrect = correct,
            showResult = true
        )
    }

    fun nextQuestion(): Boolean {
        val current = _state.value
        if (current.isLastQuestion) {
            saveScores()
            return true
        }
        _state.value = current.copy(
            currentIndex = current.currentIndex + 1,
            showResult = false
        )
        startTimer()
        return false
    }

    private fun saveScores() {
        viewModelScope.launch {
            val pairs = selections.split(",").mapNotNull { s ->
                val parts = s.split(":")
                if (parts.size == 2) Pair(parts[0].toInt(), parts[1].toInt()) else null
            }
            val totalBlanks = _state.value.questions.sumOf { it.blanks.size }
            for ((bookId, chapter) in pairs) {
                progressRepository.saveQuizScore(
                    bookId, chapter,
                    _state.value.totalCorrect, totalBlanks
                )
            }
        }
    }

    fun getTotalBlanks(): Int = _state.value.questions.sumOf { it.blanks.size }
}
