import type { PluginListenerHandle } from '@capacitor/core';

declare module '@capacitor/cli' {
  export interface PluginsConfig {
    JPush?: SetUpOptions;
  }
}

export interface JPushPlugin {
  setAlias(options: AliasOptions): Promise<void>;
  deleteAlias(): Promise<void>;
  addTags(options: SetTagsOptions): Promise<void>;
  deleteTags(options: SetTagsOptions): Promise<void>;
  cleanTags(): Promise<void>;
  setBadgeNumber(options?: SetBadgeNumberOptions): Promise<void>;
  removeListeners(): Promise<void>;
  getRegistrationID(): Promise<{registrationId: string}>;
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