package com.huck.biblequiz.ui.screens.download

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huck.biblequiz.data.preferences.BibleDownloadPrefs
import com.huck.biblequiz.data.repository.BibleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DownloadState(
    val isAlreadyDownloaded: Boolean = false,
    val isDownloading: Boolean = false,
    val totalChapters: Int = 0,
    val completedChapters: Int = 0,
    val currentBookName: String = "",
    val error: String? = null,
    val isComplete: Boolean = false
) {
    val progress: Float get() =
        if (totalChapters == 0) 0f else completedChapters.toFloat() / totalChapters
}

@HiltViewModel
class DownloadViewModel @Inject constructor(
    private val bibleRepository: BibleRepository,
    private val downloadPrefs: BibleDownloadPrefs
) : ViewModel() {

    private val _state = MutableStateFlow(DownloadState())
    val state: StateFlow<DownloadState> = _state

    init {
        if (downloadPrefs.isFullyDownloaded()) {
            _state.value = DownloadState(isAlreadyDownloaded = true, isComplete = true)
        } else {
            startDownload()
        }
    }

    private fun startDownload() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isDownloading = true, error = null)
            try {
                val books = bibleRepository.getBooks()
                val totalChapters = books.sumOf { it.chapters }
                _state.value = _state.value.copy(totalChapters = totalChapters)

                var completed = 0
                for (book in books) {
                    _state.value = _state.value.copy(currentBookName = book.name)
                    for (chapter in 1..book.chapters) {
                        try {
                            bibleRepository.getChapterVerses(book.bookId, chapter)
                        } catch (_: Exception) {
                            // Skip failed chapter — will be fetched on demand when online
                        }
                        completed++
                        _state.value = _state.value.copy(completedChapters = completed)
                    }
                }

                downloadPrefs.markFullyDownloaded()
                _state.value = _state.value.copy(isComplete = true, isDownloading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "Download failed",
                    isDownloading = false
                )
            }
        }
    }

    fun retry() {
        _state.value = DownloadState()
        startDownload()
    }
}
