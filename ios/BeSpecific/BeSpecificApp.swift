import SwiftUI
import SwiftData

@main
struct BeSpecificApp: App {
    let modelContainer: ModelContainer

    init() {
        do {
            let schema = Schema([BookEntity.self, VerseEntity.self, ChapterProgressEntity.self])
            let config = ModelConfiguration(schema: schema)
            modelContainer = try ModelContainer(for: schema, configurations: [config])
        } catch {
            fatalError("Failed to create ModelContainer: \(error)")
        }
    }

    var body: some Scene {
        WindowGroup {
            let context = modelContainer.mainContext
            let api = BollsAPIService()
            let bibleRepo = BibleRepository(api: api, modelContext: context)
            let progressRepo = ProgressRepository(modelContext: context)

            ContentView(
                bibleRepository: bibleRepo,
                progressRepository: progressRepo
            )
        }
        .modelContainer(modelContainer)
    }
}
