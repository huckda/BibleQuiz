import Foundation
import SwiftData

@MainActor
final class ProgressRepository {
    private let modelContext: ModelContext

    init(modelContext: ModelContext) {
        self.modelContext = modelContext
    }

    func getProgress(bookId: Int, chapter: Int) throws -> ChapterProgressEntity? {
        let predicate = #Predicate<ChapterProgressEntity> { $0.bookId == bookId && $0.chapter == chapter }
        let descriptor = FetchDescriptor<ChapterProgressEntity>(predicate: predicate)
        return try modelContext.fetch(descriptor).first
    }

    func getBookProgress(bookId: Int) throws -> [ChapterProgressEntity] {
        let predicate = #Predicate<ChapterProgressEntity> { $0.bookId == bookId }
        let descriptor = FetchDescriptor<ChapterProgressEntity>(predicate: predicate)
        return try modelContext.fetch(descriptor)
    }

    func markStudied(bookId: Int, chapter: Int) throws {
        if let existing = try getProgress(bookId: bookId, chapter: chapter) {
            existing.studied = true
        } else {
            let entity = ChapterProgressEntity(bookId: bookId, chapter: chapter, studied: true)
            modelContext.insert(entity)
        }
        try modelContext.save()
    }

    func saveQuizScore(bookId: Int, chapter: Int, score: Int, total: Int) throws {
        let bestScore = max(score, try getProgress(bookId: bookId, chapter: chapter)?.bestScore ?? 0)
        if let existing = try getProgress(bookId: bookId, chapter: chapter) {
            existing.quizzed = true
            existing.bestScore = bestScore
            existing.totalQuestions = total
        } else {
            let entity = ChapterProgressEntity(
                bookId: bookId, chapter: chapter,
                quizzed: true, bestScore: bestScore, totalQuestions: total
            )
            modelContext.insert(entity)
        }
        try modelContext.save()
    }
}
