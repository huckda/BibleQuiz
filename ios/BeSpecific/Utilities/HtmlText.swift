import Foundation
import SwiftUI

extension String {
    func htmlToAttributedString() -> AttributedString {
        var result = AttributedString()
        let decoded = self
            .replacingOccurrences(of: "&nbsp;", with: " ")
            .replacingOccurrences(of: "&amp;", with: "&")
            .replacingOccurrences(of: "&lt;", with: "<")
            .replacingOccurrences(of: "&gt;", with: ">")
            .replacingOccurrences(of: "&quot;", with: "\"")
            .replacingOccurrences(of: "&#39;", with: "'")

        var remaining = decoded
        while !remaining.isEmpty {
            if let openRange = remaining.range(of: "<i>", options: .caseInsensitive) {
                let before = String(remaining[..<openRange.lowerBound])
                if !before.isEmpty { result += AttributedString(before) }
                remaining = String(remaining[openRange.upperBound...])

                if let closeRange = remaining.range(of: "</i>", options: .caseInsensitive) {
                    var italic = AttributedString(String(remaining[..<closeRange.lowerBound]))
                    italic.inlinePresentationIntent = .emphasized
                    result += italic
                    remaining = String(remaining[closeRange.upperBound...])
                } else {
                    result += AttributedString(remaining)
                    remaining = ""
                }
            } else {
                result += AttributedString(remaining)
                remaining = ""
            }
        }
        return result
    }
}
