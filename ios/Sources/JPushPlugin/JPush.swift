import Capacitor
import Foundation
import UserNotifications
import JPushCoreLib

@objc public class JPush: NSObject {
    public func requestPermissions(with completion: ((Bool, Error?) -> Void)? = nil) {
           UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound, .badge]) { granted, error in
               completion?(granted, error)
           }
       }

       public func checkPermissions(with completion: ((UNAuthorizationStatus) -> Void)? = nil) {
           UNUserNotificationCenter.current().getNotificationSettings { settings in
               completion?(settings.authorizationStatus)
           }
       }
}
