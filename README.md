# capacitor-plugin-jpush
<p align="left">
  <!-- <a href="https://img.shields.io/badge/support-Android-516BEB?logo=android&logoColor=white&style=plastic">
    <img src="https://img.shields.io/badge/support-Android-516BEB?style=plastic">
  </a> -->
  <a href="https://img.shields.io/badge/support-IOS-516BEB?logo=android&logoColor=white&style=plastic">
    <img src="https://img.shields.io/badge/support-IOS-516BEB?style=plastic">
  </a>
  <a href="https://img.shields.io/badge/support-Android-516BEB?logo=android&logoColor=white&style=plastic">
    <img src="https://img.shields.io/badge/support-Android-516BEB?style=plastic">
  </a>
  <a href="https://www.npmjs.com/package/capacitor-plugin-jpush">
    <img src="https://img.shields.io/npm/v/capacitor-plugin-jpush/latest.svg">
  </a>
  <a href="https://www.npmjs.com/package/capacitor-plugin-jpush">
    <img src="https://img.shields.io/npm/dm/capacitor-plugin-jpush.svg"/>
  </a>
</p>

[简体中文](./Zh-CN.md)

jpush plugin for capacitor4.0+

## Install

```bash
npm install capacitor-plugin-jpush
npx cap sync
```

## Usage

in `capacitor.config.ts`:

```ts
/// <reference types="capacitor-plugin-jpush" />

import { CapacitorConfig } from '@capacitor/cli';
const config: CapacitorConfig = {
  plugins: {
    JPush: {
      // your application appKey on JPush
      appKey: '',
      channel: '',
    },
  },
};

export default config;
```

in `capacitor.config.json`:

```json
{
  "plugins": {
    "JPush": {
      // your application appKey on JPush
      "appKey": "",
      "channel": ""
    }
  }
}
```

### IOS

On iOS you must enable the Push Notifications capability. See [Setting Capabilities](https://capacitorjs.com/docs/v4/ios/configuration#setting-capabilities) for instructions on how to enable the capability.

After enabling the Push Notifications capability, add the following to your app's `AppDelegate.swift`:

```swift
func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
  NotificationCenter.default.post(name: .capacitorDidRegisterForRemoteNotifications, object: deviceToken)
}
func application(_ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error) {
  NotificationCenter.default.post(name: .capacitorDidFailToRegisterForRemoteNotifications, object: error)
}

// add the following code to applicationDidBecomeActive function
NotificationCenter.default.post(name: Notification.Name(rawValue: "didBecomeActiveNotification"), object: nil)
```
then use Xcode to open your native project, and set `JPUSHService.h` file's `Target MemberShip` to `CapacitorPluginJPush` which value is `Public`:

![https://user-images.githubusercontent.com/29945352/235104201-a39bdb6e-314d-423a-beb7-2869f3b27679.png](https://user-images.githubusercontent.com/29945352/235104201-a39bdb6e-314d-423a-beb7-2869f3b27679.png)
### Android
Android 13 requires a permission check in order to send notifications.  You are required to call `checkPermissions()` and `requestPermissions()` accordingly.

On Android 12 and older it won't show a prompt and will just return as granted.

please set both `compileSdkVersion` and `targetSdkVersion` to `33` in `variables.gradle`:

![android studio](https://files-1316618304.cos.ap-shanghai.myqcloud.com/20230506181607.png)

add the following to your app's `build.gradle`:
```bash
manifestPlaceholders = [
  JPUSH_PKGNAME: applicationId,
]
```
![](https://files-1316618304.cos.ap-shanghai.myqcloud.com/20230506181843.png)

> Currently does not support the manufacturer channel push

## Example

```ts
import { JPush } from 'capacitor-plugin-jpush';

// set alias
await JPush.setAlias({
  alias: 'alias',
});

// getRegistrationID
const { registrationId } = await JPush.getRegistrationID();
console.log(registrationId);

// addListener events
const receivedEvent = await JPush.addListener('notificationReceived', data => {
  console.log('notificationReceived------>', data);
});
receivedEvent.remove();

const openedEvent = await JPush.addListener('notificationOpened', data => {
  console.log('notificationOpened------>', data);
});
openedEvent.remove();
```

## API

<docgen-index>

* [`setDebugMode(...)`](#setdebugmode)
* [`setAlias(...)`](#setalias)
* [`deleteAlias(...)`](#deletealias)
* [`addTags(...)`](#addtags)
* [`deleteTags(...)`](#deletetags)
* [`cleanTags()`](#cleantags)
* [`setBadgeNumber(...)`](#setbadgenumber)
* [`removeListeners()`](#removelisteners)
* [`getRegistrationID()`](#getregistrationid)
* [`checkPermissions()`](#checkpermissions)
* [`requestPermissions()`](#requestpermissions)
* [`openNotificationSetting()`](#opennotificationsetting)
* [`addListener('notificationReceived', ...)`](#addlistenernotificationreceived)
* [`addListener('notificationOpened', ...)`](#addlistenernotificationopened)
* [Interfaces](#interfaces)
* [Type Aliases](#type-aliases)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### setDebugMode(...)

```typescript
setDebugMode(isDebug: boolean) => Promise<void>
```

enable JPush debug log

| Param         | Type                 |
| ------------- | -------------------- |
| **`isDebug`** | <code>boolean</code> |

--------------------


### setAlias(...)

```typescript
setAlias(options: AliasOptions) => Promise<void>
```

set alias for JPush

| Param         | Type                                                  |
| ------------- | ----------------------------------------------------- |
| **`options`** | <code><a href="#aliasoptions">AliasOptions</a></code> |

--------------------


### deleteAlias(...)

```typescript
deleteAlias(options: DeleteAlias) => Promise<void>
```

| Param         | Type                                                |
| ------------- | --------------------------------------------------- |
| **`options`** | <code><a href="#deletealias">DeleteAlias</a></code> |

--------------------


### addTags(...)

```typescript
addTags(options: SetTagsOptions) => Promise<void>
```

| Param         | Type                                                      |
| ------------- | --------------------------------------------------------- |
| **`options`** | <code><a href="#settagsoptions">SetTagsOptions</a></code> |

--------------------


### deleteTags(...)

```typescript
deleteTags(options: SetTagsOptions) => Promise<void>
```

| Param         | Type                                                      |
| ------------- | --------------------------------------------------------- |
| **`options`** | <code><a href="#settagsoptions">SetTagsOptions</a></code> |

--------------------


### cleanTags()

```typescript
cleanTags() => Promise<void>
```

--------------------


### setBadgeNumber(...)

```typescript
setBadgeNumber(options?: SetBadgeNumberOptions | undefined) => Promise<void>
```

| Param         | Type                                                                    |
| ------------- | ----------------------------------------------------------------------- |
| **`options`** | <code><a href="#setbadgenumberoptions">SetBadgeNumberOptions</a></code> |

--------------------


### removeListeners()

```typescript
removeListeners() => Promise<void>
```

--------------------


### getRegistrationID()

```typescript
getRegistrationID() => Promise<{ registrationId: string; }>
```

**Returns:** <code>Promise&lt;{ registrationId: string; }&gt;</code>

--------------------


### checkPermissions()

```typescript
checkPermissions() => Promise<PermissionStatus>
```

**Returns:** <code>Promise&lt;<a href="#permissionstatus">PermissionStatus</a>&gt;</code>

--------------------


### requestPermissions()

```typescript
requestPermissions() => Promise<PermissionStatus>
```

**Returns:** <code>Promise&lt;<a href="#permissionstatus">PermissionStatus</a>&gt;</code>

--------------------


### openNotificationSetting()

```typescript
openNotificationSetting() => Promise<void>
```

now only on Android

--------------------


### addListener('notificationReceived', ...)

```typescript
addListener(eventName: "notificationReceived", listenerFunc: (notificationData: ReceiveNotificationData) => void) => Promise<PluginListenerHandle> & PluginListenerHandle
```

| Param              | Type                                                                                                       |
| ------------------ | ---------------------------------------------------------------------------------------------------------- |
| **`eventName`**    | <code>'notificationReceived'</code>                                                                        |
| **`listenerFunc`** | <code>(notificationData: <a href="#receivenotificationdata">ReceiveNotificationData</a>) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

--------------------


### addListener('notificationOpened', ...)

```typescript
addListener(eventName: "notificationOpened", listenerFunc: (notificationData: ReceiveNotificationData) => void) => Promise<PluginListenerHandle> & PluginListenerHandle
```

| Param              | Type                                                                                                       |
| ------------------ | ---------------------------------------------------------------------------------------------------------- |
| **`eventName`**    | <code>'notificationOpened'</code>                                                                          |
| **`listenerFunc`** | <code>(notificationData: <a href="#receivenotificationdata">ReceiveNotificationData</a>) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

--------------------


### Interfaces


#### AliasOptions

| Prop           | Type                |
| -------------- | ------------------- |
| **`alias`**    | <code>string</code> |
| **`sequence`** | <code>number</code> |


#### DeleteAlias

| Prop           | Type                |
| -------------- | ------------------- |
| **`sequence`** | <code>number</code> |


#### SetTagsOptions

| Prop       | Type                  |
| ---------- | --------------------- |
| **`tags`** | <code>string[]</code> |


#### SetBadgeNumberOptions

| Prop        | Type                |
| ----------- | ------------------- |
| **`badge`** | <code>number</code> |


#### PermissionStatus

| Prop                | Type                                                        |
| ------------------- | ----------------------------------------------------------- |
| **`notifications`** | <code><a href="#permissionstate">PermissionState</a></code> |


#### PluginListenerHandle

| Prop         | Type                                      |
| ------------ | ----------------------------------------- |
| **`remove`** | <code>() =&gt; Promise&lt;void&gt;</code> |


#### ReceiveNotificationData

| Prop           | Type                                                                                                                                  |
| -------------- | ------------------------------------------------------------------------------------------------------------------------------------- |
| **`title`**    | <code>string</code>                                                                                                                   |
| **`content`**  | <code>string</code>                                                                                                                   |
| **`subTitle`** | <code>string</code>                                                                                                                   |
| **`rawData`**  | <code>{ [x: string]: any; aps: { alert: { body: string; subTitle: string; title: string; }; badge: number; sound: string; }; }</code> |


### Type Aliases


#### PermissionState

<code>'prompt' | 'prompt-with-rationale' | 'granted' | 'denied'</code>

</docgen-api>
