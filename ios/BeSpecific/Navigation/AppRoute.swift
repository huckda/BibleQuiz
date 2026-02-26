import Foundation

enum AppRoute: Hashable {
    case bookSelection
    case chapterSelection(bookIds: [Int])
    case modeSelection(selections: String)
    case study(selections: String)
    case quiz(selections: String, shuffle: Bool, timerSeconds: Int)
    case results(score: Int, total: Int, selections: String)
}
