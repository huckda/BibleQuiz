package com.huck.biblequiz.ui.screens.chapterselection

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huck.biblequiz.data.repository.BibleRepository
import com.huck.biblequiz.data.repository.ProgressRepository
import com.huck.biblequiz.util.BookNames
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BookWithChapters(
    val bookId: Int,
    val name: String,
    val chapterCount: Int,
    val studiedChapters: Set<Int> = emptySet(),
    val quizzedChapters: Set<Int> = emptySet()
)

data class ChapterSelectionState(
    val books: List<BookWithChapters> = emptyList(),
    val selectedChapters: Map<Int, Set<Int>> = emptyMap(),
    val isLoading: Boolean = true
)

@HiltViewModel
class ChapterSelectionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val bibleRepository: BibleRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ChapterSelectionState())
    val state: StateFlow<ChapterSelectionState> = _state

    init {
        val bookIds = savedStateHandle.get<String>("bookIds")
            ?.split(",")
            ?.mapNotNull { it.toIntOrNull() }
            ?: emptyList()
        loadBooks(bookIds)
    }

    private fun loadBooks(bookIds: List<Int>) {
        viewModelScope.launch {
            val allBooks = bibleRepository.getBooks()
            val booksWithChapters = bookIds.mapNotNull { id ->
                val entity = allBooks.find { it.bookId == id } ?: return@mapNotNull null
                val progress = progressRepository.getBookProgress(id)
                BookWithChapters(
                    bookId = id,
                    name = BookNames.getShortName(id),
                    chapterCount = entity.chapters,
                    studiedChapters = progress.filter { it.studied }.map { it.chapter }.toSet(),
                    quizzedChapters = progress.filter { it.quizzed }.map { it.chapter }.toSet()
                )
            }
            _state.value = _state.value.copy(books = booksWithChapters, isLoading = false)
        }
    }

    fun toggleChapter(bookId: Int, chapter: Int) {
        val current = _state.value.selectedChapters
        val bookChapters = current[bookId] ?: emptySet()
        val updated = if (chapter in bookChapters) bookChapters - chapter else bookChapters + chapter
        _state.value = _state.value.copy(
            selectedChapters = if (updated.isEmpty()) current - bookId else current + (bookId to updated)
        )
    }

    fun selectAllChapters(bookId: Int, chapterCount: Int) {
        val current = _state.value.selectedChapters
        val allChapters = (1..chapterCount).toSet()
        _state.value = _state.value.copy(selectedChapters = current + (bookId to allChapters))
    }

    fun getSelectionsString(): String {
        return _state.value.selectedChapters.flatMap { (bookId, chapters) ->
            chapters.sorted().map { "$bookId:$it" }
        }.joinToString(",")
    }
}
