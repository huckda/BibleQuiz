import SwiftUI

final class ThemeSettings: ObservableObject {
    @Published var background: Color  { didSet { save("background",    background)  } }
    @Published var primary: Color     { didSet { save("primary",       primary)     } }
    @Published var onPrimary: Color   { didSet { save("onPrimary",     onPrimary)   } }
    @Published var onBackground: Color{ didSet { save("onBackground",  onBackground)} }
    @Published var outline: Color     { didSet { save("outline",       outline)     } }

    init() {
        background   = Self.load("background")   ?? .cream
        primary      = Self.load("primary")      ?? .navy
        onPrimary    = Self.load("onPrimary")    ?? .lightText
        onBackground = Self.load("onBackground") ?? .darkText
        outline      = Self.load("outline")      ?? .navy
    }

    func reset() {
        background   = .cream
        primary      = .navy
        onPrimary    = .lightText
        onBackground = .darkText
        outline      = .navy
    }

    private func save(_ key: String, _ color: Color) {
        var r: CGFloat = 0, g: CGFloat = 0, b: CGFloat = 0, a: CGFloat = 0
        UIColor(color).getRed(&r, green: &g, blue: &b, alpha: &a)
        UserDefaults.standard.set([Double(r), Double(g), Double(b), Double(a)],
                                  forKey: "theme_\(key)")
    }

    private static func load(_ key: String) -> Color? {
        guard let c = UserDefaults.standard.array(forKey: "theme_\(key)") as? [Double],
              c.count == 4 else { return nil }
        return Color(red: c[0], green: c[1], blue: c[2], opacity: c[3])
    }
}
