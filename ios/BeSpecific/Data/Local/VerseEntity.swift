import Foundation
import SwiftData

@Model
final class VerseEntity {
    @Attribute(.unique) var pk: Int
    var bookId: Int
    var chapter: Int
    var verse: Int
    var text: String

    init(pk: Int, bookId: Int, chapter: Int, verse: Int, text: String) {
        self.pk = pk
        self.bookId = bookId
        self.chapter = chapter
        self.verse = verse
        self.text = text
    }
}
