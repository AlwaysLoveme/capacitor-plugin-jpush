import Foundation

@objc public class JPush: NSObject {
    @objc public func echo(_ value: String) -> String {
        print(value)
        return value
    }
}
