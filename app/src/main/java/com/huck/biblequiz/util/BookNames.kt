package com.huck.biblequiz.util

object BookNames {

    private val shortNames = mapOf(
        1 to "Genesis", 2 to "Exodus", 3 to "Leviticus", 4 to "Numbers",
        5 to "Deuteronomy", 6 to "Joshua", 7 to "Judges", 8 to "Ruth",
        9 to "1 Samuel", 10 to "2 Samuel", 11 to "1 Kings", 12 to "2 Kings",
        13 to "1 Chronicles", 14 to "2 Chronicles", 15 to "Ezra",
        16 to "Nehemiah", 17 to "Esther", 18 to "Job", 19 to "Psalms",
        20 to "Proverbs", 21 to "Ecclesiastes", 22 to "Song of Solomon",
        23 to "Isaiah", 24 to "Jeremiah", 25 to "Lamentations",
        26 to "Ezekiel", 27 to "Daniel", 28 to "Hosea", 29 to "Joel",
        30 to "Amos", 31 to "Obadiah", 32 to "Jonah", 33 to "Micah",
        34 to "Nahum", 35 to "Habakkuk", 36 to "Zephaniah", 37 to "Haggai",
        38 to "Zechariah", 39 to "Malachi",
        40 to "Matthew", 41 to "Mark", 42 to "Luke", 43 to "John",
        44 to "Acts", 45 to "Romans", 46 to "1 Corinthians",
        47 to "2 Corinthians", 48 to "Galatians", 49 to "Ephesians",
        50 to "Philippians", 51 to "Colossians",
        52 to "1 Thessalonians", 53 to "2 Thessalonians",
        54 to "1 Timothy", 55 to "2 Timothy", 56 to "Titus",
        57 to "Philemon", 58 to "Hebrews", 59 to "James",
        60 to "1 Peter", 61 to "2 Peter", 62 to "1 John",
        63 to "2 John", 64 to "3 John", 65 to "Jude", 66 to "Revelation"
    )

    fun getShortName(bookId: Int): String = shortNames[bookId] ?: "Book $bookId"

    fun isOldTestament(bookId: Int): Boolean = bookId in 1..39
}
