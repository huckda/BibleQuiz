package com.huck.biblequiz.ui.screens.bookselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huck.biblequiz.data.repository.BibleRepository
import com.huck.biblequiz.model.Book
import com.huck.biblequiz.model.Testament
import com.huck.biblequiz.util.BookNames
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BookSelectionState(
    val books: List<Book> = emptyList(),
    val selectedBookIds: Set<Int> = emptySet(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class BookSelectionViewModel @Inject constructor(
    private val bibleRepository: BibleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BookSelectionState())
    val state: StateFlow<BookSelectionState> = _state

    init {
        loadBooks()
    }

    private fun loadBooks() {
        viewModelScope.launch {
            try {
                val entities = bibleRepository.getBooks()
                val books = entities.map { entity ->
                    Book(
                        bookId = entity.bookId,
                        name = entity.name,
                        shortName = BookNames.getShortName(entity.bookId),
                        chapters = entity.chapters,
                        testament = if (BookNames.isOldTestament(entity.bookId)) Testament.OLD else Testament.NEW
                    )
                }
                _state.value = _state.value.copy(books = books, isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "Unknown error",
                    isLoading = false
                )
            }
        }
    }

    fun toggleBook(bookId: Int) {
        val current = _state.value.selectedBookIds
        _state.value = _state.value.copy(
            selectedBookIds = if (bookId in current) current - bookId else current + bookId
        )
    }

    fun retry() {
        _state.value = _state.value.copy(isLoading = true, error = null)
        loadBooks()
    }
}
