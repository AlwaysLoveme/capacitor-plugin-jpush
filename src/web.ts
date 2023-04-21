import { WebPlugin } from '@capacitor/core';

import type { JPushPlugin } from './definitions';

export class JPushWeb extends WebPlugin implements JPushPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
