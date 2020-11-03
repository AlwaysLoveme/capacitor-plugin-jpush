import { WebPlugin } from '@capacitor/core';
import { JpushPlugin } from './definitions';

export class JpushWeb extends WebPlugin implements JpushPlugin {
  constructor() {
    super({
      name: 'Jpush',
      platforms: ['web'],
    });
  }

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }

  async startPushSDK(): Promise<Boolean> {
    return Promise.resolve(true);
  }
}

const Jpush = new JpushWeb();

export { Jpush };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(Jpush);
