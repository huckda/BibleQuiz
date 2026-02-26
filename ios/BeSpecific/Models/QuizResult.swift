import Foundation

struct QuizResult {
    let bookId: Int
    let chapter: Int
    let correct: Int
    let total: Int

    var percentage: Int {
        total == 0 ? 0 : (correct * 100) / total
    }
}
