import Foundation
import SwiftData

@Model
final class BookEntity {
    @Attribute(.unique) var bookId: Int
    var name: String
    var chapters: Int

    init(bookId: Int, name: String, chapters: Int) {
        self.bookId = bookId
        self.name = name
        self.chapters = chapters
    }
}
