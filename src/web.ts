import { WebPlugin } from '@capacitor/core';

import type { JPushPlugin } from './definitions';

export class JPushWeb extends WebPlugin implements JPushPlugin {
  deleteTags(): Promise<void> {
    throw new Error('Method not supported on Web.');
  }
  cleanTags(): Promise<void> {
    throw new Error('Method not supported on Web.');
  }
  setBadgeNumber(): Promise<void> {
    throw new Error('Method not supported on Web.');
  }
  async setAlias(): Promise<void> {
    throw new Error('Method not supported on Web.');
  }
  async deleteAlias(): Promise<void> {
    throw new Error('Method not supported on Web.');
  }
  async removeListeners(): Promise<void>  {
    throw new Error('Method not supported on Web.');
  }
  async addTags(): Promise<void> {
    throw new Error('Method not supported on Web.');
  }
  async getRegistrationID(): Promise<{registrationId: string}> {
    throw new Error('Method not supported on Web.');
  }
}
