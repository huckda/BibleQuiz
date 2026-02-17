package com.huck.biblequiz.model

data class QuizResult(
    val bookId: Int,
    val chapter: Int,
    val correct: Int,
    val total: Int
) {
    val percentage: Int get() = if (total == 0) 0 else (correct * 100) / total
}
