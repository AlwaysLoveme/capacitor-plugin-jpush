# capacitor-plugin-jpush
<p align="left">
  <!-- <a href="https://img.shields.io/badge/support-Android-516BEB?logo=android&logoColor=white&style=plastic">
    <img src="https://img.shields.io/badge/support-Android-516BEB?style=plastic">
  </a> -->
  <a href="https://img.shields.io/badge/support-IOS-516BEB?logo=android&logoColor=white&style=plastic">
    <img src="https://img.shields.io/badge/support-IOS-516BEB?style=plastic">
  </a>
  <a href="https://www.npmjs.com/package/capacitor-plugin-jpush">
    <img src="https://img.shields.io/npm/v/capacitor-plugin-jpush/latest.svg">
  </a>
  <a href="https://www.npmjs.com/package/capacitor-plugin-jpush">
    <img src="https://img.shields.io/npm/dm/capacitor-plugin-jpush.svg"/>
  </a>
</p>

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

### Android

To be developed...

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

* [`setAlias(...)`](#setalias)
* [`deleteAlias()`](#deletealias)
* [`addTags(...)`](#addtags)
* [`deleteTags(...)`](#deletetags)
* [`cleanTags()`](#cleantags)
* [`setBadgeNumber(...)`](#setbadgenumber)
* [`removeListeners()`](#removelisteners)
* [`getRegistrationID()`](#getregistrationid)
* [`addListener('notificationReceived', ...)`](#addlistenernotificationreceived)
* [`addListener('notificationOpened', ...)`](#addlistenernotificationopened)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### setAlias(...)

```typescript
setAlias(options: AliasOptions) => Promise<void>
```

| Param         | Type                                                  |
| ------------- | ----------------------------------------------------- |
| **`options`** | <code><a href="#aliasoptions">AliasOptions</a></code> |

--------------------


### deleteAlias()

```typescript
deleteAlias() => Promise<void>
```

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

| Prop        | Type                |
| ----------- | ------------------- |
| **`alias`** | <code>string</code> |


#### SetTagsOptions

| Prop       | Type                  |
| ---------- | --------------------- |
| **`tags`** | <code>string[]</code> |


#### SetBadgeNumberOptions

| Prop        | Type                |
| ----------- | ------------------- |
| **`badge`** | <code>number</code> |


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

</docgen-api>
