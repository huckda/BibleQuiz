package com.huck.biblequiz.ui.screens.study

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huck.biblequiz.data.repository.BibleRepository
import com.huck.biblequiz.data.repository.ProgressRepository
import com.huck.biblequiz.model.Verse
import com.huck.biblequiz.util.BookNames
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChapterContent(
    val bookId: Int,
    val chapter: Int,
    val bookName: String,
    val verses: List<Verse>
)

data class StudyState(
    val chapters: List<ChapterContent> = emptyList(),
    val currentIndex: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null
) {
    val currentChapter: ChapterContent? get() = chapters.getOrNull(currentIndex)
    val hasPrev: Boolean get() = currentIndex > 0
    val hasNext: Boolean get() = currentIndex < chapters.size - 1
}

@HiltViewModel
class StudyViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val bibleRepository: BibleRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(StudyState())
    val state: StateFlow<StudyState> = _state

    init {
        val selections = savedStateHandle.get<String>("selections") ?: ""
        loadChapters(selections)
    }

    private fun loadChapters(selections: String) {
        viewModelScope.launch {
            try {
                val pairs = selections.split(",").mapNotNull { s ->
                    val parts = s.split(":")
                    if (parts.size == 2) Pair(parts[0].toInt(), parts[1].toInt()) else null
                }

                val chapters = pairs.map { (bookId, chapter) ->
                    val verses = bibleRepository.getChapterVerses(bookId, chapter)
                    progressRepository.markStudied(bookId, chapter)
                    ChapterContent(
                        bookId = bookId,
                        chapter = chapter,
                        bookName = BookNames.getShortName(bookId),
                        verses = verses.map { Verse(it.bookId, it.chapter, it.verse, it.text) }
                    )
                }

                _state.value = _state.value.copy(chapters = chapters, isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun nextChapter() {
        if (_state.value.hasNext) {
            _state.value = _state.value.copy(currentIndex = _state.value.currentIndex + 1)
        }
    }

    fun prevChapter() {
        if (_state.value.hasPrev) {
            _state.value = _state.value.copy(currentIndex = _state.value.currentIndex - 1)
        }
    }
}
