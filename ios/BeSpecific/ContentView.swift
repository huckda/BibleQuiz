import SwiftUI

struct ContentView: View {
    @State private var path = NavigationPath()
    let bibleRepository: BibleRepository
    let progressRepository: ProgressRepository

    var body: some View {
        NavigationStack(path: $path) {
            BookSelectionView(
                viewModel: BookSelectionViewModel(bibleRepository: bibleRepository),
                onBooksSelected: { bookIds in
                    path.append(AppRoute.chapterSelection(bookIds: bookIds))
                }
            )
            .navigationDestination(for: AppRoute.self) { route in
                switch route {
                case .chapterSelection(let bookIds):
                    ChapterSelectionView(
                        viewModel: ChapterSelectionViewModel(
                            bookIds: bookIds,
                            bibleRepository: bibleRepository,
                            progressRepository: progressRepository
                        ),
                        onChaptersSelected: { selections in
                            path.append(AppRoute.modeSelection(selections: selections))
                        },
                        onBack: { path.removeLast() }
                    )

                case .modeSelection(let selections):
                    ModeSelectionView(
                        selections: selections,
                        onStudy: {
                            path.append(AppRoute.study(selections: selections))
                        },
                        onQuiz: { shuffle, timerSeconds in
                            path.append(AppRoute.quiz(selections: selections, shuffle: shuffle, timerSeconds: timerSeconds))
                        },
                        onBack: { path.removeLast() }
                    )

                case .study(let selections):
                    StudyView(
                        viewModel: StudyViewModel(
                            selections: selections,
                            bibleRepository: bibleRepository,
                            progressRepository: progressRepository
                        ),
                        onBack: { path.removeLast() },
                        onStartQuiz: {
                            path.removeLast()
                            path.append(AppRoute.quiz(selections: selections, shuffle: true, timerSeconds: 0))
                        }
                    )

                case .quiz(let selections, let shuffle, let timerSeconds):
                    QuizView(
                        viewModel: QuizViewModel(
                            selections: selections,
                            shuffle: shuffle,
                            timerSeconds: timerSeconds,
                            bibleRepository: bibleRepository,
                            progressRepository: progressRepository
                        ),
                        onFinished: { score, total in
                            path.append(AppRoute.results(score: score, total: total, selections: selections))
                        },
                        onBack: { path.removeLast() }
                    )

                case .results(let score, let total, let selections):
                    ResultsView(
                        score: score,
                        total: total,
                        onRetry: {
                            path.removeLast()
                            path.append(AppRoute.quiz(selections: selections, shuffle: true, timerSeconds: 0))
                        },
                        onStudy: {
                            path.removeLast()
                            path.append(AppRoute.study(selections: selections))
                        },
                        onHome: {
                            path = NavigationPath()
                        }
                    )

                case .bookSelection:
                    EmptyView()
                }
            }
        }
    }
}
