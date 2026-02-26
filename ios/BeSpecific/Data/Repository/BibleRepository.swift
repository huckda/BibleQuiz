import Foundation
import SwiftData

@MainActor
final class BibleRepository {
    private let api: BollsAPIService
    private let modelContext: ModelContext

    init(api: BollsAPIService, modelContext: ModelContext) {
        self.api = api
        self.modelContext = modelContext
    }

    func getBooks() async throws -> [BookEntity] {
        let cached = try modelContext.fetch(FetchDescriptor<BookEntity>(sortBy: [SortDescriptor(\.bookId)]))
        if !cached.isEmpty { return cached }

        do {
            let remote = try await api.getBooks()
            let entities = remote.map { dto in
                BookEntity(bookId: dto.bookid, name: dto.name, chapters: dto.chapters)
            }
            for entity in entities {
                modelContext.insert(entity)
            }
            try modelContext.save()
            return entities
        } catch {
            let fallback = try modelContext.fetch(FetchDescriptor<BookEntity>(sortBy: [SortDescriptor(\.bookId)]))
            if !fallback.isEmpty { return fallback }
            throw error
        }
    }

    func getChapterVerses(bookId: Int, chapter: Int) async throws -> [VerseEntity] {
        let predicate = #Predicate<VerseEntity> { $0.bookId == bookId && $0.chapter == chapter }
        let descriptor = FetchDescriptor<VerseEntity>(predicate: predicate, sortBy: [SortDescriptor(\.verse)])
        let cached = try modelContext.fetch(descriptor)
        if !cached.isEmpty { return cached }

        do {
            let remote = try await api.getChapter(bookId: bookId, chapter: chapter)
            let entities = remote.map { dto in
                VerseEntity(
                    pk: dto.pk,
                    bookId: bookId,
                    chapter: chapter,
                    verse: dto.verse,
                    text: TextCleaner.clean(dto.text)
                )
            }
            for entity in entities {
                modelContext.insert(entity)
            }
            try modelContext.save()
            return entities
        } catch {
            let fallback = try modelContext.fetch(descriptor)
            if !fallback.isEmpty { return fallback }
            throw error
        }
    }
}
