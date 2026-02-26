import SwiftUI

struct ResultsView: View {
    let score: Int
    let total: Int
    let onRetry: () -> Void
    let onStudy: () -> Void
    let onHome: () -> Void

    private var percentage: Int {
        total == 0 ? 0 : (score * 100) / total
    }

    private var message: String {
        switch percentage {
        case 90...: return "Excellent!"
        case 70...: return "Great job!"
        case 50...: return "Good effort!"
        default: return "Keep studying!"
        }
    }

    private var messageColor: Color {
        switch percentage {
        case 70...: return .correctGreen
        case 50...: return .gold
        default: return .incorrectRed
        }
    }

    var body: some View {
        VStack(spacing: 0) {
            Spacer()

            Text(message)
                .font(AppTypography.headlineLarge)
                .foregroundStyle(messageColor)

            Spacer().frame(height: 16)

            Text("\(score) / \(total)")
                .font(AppTypography.headlineLarge)

            Text("\(percentage)%")
                .font(AppTypography.headlineMedium)
                .foregroundStyle(.accent)

            Spacer().frame(height: 48)

            Button {
                onRetry()
            } label: {
                Text("Try Again")
                    .frame(maxWidth: .infinity)
                    .frame(height: 48)
            }
            .buttonStyle(.borderedProminent)

            Spacer().frame(height: 12)

            Button {
                onStudy()
            } label: {
                Text("Study Verses")
                    .frame(maxWidth: .infinity)
                    .frame(height: 48)
            }
            .buttonStyle(.bordered)

            Spacer().frame(height: 12)

            Button {
                onHome()
            } label: {
                Text("Home")
                    .frame(maxWidth: .infinity)
            }

            Spacer()
        }
        .padding(.horizontal, 32)
        .navigationBarBackButtonHidden(true)
    }
}
