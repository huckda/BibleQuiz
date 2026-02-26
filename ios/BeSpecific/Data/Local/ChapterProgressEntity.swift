import Foundation
import SwiftData

@Model
final class ChapterProgressEntity {
    var bookId: Int
    var chapter: Int
    var studied: Bool
    var quizzed: Bool
    var bestScore: Int
    var totalQuestions: Int

    init(bookId: Int, chapter: Int, studied: Bool = false, quizzed: Bool = false, bestScore: Int = 0, totalQuestions: Int = 0) {
        self.bookId = bookId
        self.chapter = chapter
        self.studied = studied
        self.quizzed = quizzed
        self.bestScore = bestScore
        self.totalQuestions = totalQuestions
    }
}
