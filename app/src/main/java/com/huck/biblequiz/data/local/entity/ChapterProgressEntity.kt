package com.huck.biblequiz.data.local.entity

import androidx.room.Entity

@Entity(tableName = "chapter_progress", primaryKeys = ["bookId", "chapter"])
data class ChapterProgressEntity(
    val bookId: Int,
    val chapter: Int,
    val studied: Boolean = false,
    val quizzed: Boolean = false,
    val bestScore: Int = 0,
    val totalQuestions: Int = 0
)
