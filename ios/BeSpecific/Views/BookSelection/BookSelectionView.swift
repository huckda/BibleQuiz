import SwiftUI

struct BookSelectionView: View {
    @Bindable var viewModel: BookSelectionViewModel
    let onBooksSelected: ([Int]) -> Void

    private let columns = [GridItem(.adaptive(minimum: 100), spacing: 8)]

    var body: some View {
        ZStack {
            if viewModel.isLoading {
                ProgressView()
            } else if let error = viewModel.error {
                VStack(spacing: 8) {
                    Text("Failed to load books")
                        .font(AppTypography.titleLarge)
                    Text(error)
                        .font(AppTypography.bodyMedium)
                    Button("Retry") { viewModel.retry() }
                }
            } else {
                ScrollView {
                    LazyVGrid(columns: columns, spacing: 8) {
                        Section {
                            ForEach(viewModel.books.filter { $0.testament == .old }) { book in
                                bookCard(book)
                            }
                        } header: {
                            Text("Old Testament")
                                .font(AppTypography.titleLarge)
                                .frame(maxWidth: .infinity, alignment: .leading)
                                .padding(.vertical, 4)
                        }

                        Section {
                            ForEach(viewModel.books.filter { $0.testament == .new }) { book in
                                bookCard(book)
                            }
                        } header: {
                            Text("New Testament")
                                .font(AppTypography.titleLarge)
                                .frame(maxWidth: .infinity, alignment: .leading)
                                .padding(.vertical, 4)
                        }
                    }
                    .padding(.horizontal, 12)
                    .padding(.top, 12)
                    .padding(.bottom, 80)
                }
            }
        }
        .navigationTitle("Select Books")
        .safeAreaInset(edge: .bottom) {
            if !viewModel.selectedBookIds.isEmpty {
                Button {
                    onBooksSelected(viewModel.selectedBookIds.sorted())
                } label: {
                    Text("Continue (\(viewModel.selectedBookIds.count) selected)")
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
    private func bookCard(_ book: Book) -> some View {
        let isSelected = viewModel.selectedBookIds.contains(book.bookId)
        Button {
            viewModel.toggleBook(book.bookId)
        } label: {
            Text(book.shortName)
                .font(AppTypography.labelLarge)
                .foregroundStyle(isSelected ? .white : .primary)
                .frame(maxWidth: .infinity)
                .padding(.vertical, 12)
                .padding(.horizontal, 8)
                .lineLimit(1)
                .minimumScaleFactor(0.7)
        }
        .background(isSelected ? Color.accentColor : Color(.systemBackground))
        .clipShape(RoundedRectangle(cornerRadius: 8))
        .overlay(
            RoundedRectangle(cornerRadius: 8)
                .stroke(isSelected ? Color.accentColor : Color(.separator), lineWidth: 1)
        )
    }
}
