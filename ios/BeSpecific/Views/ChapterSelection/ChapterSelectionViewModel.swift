import Foundation

struct BookWithChapters: Identifiable {
    let bookId: Int
    let name: String
    let chapterCount: Int
    var studiedChapters: Set<Int> = []
    var quizzedChapters: Set<Int> = []

    var id: Int { bookId }
}

@MainActor @Observable
final class ChapterSelectionViewModel {
    var books: [BookWithChapters] = []
    var selectedChapters: [Int: Set<Int>] = [:]
    var isLoading = true

    private let bibleRepository: BibleRepository
    private let progressRepository: ProgressRepository

    init(bookIds: [Int], bibleRepository: BibleRepository, progressRepository: ProgressRepository) {
        self.bibleRepository = bibleRepository
        self.progressRepository = progressRepository
        Task { await loadBooks(bookIds: bookIds) }
    }

    private func loadBooks(bookIds: [Int]) async {
        do {
            let allBooks = try await bibleRepository.getBooks()
            var result: [BookWithChapters] = []
            for id in bookIds {
                guard let entity = allBooks.first(where: { $0.bookId == id }) else { continue }
                let progress = try progressRepository.getBookProgress(bookId: id)
                result.append(BookWithChapters(
                    bookId: id,
                    name: BookNames.getShortName(id),
                    chapterCount: entity.chapters,
                    studiedChapters: Set(progress.filter { $0.studied }.map { $0.chapter }),
                    quizzedChapters: Set(progress.filter { $0.quizzed }.map { $0.chapter })
                ))
            }
            books = result
            isLoading = false
        } catch {
            isLoading = false
        }
    }

    func toggleChapter(bookId: Int, chapter: Int) {
        var chapters = selectedChapters[bookId] ?? []
        if chapters.contains(chapter) {
            chapters.remove(chapter)
        } else {
            chapters.insert(chapter)
        }
        if chapters.isEmpty {
            selectedChapters.removeValue(forKey: bookId)
        } else {
            selectedChapters[bookId] = chapters
        }
    }

    func selectAllChapters(bookId: Int, chapterCount: Int) {
        selectedChapters[bookId] = Set(1...chapterCount)
    }

    var totalSelected: Int {
        selectedChapters.values.reduce(0) { $0 + $1.count }
    }

    func getSelectionsString() -> String {
        selectedChapters.flatMap { (bookId, chapters) in
            chapters.sorted().map { "\(bookId):\($0)" }
        }.joined(separator: ",")
    }
}
