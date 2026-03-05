import Foundation
import SwiftData

// ── Bundled-asset data model ──────────────────────────────────────────────────

private struct BundledBook: Codable {
    let bookId: Int
    let name: String
    let chapters: Int
}

private struct BundledVerse: Codable {
    let pk: Int
    let bookId: Int
    let chapter: Int
    let verse: Int
    let text: String
}

private struct BundledBible: Codable {
    let books: [BundledBook]
    let verses: [BundledVerse]
}

// ── Repository ────────────────────────────────────────────────────────────────

@MainActor
final class BibleRepository {
    private let api: BollsAPIService
    private let modelContext: ModelContext

    init(api: BollsAPIService, modelContext: ModelContext) {
        self.api = api
        self.modelContext = modelContext
    }

    func getBooks() async throws -> [BookEntity] {
        let cached = try modelContext.fetch(FetchDescriptor<BookEntity>(
            sortBy: [SortDescriptor(\.bookId)]
        ))
        if !cached.isEmpty { return cached }

        // Load from bundled JSON — no network required
        if let loaded = try? loadBundledBible() {
            return loaded
        }

        // Fallback to network if bundle somehow missing
        let remote = try await api.getBooks()
        let entities = remote.map { BookEntity(bookId: $0.bookid, name: $0.name, chapters: $0.chapters) }
        for entity in entities { modelContext.insert(entity) }
        try modelContext.save()
        return entities
    }

    func getChapterVerses(bookId: Int, chapter: Int) async throws -> [VerseEntity] {
        let predicate = #Predicate<VerseEntity> { $0.bookId == bookId && $0.chapter == chapter }
        let descriptor = FetchDescriptor<VerseEntity>(
            predicate: predicate, sortBy: [SortDescriptor(\.verse)]
        )
        let cached = try modelContext.fetch(descriptor)
        if !cached.isEmpty { return cached }

        // Should already be in store from bundle load; fallback to network
        let remote = try await api.getChapter(bookId: bookId, chapter: chapter)
        let entities = remote.map {
            VerseEntity(pk: $0.pk, bookId: bookId, chapter: chapter,
                        verse: $0.verse, text: TextCleaner.clean($0.text))
        }
        for entity in entities { modelContext.insert(entity) }
        try modelContext.save()
        return entities
    }

    // ── Bundle load ───────────────────────────────────────────────────────────

    private func loadBundledBible() throws -> [BookEntity] {
        guard let url = Bundle.main.url(forResource: "bible_nkjv", withExtension: "json") else {
            throw NSError(domain: "BibleRepository", code: 1,
                          userInfo: [NSLocalizedDescriptionKey: "bible_nkjv.json not found in bundle"])
        }

        let data = try Data(contentsOf: url)
        let bible = try JSONDecoder().decode(BundledBible.self, from: data)

        let bookEntities = bible.books.map {
            BookEntity(bookId: $0.bookId, name: $0.name, chapters: $0.chapters)
        }
        for entity in bookEntities { modelContext.insert(entity) }

        // Insert verses grouped by book to keep memory pressure manageable
        let byBook = Dictionary(grouping: bible.verses, by: \.bookId)
        for (_, verses) in byBook {
            for v in verses {
                modelContext.insert(VerseEntity(
                    pk: v.pk, bookId: v.bookId, chapter: v.chapter,
                    verse: v.verse, text: v.text
                ))
            }
        }

        try modelContext.save()
        return bookEntities
    }
}
