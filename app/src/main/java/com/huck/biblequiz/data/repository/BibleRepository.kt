package com.huck.biblequiz.data.repository

import com.huck.biblequiz.data.local.dao.BookDao
import com.huck.biblequiz.data.local.dao.VerseDao
import com.huck.biblequiz.data.local.entity.BookEntity
import com.huck.biblequiz.data.local.entity.VerseEntity
import com.huck.biblequiz.data.remote.BollsApiService
import com.huck.biblequiz.util.TextCleaner
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BibleRepository @Inject constructor(
    private val api: BollsApiService,
    private val bookDao: BookDao,
    private val verseDao: VerseDao
) {

    suspend fun getBooks(): List<BookEntity> {
        val cached = bookDao.getAllBooks()
        if (cached.isNotEmpty()) return cached

        try {
            val remote = api.getBooks()
            val entities = remote.map { dto ->
                BookEntity(
                    bookId = dto.bookId,
                    name = dto.name,
                    chapters = dto.chapters
                )
            }
            bookDao.insertBooks(entities)
            return entities
        } catch (e: Exception) {
            val fallback = bookDao.getAllBooks()
            if (fallback.isNotEmpty()) return fallback
            throw e
        }
    }

    suspend fun getChapterVerses(bookId: Int, chapter: Int): List<VerseEntity> {
        val cached = verseDao.getVerses(bookId, chapter)
        if (cached.isNotEmpty()) return cached

        try {
            val remote = api.getChapter(bookId, chapter)
            val entities = remote.map { dto ->
                VerseEntity(
                    pk = dto.pk,
                    bookId = bookId,
                    chapter = chapter,
                    verse = dto.verse,
                    text = TextCleaner.clean(dto.text)
                )
            }
            verseDao.insertVerses(entities)
            return entities
        } catch (e: Exception) {
            val fallback = verseDao.getVerses(bookId, chapter)
            if (fallback.isNotEmpty()) return fallback
            throw e
        }
    }
}
