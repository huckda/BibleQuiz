import SwiftUI

struct SettingsView: View {
    @EnvironmentObject var theme: ThemeSettings

    var body: some View {
        List {
            Section("Colors") {
                ColorPicker("Background",    selection: $theme.background,   supportsOpacity: false)
                ColorPicker("Button Color",  selection: $theme.primary,      supportsOpacity: false)
                ColorPicker("Button Text",   selection: $theme.onPrimary,    supportsOpacity: false)
                ColorPicker("Text Color",    selection: $theme.onBackground, supportsOpacity: false)
                ColorPicker("Button Outline",selection: $theme.outline,      supportsOpacity: false)
            }

            Section {
                Button("Reset to Defaults", role: .destructive) {
                    theme.reset()
                }
            }
        }
        .navigationTitle("Appearance")
    }
}
