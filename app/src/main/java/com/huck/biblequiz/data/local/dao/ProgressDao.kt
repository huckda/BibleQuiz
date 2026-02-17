package com.huck.biblequiz.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.huck.biblequiz.data.local.entity.ChapterProgressEntity

@Dao
interface ProgressDao {

    @Query("SELECT * FROM chapter_progress WHERE bookId = :bookId AND chapter = :chapter")
    suspend fun getProgress(bookId: Int, chapter: Int): ChapterProgressEntity?

    @Query("SELECT * FROM chapter_progress WHERE bookId = :bookId")
    suspend fun getBookProgress(bookId: Int): List<ChapterProgressEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(progress: ChapterProgressEntity)
}
