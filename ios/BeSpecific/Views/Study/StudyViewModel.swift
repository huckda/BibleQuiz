import Foundation

struct ChapterContent {
    let bookId: Int
    let chapter: Int
    let bookName: String
    let verses: [Verse]
}

@MainActor @Observable
final class StudyViewModel {
    var chapters: [ChapterContent] = []
    var currentIndex = 0
    var isLoading = true
    var error: String?

    var currentChapter: ChapterContent? { chapters.indices.contains(currentIndex) ? chapters[currentIndex] : nil }
    var hasPrev: Bool { currentIndex > 0 }
    var hasNext: Bool { currentIndex < chapters.count - 1 }

    private let bibleRepository: BibleRepository
    private let progressRepository: ProgressRepository

    init(selections: String, bibleRepository: BibleRepository, progressRepository: ProgressRepository) {
        self.bibleRepository = bibleRepository
        self.progressRepository = progressRepository
        Task { await loadChapters(selections: selections) }
    }

    private func loadChapters(selections: String) async {
        do {
            let pairs = selections.split(separator: ",").compactMap { s -> (Int, Int)? in
                let parts = s.split(separator: ":")
                guard parts.count == 2, let b = Int(parts[0]), let c = Int(parts[1]) else { return nil }
                return (b, c)
            }

            var result: [ChapterContent] = []
            for (bookId, chapter) in pairs {
                let entities = try await bibleRepository.getChapterVerses(bookId: bookId, chapter: chapter)
                try progressRepository.markStudied(bookId: bookId, chapter: chapter)
                result.append(ChapterContent(
                    bookId: bookId,
                    chapter: chapter,
                    bookName: BookNames.getShortName(bookId),
                    verses: entities.map { Verse(bookId: $0.bookId, chapter: $0.chapter, verse: $0.verse, text: $0.text) }
                ))
            }
            chapters = result
            isLoading = false
        } catch {
            self.error = error.localizedDescription
            isLoading = false
        }
    }

    func nextChapter() {
        if hasNext { currentIndex += 1 }
    }

    func prevChapter() {
        if hasPrev { currentIndex -= 1 }
    }
}
