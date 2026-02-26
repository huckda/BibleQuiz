import SwiftUI

struct ChapterSelectionView: View {
    @Bindable var viewModel: ChapterSelectionViewModel
    let onChaptersSelected: (String) -> Void
    let onBack: () -> Void

    var body: some View {
        Group {
            if viewModel.isLoading {
                ProgressView()
            } else {
                ScrollView {
                    VStack(alignment: .leading, spacing: 16) {
                        ForEach(viewModel.books) { book in
                            bookSection(book)
                        }
                    }
                    .padding(.horizontal, 16)
                    .padding(.bottom, 80)
                }
            }
        }
        .navigationTitle("Select Chapters")
        .navigationBarBackButtonHidden(true)
        .toolbar {
            ToolbarItem(placement: .navigationBarLeading) {
                Button(action: onBack) {
                    Image(systemName: "chevron.left")
                }
            }
        }
        .safeAreaInset(edge: .bottom) {
            if viewModel.totalSelected > 0 {
                Button {
                    onChaptersSelected(viewModel.getSelectionsString())
                } label: {
                    Text("Continue (\(viewModel.totalSelected) chapters)")
                        .frame(maxWidth: .infinity)
                }
                .buttonStyle(.borderedProminent)
                .controlSize(.large)
                .padding()
                .background(.bar)
            }
        }
    }

    @ViewBuilder
    private func bookSection(_ book: BookWithChapters) -> some View {
        let selectedForBook = viewModel.selectedChapters[book.bookId] ?? []

        VStack(alignment: .leading, spacing: 6) {
            HStack {
                Text(book.name)
                    .font(AppTypography.titleLarge)
                Spacer()
                Button("Select All") {
                    viewModel.selectAllChapters(bookId: book.bookId, chapterCount: book.chapterCount)
                }
            }

            let columns = [GridItem(.adaptive(minimum: 44), spacing: 6)]
            LazyVGrid(columns: columns, spacing: 6) {
                ForEach(1...book.chapterCount, id: \.self) { ch in
                    let isSelected = selectedForBook.contains(ch)
                    let isStudied = book.studiedChapters.contains(ch)
                    let isQuizzed = book.quizzedChapters.contains(ch)

                    Button {
                        viewModel.toggleChapter(bookId: book.bookId, chapter: ch)
                    } label: {
                        Text("\(ch)")
                            .font(AppTypography.labelLarge)
                            .foregroundStyle(isSelected ? .white : .primary)
                            .frame(width: 44, height: 44)
                    }
                    .background(chapterColor(isSelected: isSelected, isStudied: isStudied, isQuizzed: isQuizzed))
                    .clipShape(RoundedRectangle(cornerRadius: 6))
                    .overlay(
                        RoundedRectangle(cornerRadius: 6)
                            .stroke(isSelected ? Color.accentColor : Color(.separator), lineWidth: 1)
                    )
                }
            }
        }
    }

    private func chapterColor(isSelected: Bool, isStudied: Bool, isQuizzed: Bool) -> Color {
        if isSelected { return .accentColor }
        if isQuizzed { return .accentColor.opacity(0.3) }
        if isStudied { return Color(.systemGray5) }
        return Color(.systemBackground)
    }
}
