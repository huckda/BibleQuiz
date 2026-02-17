package com.huck.biblequiz.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.huck.biblequiz.data.local.entity.VerseEntity

@Dao
interface VerseDao {

    @Query("SELECT * FROM verses WHERE bookId = :bookId AND chapter = :chapter ORDER BY verse ASC")
    suspend fun getVerses(bookId: Int, chapter: Int): List<VerseEntity>

    @Query("SELECT COUNT(*) FROM verses WHERE bookId = :bookId AND chapter = :chapter")
    suspend fun getVerseCount(bookId: Int, chapter: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerses(verses: List<VerseEntity>)
}
