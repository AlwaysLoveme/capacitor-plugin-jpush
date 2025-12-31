import Foundation
import Capacitor
import UserNotifications

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
enum PushNotificationError: Error {
    case tokenParsingFailed
    case tokenRegistrationFailed
}

enum PushNotificationsPermissions: String {
    case prompt
    case denied
    case granted
}

@objc(JPushPlugin)
public class JPushPlugin: CAPPlugin {
    private let implementation = JPush()

    var sequence = Int(arc4random_uniform(1000))
    private func getSequence() -> Int {
        sequence += 1
        if sequence >= Int(INT32_MAX) {
            sequence = 0
        }
        return sequence
    }

    override public func load() {

        NotificationCenter.default.addObserver(self,
                                               selector: #selector(self.didRegisterForRemoteNotificationsWithDeviceToken(notification:)),
                                               name: .capacitorDidRegisterForRemoteNotifications,
                                               object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(self.didFinishLaunching(_:)), name: NSNotification.capacitorDidRegisterForRemoteNotifications, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(self.didBecomeActive(_:)), name: Notification.Name(rawValue: "didBecomeActiveNotification"), object: nil)

    }
    // 极光初始化
    private func jpushInit(_ call: CAPPluginCall) {
        let isProduction = getConfig().getBoolean("isProduction", true)
        let appKey: String = getConfig().getString("appKey") ?? ""
        let channel: String = getConfig().getString("channel") ?? ""

        if appKey == "" {
            call.reject("请在 capacitor.config.ts(capacitor.config.json) 中配置 appKey")
            return
        }
        
        print("JPushPlugin init with appKey: \(appKey), channel: \(channel), isProduction: \(isProduction)")

        DispatchQueue.main.async {
            if #available(iOS 10, *) {
                let jpushConfig = JPUSHRegisterEntity()
                jpushConfig.types = NSInteger(UNAuthorizationOptions.alert.rawValue) |
                    NSInteger(UNAuthorizationOptions.sound.rawValue) |
                    NSInteger(UNAuthorizationOptions.badge.rawValue)
                JPUSHService.register(forRemoteNotificationConfig: jpushConfig, delegate: self)
            } else if #available(iOS 8, *) {
                // 可以自定义 categories
                JPUSHService.register(
                    forRemoteNotificationTypes: UIUserNotificationType.badge.rawValue |
                        UIUserNotificationType.sound.rawValue |
                        UIUserNotificationType.alert.rawValue,
                    categories: nil)
            } else {
                // ios 8 以前 categories 必须为nil
                JPUSHService.register(
                    forRemoteNotificationTypes: UIRemoteNotificationType.badge.rawValue |
                        UIRemoteNotificationType.sound.rawValue |
                        UIRemoteNotificationType.alert.rawValue,
                    categories: nil)
            }

            JPUSHService.setup(withOption: nil, appKey: appKey, channel: channel, apsForProduction: isProduction)

            call.resolve()
        }
    }

    deinit {
        NotificationCenter.default.removeObserver(self)
    }

    // 注册 DeviceToken
    @objc public func didRegisterForRemoteNotificationsWithDeviceToken(notification: NSNotification) {
        if let deviceToken = notification.object as? Data {
            let deviceTokenString = deviceToken.reduce("", {$0 + String(format: "%02X", $1)})
            JPUSHService.registerDeviceToken(deviceToken)
            notifyListeners("registration", data: [
                "value": deviceTokenString
            ])
        } else if let stringToken = notification.object as? String {
            notifyListeners("registration", data: [
                "value": stringToken
            ])
        } else {
            notifyListeners("registrationError", data: [
                "error": PushNotificationError.tokenParsingFailed.localizedDescription
            ])
        }
    }

    @objc func didFinishLaunching(_ notification: NSNotification) {}
    
    // APP 返回前台时清空角标
    @objc func didBecomeActive(_ notification: NSNotification) {
        let cleanBadge = getConfig().getBoolean("cleanBadgeWhenActive", false)
        if cleanBadge {
            JPUSHService.setBadge(0)
            UIApplication.shared.applicationIconBadgeNumber = 0
        }
    }

    @objc func startJPush(_ call: CAPPluginCall) {
        jpushInit(call)
    }

    /**
     * Request notification permission
     */
    @objc override public func requestPermissions(_ call: CAPPluginCall) {
        self.implementation.requestPermissions { granted, error in
            guard error == nil else {
                if let err = error {
                    call.reject(err.localizedDescription)
                    return
                }

                call.reject("unknown error in permissions request")
                return
            }

            var result: PushNotificationsPermissions = .denied

            if granted {
                result = .granted
            }

            call.resolve(["permission": result.rawValue])
        }
    }

    /**
     * Check notification permission
     */
    @objc override public func checkPermissions(_ call: CAPPluginCall) {
        self.implementation.checkPermissions { status in
            var result: PushNotificationsPermissions = .prompt

            switch status {
            case .notDetermined:
                result = .prompt
            case .denied:
                result = .denied
            case .ephemeral, .authorized, .provisional:
                result = .granted
            @unknown default:
                result = .prompt
            }

            call.resolve(["permission": result.rawValue])
        }
    }

    @objc func setAlias(_ call: CAPPluginCall) {
        let alias = call.getString("alias") ?? ""
        JPUSHService.setAlias(alias, completion: { (resCode, _, _) in
            if resCode == 0 {
                call.resolve()
            } else {
                call.reject("Error setting alias")
            }
        }, seq: 0)
    }

    @objc func deleteAlias(_ call: CAPPluginCall) {
        JPUSHService.deleteAlias({ (resCode, alias, _) in
            if resCode == 0 {
                call.resolve(["data": alias ?? ""])
            } else {
                call.reject("Error deleting alias")
            }
        }, seq: 0)
    }

    @objc func addTags(_ call: CAPPluginCall) {
        guard let tags = call.getArray("tags", String.self) else {
            call.reject("Invalid tags")
            return
        }

        JPUSHService.setTags(Set(tags), completion: { (_, _, _) in
            call.resolve()
        }, seq: getSequence())
    }

    @objc func deleteTags(_ call: CAPPluginCall) {
        guard let tags = call.getArray("tags", String.self) else {
            call.reject("Invalid tags")
            return
        }

        JPUSHService.deleteTags(Set(tags), completion: { (_, _, _) in
            call.resolve()
        }, seq: getSequence())
    }

    @objc func cleanTags(_ call: CAPPluginCall) {
        JPUSHService.cleanTags({ (_, _, _) in
            call.resolve()
        }, seq: getSequence())
    }

    @objc func setBadgeNumber(_ call: CAPPluginCall) {
        let badge = call.getInt("badge") ?? 0
        JPUSHService.setBadge(badge)
        UIApplication.shared.applicationIconBadgeNumber = badge
        call.resolve()
    }

    @objc func getRegistrationID(_ call: CAPPluginCall) {
        let registrationID = JPUSHService.registrationID()
        call.resolve(["registrationId": registrationID])
    }
}

extension JPushPlugin: JPUSHRegisterDelegate {
    public func jpushNotificationAuthorization(_ status: JPAuthorizationStatus, withInfo info: [AnyHashable: Any]?) {}
    public func jpushNotificationCenter(_ center: UNUserNotificationCenter, openSettingsFor notification: UNNotification) {}
    // 监听消息推送
    @available(iOS 10.0, *)
    public func jpushNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: ((Int) -> Void)) {
        let notificationContent = notification.request.content
        let userInfo = notificationContent.userInfo
        if let trigger = userInfo["trigger"] as? [String: AnyObject], trigger["type"]?.intValue == 1 {
            JPUSHService.handleRemoteNotification(userInfo)
        }
        let data: [String: Any] = [
            "title": notificationContent.title,
            "subTitle": notificationContent.subtitle,
            "content": notificationContent.body,
            "rawData": [
                "extra": userInfo
            ]
        ]

        self.notifyListeners("notificationReceived", data: data)
        completionHandler(Int(UNNotificationPresentationOptions.alert.rawValue))
    }
    // 监听通知栏消息点击
    @available(iOS 10.0, *)
    public func jpushNotificationCenter(_ center: UNUserNotificationCenter, didReceive response: UNNotificationResponse, withCompletionHandler completionHandler: (() -> Void)) {
        let notificationContent = response.notification.request.content
        let userInfo = notificationContent.userInfo
        if let trigger = userInfo["trigger"] as? [String: AnyObject], trigger["type"]?.intValue == 1 {
            JPUSHService.handleRemoteNotification(userInfo)
        }
        let data: [String: Any] = [
            "title": notificationContent.title,
            "subTitle": notificationContent.subtitle,
            "content": notificationContent.body,
            "rawData": [
                "extra": userInfo
            ]
        ]
        self.notifyListeners("notificationOpened", data: data)
        completionHandler()
    }
}

// 地理围栏
extension JPushPlugin: JPUSHGeofenceDelegate {
    public func jpushGeofenceRegion(_ geofence: [AnyHashable: Any]?, error: Error?) {

    }

    public func jpushCallbackGeofenceReceived(_ geofenceList: [[AnyHashable: Any]]?) {

    }

    public func jpushGeofenceIdentifer(_ geofenceId: String, didEnterRegion userInfo: [AnyHashable: Any]?, error: Error?) {

    }

    public func jpushGeofenceIdentifer(_ geofenceId: String, didExitRegion userInfo: [AnyHashable: Any]?, error: Error?) {

    }
}
