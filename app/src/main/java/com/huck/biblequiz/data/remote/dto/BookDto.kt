package com.huck.biblequiz.data.remote.dto

import com.google.gson.annotations.SerializedName

data class BookDto(
    @SerializedName("bookid") val bookId: Int,
    @SerializedName("name") val name: String,
    @SerializedName("chronorder") val chronOrder: Int,
    @SerializedName("chapters") val chapters: Int
)
