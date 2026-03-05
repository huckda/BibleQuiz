import Foundation

enum BibleDownloadPrefs {
    static var isFullyDownloaded: Bool {
        get { UserDefaults.standard.bool(forKey: "bible_downloaded") }
        set { UserDefaults.standard.set(newValue, forKey: "bible_downloaded") }
    }
}
