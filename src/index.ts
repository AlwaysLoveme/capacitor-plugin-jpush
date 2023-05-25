import { registerPlugin } from '@capacitor/core';

import type { JPushPlugin } from './definitions';

const JPush = registerPlugin<JPushPlugin>('JPush');

export * from './definitions';
export { JPush };
