package com.huck.biblequiz.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.huck.biblequiz.data.local.entity.BookEntity

@Dao
interface BookDao {

    @Query("SELECT * FROM books ORDER BY bookId ASC")
    suspend fun getAllBooks(): List<BookEntity>

    @Query("SELECT COUNT(*) FROM books")
    suspend fun getBookCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BookEntity>)
}
