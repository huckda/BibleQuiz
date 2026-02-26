import Foundation

@MainActor @Observable
final class QuizViewModel {
    var questions: [QuizQuestion] = []
    var currentIndex = 0
    var answers: [Int: [String]] = [:]
    var showResult = false
    var totalCorrect = 0
    var isLoading = true
    var error: String?
    var timeRemaining = 0

    let timerSeconds: Int

    var currentQuestion: QuizQuestion? { questions.indices.contains(currentIndex) ? questions[currentIndex] : nil }
    var progress: Float { questions.isEmpty ? 0 : Float(currentIndex + 1) / Float(questions.count) }
    var isLastQuestion: Bool { currentIndex >= questions.count - 1 }

    private let selections: String
    private let shuffle: Bool
    private let bibleRepository: BibleRepository
    private let progressRepository: ProgressRepository
    private var timerTask: Task<Void, Never>?

    init(selections: String, shuffle: Bool, timerSeconds: Int, bibleRepository: BibleRepository, progressRepository: ProgressRepository) {
        self.selections = selections
        self.shuffle = shuffle
        self.timerSeconds = timerSeconds
        self.bibleRepository = bibleRepository
        self.progressRepository = progressRepository
        Task { await loadQuiz() }
    }

    private func loadQuiz() async {
        do {
            let pairs = selections.split(separator: ",").compactMap { s -> (Int, Int)? in
                let parts = s.split(separator: ":")
                guard parts.count == 2, let b = Int(parts[0]), let c = Int(parts[1]) else { return nil }
                return (b, c)
            }

            var allQuestions: [QuizQuestion] = []
            for (bookId, chapter) in pairs {
                let entities = try await bibleRepository.getChapterVerses(bookId: bookId, chapter: chapter)
                let verses = entities.map { Verse(bookId: $0.bookId, chapter: $0.chapter, verse: $0.verse, text: $0.text) }
                allQuestions.append(contentsOf: QuizGenerator.generate(verses: verses))
            }

            questions = shuffle ? allQuestions.shuffled() : allQuestions
            isLoading = false
            timeRemaining = timerSeconds
            startTimer()
        } catch {
            self.error = error.localizedDescription
            isLoading = false
        }
    }

    private func startTimer() {
        guard timerSeconds > 0 else { return }
        timerTask?.cancel()
        timeRemaining = timerSeconds
        timerTask = Task {
            var remaining = timerSeconds
            while remaining > 0 {
                try? await Task.sleep(for: .seconds(1))
                if Task.isCancelled { return }
                remaining -= 1
                timeRemaining = remaining
            }
            if !showResult {
                let question = currentQuestion
                let emptyAnswers = Array(repeating: "", count: question?.blanks.count ?? 0)
                submitAnswers(emptyAnswers)
            }
        }
    }

    func submitAnswers(_ userAnswers: [String]) {
        guard let question = currentQuestion else { return }
        timerTask?.cancel()

        var correct = totalCorrect
        for (i, blank) in question.blanks.enumerated() {
            let userAnswer = i < userAnswers.count ? userAnswers[i] : ""
            if QuizGenerator.checkAnswer(userAnswer, blank.answer) {
                correct += 1
            }
        }

        answers[currentIndex] = userAnswers
        totalCorrect = correct
        showResult = true
    }

    func nextQuestion() -> Bool {
        if isLastQuestion {
            saveScores()
            return true
        }
        currentIndex += 1
        showResult = false
        startTimer()
        return false
    }

    private func saveScores() {
        let pairs = selections.split(separator: ",").compactMap { s -> (Int, Int)? in
            let parts = s.split(separator: ":")
            guard parts.count == 2, let b = Int(parts[0]), let c = Int(parts[1]) else { return nil }
            return (b, c)
        }
        let totalBlanks = getTotalBlanks()
        Task {
            for (bookId, chapter) in pairs {
                try? progressRepository.saveQuizScore(bookId: bookId, chapter: chapter, score: totalCorrect, total: totalBlanks)
            }
        }
    }

    func getTotalBlanks() -> Int {
        questions.reduce(0) { $0 + $1.blanks.count }
    }
}
