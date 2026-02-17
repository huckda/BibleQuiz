package com.huck.biblequiz.util

import com.huck.biblequiz.model.Blank
import com.huck.biblequiz.model.QuizQuestion
import com.huck.biblequiz.model.Verse

object QuizGenerator {

    private val stopWords = setOf(
        "the", "and", "that", "this", "with", "from", "your", "have", "has",
        "his", "her", "they", "them", "their", "been", "were", "was", "are",
        "but", "not", "you", "all", "can", "had", "who", "will", "for",
        "shall", "unto", "upon", "said", "also", "into", "than", "then",
        "when", "which", "whom", "what", "there", "would", "could", "should"
    )

    fun generate(verses: List<Verse>): List<QuizQuestion> {
        return verses.mapNotNull { verse ->
            generateQuestion(verse)
        }
    }

    private fun generateQuestion(verse: Verse): QuizQuestion? {
        val words = verse.text.split("\\s+".toRegex()).filter { it.isNotBlank() }
        if (words.size < 3) return null

        val blankCount = when {
            words.size < 8 -> 1
            words.size <= 15 -> (1..2).random()
            else -> (2..3).random()
        }

        val candidates = words.indices.filter { i ->
            val word = stripPunctuation(words[i])
            word.length >= 4 && word.lowercase() !in stopWords
        }

        if (candidates.isEmpty()) return null

        val scored = candidates.sortedByDescending { i ->
            val word = stripPunctuation(words[i])
            var score = word.length
            if (i > 0 && word[0].isUpperCase()) score += 3
            score
        }

        val selected = scored.take(blankCount * 2)
            .shuffled()
            .take(blankCount)
            .sorted()

        val blanks = selected.map { i ->
            Blank(index = i, answer = stripPunctuation(words[i]))
        }

        val displayWords = words.toMutableList()
        for (blank in blanks) {
            val original = displayWords[blank.index]
            val trailingPunct = original.takeLastWhile { !it.isLetterOrDigit() }
            displayWords[blank.index] = "______$trailingPunct"
        }

        return QuizQuestion(
            verse = verse,
            displayText = displayWords.joinToString(" "),
            blanks = blanks
        )
    }

    fun checkAnswer(userAnswer: String, correctAnswer: String): Boolean {
        return stripPunctuation(userAnswer.trim()).equals(
            stripPunctuation(correctAnswer.trim()),
            ignoreCase = true
        )
    }

    private fun stripPunctuation(word: String): String {
        return word.trimEnd { !it.isLetterOrDigit() }.trimStart { !it.isLetterOrDigit() }
    }
}
