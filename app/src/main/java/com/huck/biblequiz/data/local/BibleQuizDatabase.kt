package com.huck.biblequiz.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.huck.biblequiz.data.local.dao.BookDao
import com.huck.biblequiz.data.local.dao.ProgressDao
import com.huck.biblequiz.data.local.dao.VerseDao
import com.huck.biblequiz.data.local.entity.BookEntity
import com.huck.biblequiz.data.local.entity.ChapterProgressEntity
import com.huck.biblequiz.data.local.entity.VerseEntity

@Database(
    entities = [BookEntity::class, VerseEntity::class, ChapterProgressEntity::class],
    version = 1,
    exportSchema = false
)
abstract class BibleQuizDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun verseDao(): VerseDao
    abstract fun progressDao(): ProgressDao
}
