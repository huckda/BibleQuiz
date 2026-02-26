import Foundation

enum TextCleaner {
    private static let htmlTagRegex = try! NSRegularExpression(pattern: "<[^>]*>")

    static func clean(_ html: String) -> String {
        let range = NSRange(html.startIndex..., in: html)
        var result = htmlTagRegex.stringByReplacingMatches(in: html, range: range, withTemplate: "")
        result = result.replacingOccurrences(of: "&nbsp;", with: " ")
        result = result.replacingOccurrences(of: "&amp;", with: "&")
        result = result.replacingOccurrences(of: "&lt;", with: "<")
        result = result.replacingOccurrences(of: "&gt;", with: ">")
        result = result.replacingOccurrences(of: "&quot;", with: "\"")
        result = result.replacingOccurrences(of: "&#39;", with: "'")
        return result.trimmingCharacters(in: .whitespaces)
    }
}
