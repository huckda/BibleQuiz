package com.huck.biblequiz.data.repository

import com.huck.biblequiz.data.local.dao.ProgressDao
import com.huck.biblequiz.data.local.entity.ChapterProgressEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProgressRepository @Inject constructor(
    private val progressDao: ProgressDao
) {

    suspend fun getProgress(bookId: Int, chapter: Int): ChapterProgressEntity? {
        return progressDao.getProgress(bookId, chapter)
    }

    suspend fun getBookProgress(bookId: Int): List<ChapterProgressEntity> {
        return progressDao.getBookProgress(bookId)
    }

    suspend fun markStudied(bookId: Int, chapter: Int) {
        val existing = progressDao.getProgress(bookId, chapter)
        val updated = existing?.copy(studied = true)
            ?: ChapterProgressEntity(bookId = bookId, chapter = chapter, studied = true)
        progressDao.upsertProgress(updated)
    }

    suspend fun saveQuizScore(bookId: Int, chapter: Int, score: Int, total: Int) {
        val existing = progressDao.getProgress(bookId, chapter)
        val bestScore = maxOf(score, existing?.bestScore ?: 0)
        val updated = existing?.copy(quizzed = true, bestScore = bestScore, totalQuestions = total)
            ?: ChapterProgressEntity(
                bookId = bookId,
                chapter = chapter,
                quizzed = true,
                bestScore = bestScore,
                totalQuestions = total
            )
        progressDao.upsertProgress(updated)
    }
}
