package com.huck.biblequiz.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey val bookId: Int,
    val name: String,
    val chapters: Int
)
