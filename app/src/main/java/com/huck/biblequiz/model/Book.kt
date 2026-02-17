package com.huck.biblequiz.model

data class Book(
    val bookId: Int,
    val name: String,
    val shortName: String,
    val chapters: Int,
    val testament: Testament
)

enum class Testament { OLD, NEW }
