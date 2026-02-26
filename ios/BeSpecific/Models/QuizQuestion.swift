import Foundation

struct QuizQuestion {
    let verse: Verse
    let displayText: String
    let blanks: [Blank]
}

struct Blank {
    let index: Int
    let answer: String
}
