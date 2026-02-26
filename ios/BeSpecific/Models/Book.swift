import Foundation

enum Testament: String, Codable {
    case old, new
}

struct Book: Identifiable {
    let bookId: Int
    let name: String
    let shortName: String
    let chapters: Int
    let testament: Testament

    var id: Int { bookId }
}
