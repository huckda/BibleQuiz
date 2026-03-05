package com.huck.biblequiz

import android.app.Application
import com.huck.biblequiz.data.preferences.BibleDownloadPrefs
import com.huck.biblequiz.data.repository.BibleRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class BibleQuizApp : Application() {

    @Inject lateinit var bibleRepository: BibleRepository
    @Inject lateinit var downloadPrefs: BibleDownloadPrefs

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        if (!downloadPrefs.isFullyDownloaded()) {
            appScope.launch {
                try {
                    val books = bibleRepository.getBooks()
                    for (book in books) {
                        for (chapter in 1..book.chapters) {
                            try {
                                bibleRepository.getChapterVerses(book.bookId, chapter)
                            } catch (_: Exception) {
                                // Skip failed chapter — will retry on next launch
                            }
                        }
                    }
                    downloadPrefs.markFullyDownloaded()
                } catch (_: Exception) {
                    // Will retry on next launch
                }
            }
        }
    }
}
