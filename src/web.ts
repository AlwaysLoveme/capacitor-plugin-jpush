import { WebPlugin } from '@capacitor/core';

import type { JPushPlugin } from './definitions';

export class JPushPluginWeb extends WebPlugin implements JPushPlugin {
  startJPush(): Promise<void> {
    console.warn('startJPush not implemented on web.');
    return Promise.resolve();
  }
  setDebugMode(_isDebug: boolean): Promise<void> {
    console.warn('setDebugMode not implemented on web.');
    return Promise.resolve();
  }
  setAlias(_options: any): Promise<void> {
    console.warn('setAlias not implemented on web.');
    return Promise.resolve();
  }
  deleteAlias(_options?: any): Promise<void> {
    console.warn('deleteAlias not implemented on web.');
    return Promise.resolve();
  }
  addTags(_options: any): Promise<void> {
    console.warn('addTags not implemented on web.');
    return Promise.resolve();
  }
  deleteTags(_options: any): Promise<void> {
    console.warn('deleteTags not implemented on web.');
    return Promise.resolve();
  }
  cleanTags(): Promise<void> {
    console.warn('cleanTags not implemented on web.');
    return Promise.resolve();
  }
  setBadgeNumber(_options?: any): Promise<void> {
    console.warn('setBadgeNumber not implemented on web.');
    return Promise.resolve();
  }
  getRegistrationID(): Promise<{ registrationId: string }> {
    console.warn('getRegistrationID not implemented on web.');
    return Promise.resolve({ registrationId: '' });
  }
  checkPermissions(): Promise<any> {
    console.warn('checkPermissions not implemented on web.');
    return Promise.resolve({ receiveNotification: false });
  }
  requestPermissions(): Promise<any> {
    console.warn('requestPermissions not implemented on web.');
    return Promise.resolve({ receiveNotification: false });
  }
  removeListeners(): Promise<void> {
    console.warn('removeAllListeners not implemented on web.');
    return Promise.resolve();
  }
  openNotificationSetting(): Promise<void> {
    console.warn('openNotificationSetting not implemented on web.');
    return Promise.resolve();
  }
}
