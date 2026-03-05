package com.huck.biblequiz.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.huck.biblequiz.data.local.dao.BookDao
import com.huck.biblequiz.data.local.dao.VerseDao
import com.huck.biblequiz.data.local.entity.BookEntity
import com.huck.biblequiz.data.local.entity.VerseEntity
import com.huck.biblequiz.data.remote.BollsApiService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.zip.GZIPInputStream
import javax.inject.Inject
import javax.inject.Singleton

// ── Bundled-asset data model ──────────────────────────────────────────────────

private data class BundledBook(
    val bookId: Int,
    val name: String,
    val chapters: Int
)

private data class BundledVerse(
    val pk: Int,
    val bookId: Int,
    val chapter: Int,
    val verse: Int,
    val text: String
)

private data class BundledBible(
    val books: List<BundledBook>,
    val verses: List<BundledVerse>
)

// ── Repository ────────────────────────────────────────────────────────────────

@Singleton
class BibleRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: BollsApiService,
    private val bookDao: BookDao,
    private val verseDao: VerseDao
) {

    suspend fun getBooks(): List<BookEntity> {
        val cached = bookDao.getAllBooks()
        if (cached.isNotEmpty()) return cached

        // Load from bundled asset (no network required)
        loadBundledBible()
        val afterLoad = bookDao.getAllBooks()
        if (afterLoad.isNotEmpty()) return afterLoad

        // Fallback to network if asset somehow fails
        val remote = api.getBooks()
        val entities = remote.map { BookEntity(it.bookId, it.name, it.chapters) }
        bookDao.insertBooks(entities)
        return entities
    }

    suspend fun getChapterVerses(bookId: Int, chapter: Int): List<VerseEntity> {
        val cached = verseDao.getVerses(bookId, chapter)
        if (cached.isNotEmpty()) return cached

        // Data should already be loaded from bundle; fallback to network
        val remote = api.getChapter(bookId, chapter)
        val entities = remote.map {
            VerseEntity(pk = it.pk, bookId = bookId, chapter = chapter,
                        verse = it.verse, text = it.text)
        }
        verseDao.insertVerses(entities)
        return entities
    }

    private suspend fun loadBundledBible() = withContext(Dispatchers.IO) {
        try {
            val json = context.assets.open("bible_nkjv.json.gz").use { raw ->
                BufferedReader(InputStreamReader(GZIPInputStream(raw))).readText()
            }
            val bible = Gson().fromJson(json, BundledBible::class.java)

            val bookEntities = bible.books.map { BookEntity(it.bookId, it.name, it.chapters) }
            bookDao.insertBooks(bookEntities)

            // Insert verses in per-book batches to keep memory use bounded
            val byBook = bible.verses.groupBy { it.bookId }
            for ((_, verses) in byBook) {
                val entities = verses.map {
                    VerseEntity(pk = it.pk, bookId = it.bookId, chapter = it.chapter,
                                verse = it.verse, text = it.text)
                }
                verseDao.insertVerses(entities)
            }
        } catch (_: Exception) {
            // Asset load failed — network fallback will handle it
        }
    }
}
