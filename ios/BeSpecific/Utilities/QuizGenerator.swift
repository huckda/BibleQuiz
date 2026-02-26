import Foundation

enum QuizGenerator {
    private static let stopWords: Set<String> = [
        "the", "and", "that", "this", "with", "from", "your", "have", "has",
        "his", "her", "they", "them", "their", "been", "were", "was", "are",
        "but", "not", "you", "all", "can", "had", "who", "will", "for",
        "shall", "unto", "upon", "said", "also", "into", "than", "then",
        "when", "which", "whom", "what", "there", "would", "could", "should"
    ]

    static func generate(verses: [Verse]) -> [QuizQuestion] {
        verses.compactMap { generateQuestion($0) }
    }

    private static func generateQuestion(_ verse: Verse) -> QuizQuestion? {
        let words = verse.text.components(separatedBy: .whitespaces).filter { !$0.isEmpty }
        guard words.count >= 3 else { return nil }

        let blankCount: Int
        switch words.count {
        case ..<8: blankCount = 1
        case 8...15: blankCount = Int.random(in: 1...2)
        default: blankCount = Int.random(in: 2...3)
        }

        let candidates = words.indices.filter { i in
            let word = stripPunctuation(words[i])
            return word.count >= 4 && !stopWords.contains(word.lowercased())
        }

        guard !candidates.isEmpty else { return nil }

        let scored = candidates.sorted { a, b in
            let wordA = stripPunctuation(words[a])
            let wordB = stripPunctuation(words[b])
            var scoreA = wordA.count
            var scoreB = wordB.count
            if a > 0, let first = wordA.first, first.isUppercase { scoreA += 3 }
            if b > 0, let first = wordB.first, first.isUppercase { scoreB += 3 }
            return scoreA > scoreB
        }

        let selected = Array(scored.prefix(blankCount * 2))
            .shuffled()
            .prefix(blankCount)
            .sorted()

        let blanks = selected.map { i in
            Blank(index: i, answer: stripPunctuation(words[i]))
        }

        var displayWords = words
        for blank in blanks {
            let original = displayWords[blank.index]
            let trailingPunct = String(original.reversed().prefix(while: { !$0.isLetter && !$0.isNumber }).reversed())
            displayWords[blank.index] = "______\(trailingPunct)"
        }

        return QuizQuestion(
            verse: verse,
            displayText: displayWords.joined(separator: " "),
            blanks: blanks
        )
    }

    static func checkAnswer(_ userAnswer: String, _ correctAnswer: String) -> Bool {
        removePunctuation(userAnswer.trimmingCharacters(in: .whitespaces))
            .caseInsensitiveCompare(
                removePunctuation(correctAnswer.trimmingCharacters(in: .whitespaces))
            ) == .orderedSame
    }

    private static func stripPunctuation(_ word: String) -> String {
        var result = word
        while let last = result.last, !last.isLetter, !last.isNumber {
            result.removeLast()
        }
        while let first = result.first, !first.isLetter, !first.isNumber {
            result.removeFirst()
        }
        return result
    }

    private static func removePunctuation(_ text: String) -> String {
        let cleaned = text.unicodeScalars.filter {
            CharacterSet.letters.contains($0) || CharacterSet.decimalDigits.contains($0) || CharacterSet.whitespaces.contains($0)
        }
        let result = String(String.UnicodeScalarView(cleaned))
        return result.components(separatedBy: .whitespaces)
            .filter { !$0.isEmpty }
            .joined(separator: " ")
            .trimmingCharacters(in: .whitespaces)
    }
}
