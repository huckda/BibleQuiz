import SwiftUI

struct ModeSelectionView: View {
    let selections: String
    let onStudy: () -> Void
    let onQuiz: (Bool, Int) -> Void
    let onBack: () -> Void

    @State private var shuffleEnabled = true
    @State private var timedEnabled = false
    @State private var timerSeconds: Double = 30

    private var chapterCount: Int {
        selections.split(separator: ",").count
    }

    var body: some View {
        VStack(spacing: 0) {
            Spacer()

            Text("\(chapterCount) chapter\(chapterCount > 1 ? "s" : "") selected")
                .font(AppTypography.headlineMedium)

            Spacer().frame(height: 48)

            Button {
                onStudy()
            } label: {
                Text("Study")
                    .font(AppTypography.titleLarge)
                    .frame(maxWidth: .infinity)
                    .frame(height: 56)
            }
            .buttonStyle(.borderedProminent)

            Spacer().frame(height: 16)

            Button {
                onQuiz(shuffleEnabled, timedEnabled ? Int(timerSeconds.rounded()) : 0)
            } label: {
                Text("Quiz")
                    .font(AppTypography.titleLarge)
                    .frame(maxWidth: .infinity)
                    .frame(height: 56)
            }
            .buttonStyle(.bordered)

            Spacer().frame(height: 24)

            Toggle("Randomize verse order", isOn: $shuffleEnabled)
                .font(AppTypography.bodyLarge)

            Spacer().frame(height: 8)

            Toggle("Timed mode", isOn: $timedEnabled)
                .font(AppTypography.bodyLarge)

            if timedEnabled {
                Spacer().frame(height: 8)
                Text("\(Int(timerSeconds.rounded())) seconds per question")
                    .font(AppTypography.bodyMedium)
                    .foregroundStyle(.accent)
                Slider(value: $timerSeconds, in: 1...59, step: 1)
            }

            Spacer()
        }
        .padding(.horizontal, 32)
        .navigationTitle("Choose Mode")
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
