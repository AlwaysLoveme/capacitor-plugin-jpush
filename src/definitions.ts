/// <reference types="@capacitor/cli" />

import type { PluginListenerHandle, PermissionState } from '@capacitor/core';

declare module '@capacitor/cli' {
  export interface PluginsConfig {
    JPush?: {
      /**
       * 推送服务的基础信息设置，必填项，否则推送服务不会初始化
       */
      appKey: string;
      channel?: string;
      isProduction?: boolean;
      /**
       * 设备返回前台是,是否自动清空角标,默认 false
       */
      cleanBadgeWhenActive?: boolean;
    };
  }
}

export interface JPushPlugin {
  /**
   * 启动极光推送服务，即使没有获取到通知权限，也会进行推送服务初始化
   */
  startJPush(): Promise<void>;
  /**
   * 开启 debug 模式 log日志
   * @param isDebug
   */
  setDebugMode(isDebug: boolean): Promise<void>;
  /**
   * 设置推送别名，可作为推送消息的目标对象
   * @param options
   */
  setAlias(options: AliasOptions): Promise<void>;
  /**
   * 删除推送别名
   * @param options
   */
  deleteAlias(options?: DeleteAlias): Promise<void>;
  /**
   * 设置推送标签
   * @param options
   */
  addTags(options: SetTagsOptions): Promise<void>;
  /**
   * 删除推送标签
   * @param options
   */
  deleteTags(options: SetTagsOptions): Promise<void>;
  cleanTags(): Promise<void>;
  /**
   * 设置 APP 角标数字，设为 0 即清空角标
   * @param options
   */
  setBadgeNumber(options?: SetBadgeNumberOptions): Promise<void>;
  removeListeners(): Promise<void>;
  /**
   * 获取设备的注册 ID，若服务重新注册，则返回的 ID 是不一样的
   */
  getRegistrationID(): Promise<{ registrationId: string }>;
  /**
   * 检查通知权限状态
   */
  checkPermissions(): Promise<PermissionStatus>;
  /**
   * 申请通知权限
   */
  requestPermissions(): Promise<PermissionStatus>;
  /**
   * 打开推送通知权限设置页面（目前仅安卓支持）
   */
  openNotificationSetting(): Promise<void>;
  /**
   * 监听推送消息
   * @param eventName
   * @param listenerFunc
   */
  addListener(
    eventName: 'notificationReceived',
    listenerFunc: (notificationData: ReceiveNotificationData) => void,
  ): Promise<PluginListenerHandle>;
  /**
   * 监听消息栏通知被点击
   * @param eventName
   * @param listenerFunc
   */
  addListener(
    eventName: 'notificationOpened',
    listenerFunc: (notificationData: ReceiveNotificationData) => void,
  ): Promise<PluginListenerHandle>;
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
    };
    [x: string]: any;
  };
}

export interface PermissionStatus {
  /**
   * prompt: 首次申请，询问。
   * prompt-with-rationale： 每次都询问。
   * granted： 已获取权限。
   * denied：权限已拒绝。
   */
  permission: PermissionState;
}
