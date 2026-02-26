import SwiftUI

struct QuizView: View {
    @Bindable var viewModel: QuizViewModel
    let onFinished: (Int, Int) -> Void
    let onBack: () -> Void

    @State private var userAnswers: [String] = []

    var body: some View {
        Group {
            if viewModel.isLoading {
                ProgressView()
            } else if let error = viewModel.error {
                Text("Error: \(error)")
            } else if viewModel.questions.isEmpty {
                Text("No questions available")
            } else if let question = viewModel.currentQuestion {
                quizContent(question)
            }
        }
        .navigationTitle("Quiz")
        .navigationBarBackButtonHidden(true)
        .toolbar {
            ToolbarItem(placement: .navigationBarLeading) {
                Button(action: onBack) {
                    Image(systemName: "chevron.left")
                }
            }
            if viewModel.timerSeconds > 0, !viewModel.isLoading, !viewModel.showResult, !viewModel.questions.isEmpty {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Text("\(viewModel.timeRemaining)s")
                        .font(AppTypography.titleLarge)
                        .foregroundStyle(viewModel.timeRemaining <= 5 ? Color.incorrectRed : .primary)
                }
            }
        }
        .onChange(of: viewModel.currentIndex) {
            userAnswers = Array(repeating: "", count: viewModel.currentQuestion?.blanks.count ?? 0)
        }
        .onAppear {
            userAnswers = Array(repeating: "", count: viewModel.currentQuestion?.blanks.count ?? 0)
        }
    }

    @ViewBuilder
    private func quizContent(_ question: QuizQuestion) -> some View {
        VStack(spacing: 0) {
            ProgressView(value: viewModel.progress)
                .progressViewStyle(.linear)

            HStack {
                Text("Question \(viewModel.currentIndex + 1) of \(viewModel.questions.count)")
                    .font(AppTypography.labelLarge)

                if viewModel.timerSeconds > 0, !viewModel.showResult {
                    Spacer()
                    let fraction = Float(viewModel.timeRemaining) / Float(viewModel.timerSeconds)
                    ProgressView(value: fraction)
                        .progressViewStyle(.linear)
                        .tint(viewModel.timeRemaining <= 5 ? Color.incorrectRed : .accentColor)
                }
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 8)

            ScrollView {
                VStack(alignment: .leading, spacing: 0) {
                    Text("\(BookNames.getShortName(question.verse.bookId)) \(question.verse.chapter):\(question.verse.verse)")
                        .font(AppTypography.labelLarge)
                        .foregroundStyle(.accent)

                    Spacer().frame(height: 8)

                    Text(question.displayText)
                        .font(AppTypography.bodyLarge)

                    Spacer().frame(height: 24)

                    ForEach(Array(question.blanks.enumerated()), id: \.offset) { i, blank in
                        if viewModel.showResult {
                            let userAnswer = i < userAnswers.count ? userAnswers[i] : ""
                            let isCorrect = QuizGenerator.checkAnswer(userAnswer, blank.answer)
                            HStack(spacing: 8) {
                                Text(isCorrect ? "Correct!" : "Wrong")
                                    .font(AppTypography.labelLarge)
                                    .foregroundStyle(isCorrect ? Color.correctGreen : Color.incorrectRed)
                                if !isCorrect {
                                    Text("Your answer: \"\(userAnswer)\" | Correct: \"\(blank.answer)\"")
                                        .font(AppTypography.bodyMedium)
                                }
                            }
                            .padding(.vertical, 4)
                        } else {
                            TextField("Blank \(i + 1)", text: binding(for: i))
                                .textFieldStyle(.roundedBorder)
                                .padding(.vertical, 4)
                        }
                    }
                }
                .padding(16)
            }

            HStack {
                Spacer()
                if viewModel.showResult {
                    Button(viewModel.isLastQuestion ? "See Results" : "Next") {
                        let finished = viewModel.nextQuestion()
                        if finished {
                            onFinished(viewModel.totalCorrect, viewModel.getTotalBlanks())
                        }
                    }
                    .buttonStyle(.borderedProminent)
                    .controlSize(.large)
                } else {
                    Button("Submit") {
                        viewModel.submitAnswers(userAnswers)
                    }
                    .buttonStyle(.borderedProminent)
                    .controlSize(.large)
                    .disabled(userAnswers.allSatisfy { $0.trimmingCharacters(in: .whitespaces).isEmpty })
                }
            }
            .padding(16)
        }
    }

    private func binding(for index: Int) -> Binding<String> {
        Binding(
            get: { index < userAnswers.count ? userAnswers[index] : "" },
            set: { newValue in
                while userAnswers.count <= index { userAnswers.append("") }
                userAnswers[index] = newValue
            }
        )
    }
}
