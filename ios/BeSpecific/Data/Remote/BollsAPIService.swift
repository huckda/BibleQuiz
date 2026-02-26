import Foundation

actor BollsAPIService {
    private let baseURL = "https://bolls.life/"
    private let session: URLSession

    init(session: URLSession = .shared) {
        self.session = session
    }

    func getBooks() async throws -> [BookDTO] {
        let url = URL(string: "\(baseURL)get-books/NKJV/")!
        let (data, _) = try await session.data(from: url)
        return try JSONDecoder().decode([BookDTO].self, from: data)
    }

    func getChapter(bookId: Int, chapter: Int) async throws -> [VerseDTO] {
        let url = URL(string: "\(baseURL)get-text/NKJV/\(bookId)/\(chapter)/")!
        let (data, _) = try await session.data(from: url)
        return try JSONDecoder().decode([VerseDTO].self, from: data)
    }
}
