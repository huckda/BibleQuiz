package com.huck.biblequiz.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "verses")
data class VerseEntity(
    @PrimaryKey val pk: Int,
    val bookId: Int,
    val chapter: Int,
    val verse: Int,
    val text: String
)
