import Foundation
import Capacitor
/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(Jpush)
public class Jpush: CAPPlugin {
    
    func application(application: UIApplication, didFinishLaunchingWithOptions launchOptions: [NSObject: AnyObject]?) -> Bool {
       
       if((UIDevice.currentDevice().systemVersion as NSString).floatValue >= 8.0) {
         // 可以自定义 categories
         JPUSHService.registerForRemoteNotificationTypes(UIUserNotificationType.Badge.rawValue | UIUserNotificationType.Badge.rawValue | UIUserNotificationType.Alert.rawValue , categories: nil)
       } else {
         JPUSHService.registerForRemoteNotificationTypes(UIUserNotificationType.Badge.rawValue | UIUserNotificationType.Badge.rawValue | UIUserNotificationType.Alert.rawValue , categories: nil)
       }
       JPUSHService.setupWithOption(launchOptions, appKey: appKey, channel: channel, apsForProduction: isProduction)
       
       return true
     }
    
    func application(application: UIApplication,
        didRegisterForRemoteNotificationsWithDeviceToken deviceToken: NSData) {
          print("get the deviceToken  \(deviceToken)")
          NSNotificationCenter.defaultCenter().postNotificationName("DidRegisterRemoteNotification", object: deviceToken)
          JPUSHService.registerDeviceToken(deviceToken)
      }
    
    @objc override public func load() {
       // Called when the plugin is first constructed in the bridge
//        JPUSHService.

     }

    @objc func echo(_ call: CAPPluginCall) {
        let value = call.getString("value") ?? ""
        call.success([
            "value": value
        ])
    }
    
    @objc func startPushSDK(_ call: CAPPluginCall) {
        JPUSHService.startPushSDK()
        call.success()
    }
    
}

