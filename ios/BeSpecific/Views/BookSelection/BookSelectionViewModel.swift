import Foundation

@MainActor @Observable
final class BookSelectionViewModel {
    var books: [Book] = []
    var selectedBookIds: Set<Int> = []
    var isLoading = true
    var error: String?

    private let bibleRepository: BibleRepository

    init(bibleRepository: BibleRepository) {
        self.bibleRepository = bibleRepository
        Task { await loadBooks() }
    }

    private func loadBooks() async {
        do {
            let entities = try await bibleRepository.getBooks()
            books = entities.map { entity in
                Book(
                    bookId: entity.bookId,
                    name: entity.name,
                    shortName: BookNames.getShortName(entity.bookId),
                    chapters: entity.chapters,
                    testament: BookNames.isOldTestament(entity.bookId) ? .old : .new
                )
            }
            isLoading = false
        } catch {
            self.error = error.localizedDescription
            isLoading = false
        }
    }

    func toggleBook(_ bookId: Int) {
        if selectedBookIds.contains(bookId) {
            selectedBookIds.remove(bookId)
        } else {
            selectedBookIds.insert(bookId)
        }
    }

    func retry() {
        isLoading = true
        error = nil
        Task { await loadBooks() }
    }
}
