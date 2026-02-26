import SwiftUI

struct StudyView: View {
    @Bindable var viewModel: StudyViewModel
    let onBack: () -> Void
    let onStartQuiz: () -> Void

    var body: some View {
        Group {
            if viewModel.isLoading {
                ProgressView()
            } else if let error = viewModel.error {
                Text("Error: \(error)")
            } else if let chapter = viewModel.currentChapter {
                VStack(spacing: 0) {
                    ScrollView {
                        VStack(alignment: .leading, spacing: 4) {
                            ForEach(chapter.verses, id: \.verse) { verse in
                                HStack(alignment: .top, spacing: 8) {
                                    Text("\(verse.verse)")
                                        .font(AppTypography.labelLarge)
                                        .foregroundStyle(.accent)
                                        .padding(.top, 2)
                                    Text(verse.text)
                                        .font(AppTypography.bodyLarge)
                                }
                                .padding(.vertical, 4)
                            }
                        }
                        .padding(16)
                    }

                    HStack(spacing: 8) {
                        if viewModel.hasPrev {
                            Button("Previous") { viewModel.prevChapter() }
                                .buttonStyle(.bordered)
                                .frame(maxWidth: .infinity)
                        }
                        if viewModel.hasNext {
                            Button("Next Chapter") { viewModel.nextChapter() }
                                .buttonStyle(.borderedProminent)
                                .frame(maxWidth: .infinity)
                        } else {
                            Button("Start Quiz") { onStartQuiz() }
                                .buttonStyle(.borderedProminent)
                                .frame(maxWidth: .infinity)
                        }
                    }
                    .controlSize(.large)
                    .padding(16)
                }
            }
        }
        .navigationTitle(viewModel.currentChapter.map { "\($0.bookName) \($0.chapter)" } ?? "Study")
        .navigationBarBackButtonHidden(true)
        .toolbar {
            ToolbarItem(placement: .navigationBarLeading) {
                Button(action: onBack) {
                    Image(systemName: "chevron.left")
                }
            }
        }
    }
}
