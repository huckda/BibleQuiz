package com.huck.biblequiz.data.preferences

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BibleDownloadPrefs @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences("download_prefs", Context.MODE_PRIVATE)

    fun isFullyDownloaded(): Boolean = prefs.getBoolean("bible_downloaded", false)

    fun markFullyDownloaded() = prefs.edit().putBoolean("bible_downloaded", true).apply()

    fun reset() = prefs.edit().remove("bible_downloaded").apply()
}
