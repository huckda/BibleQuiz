package com.huck.biblequiz.data.remote

import com.huck.biblequiz.data.remote.dto.BookDto
import com.huck.biblequiz.data.remote.dto.VerseDto
import retrofit2.http.GET
import retrofit2.http.Path

interface BollsApiService {

    @GET("get-books/NKJV/")
    suspend fun getBooks(): List<BookDto>

    @GET("get-text/NKJV/{bookId}/{chapter}/")
    suspend fun getChapter(
        @Path("bookId") bookId: Int,
        @Path("chapter") chapter: Int
    ): List<VerseDto>
}
