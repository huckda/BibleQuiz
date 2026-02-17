package com.huck.biblequiz.di

import android.content.Context
import androidx.room.Room
import com.huck.biblequiz.data.local.BibleQuizDatabase
import com.huck.biblequiz.data.local.dao.BookDao
import com.huck.biblequiz.data.local.dao.ProgressDao
import com.huck.biblequiz.data.local.dao.VerseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): BibleQuizDatabase {
        return Room.databaseBuilder(
            context,
            BibleQuizDatabase::class.java,
            "bible_quiz.db"
        ).build()
    }

    @Provides
    fun provideBookDao(db: BibleQuizDatabase): BookDao = db.bookDao()

    @Provides
    fun provideVerseDao(db: BibleQuizDatabase): VerseDao = db.verseDao()

    @Provides
    fun provideProgressDao(db: BibleQuizDatabase): ProgressDao = db.progressDao()
}
