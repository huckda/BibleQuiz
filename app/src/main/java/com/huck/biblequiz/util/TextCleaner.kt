package com.huck.biblequiz.util

object TextCleaner {

    private val htmlTagRegex = Regex("<[^>]*>")

    fun clean(html: String): String {
        return html
            .replace(htmlTagRegex, "")
            .replace("&nbsp;", " ")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .trim()
    }
}
