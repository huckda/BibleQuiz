package com.huck.biblequiz.model

data class QuizQuestion(
    val verse: Verse,
    val displayText: String,
    val blanks: List<Blank>
)

data class Blank(
    val index: Int,
    val answer: String
)
