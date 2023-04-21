import { registerPlugin } from '@capacitor/core';

import type { JPushPlugin } from './definitions';

const JPush = registerPlugin<JPushPlugin>('JPush', {
  web: () => import('./web').then(m => new m.JPushWeb()),
});

export * from './definitions';
export { JPush };
