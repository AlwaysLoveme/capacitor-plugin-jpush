import type { PluginListenerHandle, PermissionState } from '@capacitor/core';

declare module '@capacitor/cli' {
  export interface PluginsConfig {
    JPush?: SetUpOptions;
  }
}

export interface JPushPlugin {
  /**
   * enable JPush debug log
   * @param isDebug 
   */
  setDebugMode(isDebug: boolean): Promise<void>;
  setAlias(options: AliasOptions): Promise<void>;
  deleteAlias(options: DeleteAlias): Promise<void>;
  addTags(options: SetTagsOptions): Promise<void>;
  deleteTags(options: SetTagsOptions): Promise<void>;
  cleanTags(): Promise<void>;
  setBadgeNumber(options?: SetBadgeNumberOptions): Promise<void>;
  removeListeners(): Promise<void>;
  getRegistrationID(): Promise<{registrationId: string}>;
  checkPermissions(): Promise<PermissionStatus>;
  requestPermissions(): Promise<PermissionStatus>;
  openNotificationSetting(): Promise<void>;
  addListener(eventName: "notificationReceived", listenerFunc: (notificationData: ReceiveNotificationData) => void): Promise<PluginListenerHandle> & PluginListenerHandle;
  addListener(eventName: "notificationOpened", listenerFunc: (notificationData: ReceiveNotificationData) => void): Promise<PluginListenerHandle> & PluginListenerHandle;
}

export interface SetUpOptions {
  appKey: string;
  channel?: string;
  isProduction?: boolean;
}

export interface AliasOptions {
  alias: string;
  sequence?: number;
}

export interface DeleteAlias {
  sequence?: number;
}

export interface SetTagsOptions {
  tags: string[];
}

export interface SetBadgeNumberOptions {
  badge: number;
}

export interface RegistrationIdReceive {
  registrationId: string;
}

export interface ReceiveNotificationData {
  title: string;
  content: string;
  subTitle: string;
  rawData: {
    aps: {
      alert: {
        body: string;
        subTitle: string;
        title: string;
      };
      badge: number;
      sound: string;
    }
    [x: string]: any;
  };
}

export interface PermissionStatus {
  notifications: PermissionState;
}