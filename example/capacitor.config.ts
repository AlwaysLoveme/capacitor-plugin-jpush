/// <reference types="capacitor-plugin-jpush" />

import { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'jiguang.test.cn',
  appName: 'jpushTest',
  webDir: 'dist',
  plugins: {
    SplashScreen: {
      launchShowDuration: 3000,
      launchAutoHide: true,
      splashFullScreen: true,
      splashImmersive: true,
    },
    JPush: {
      // your application appKey on JPush
      appKey: '3edf7a4d8f2ba371a6a9d1ac',
      channel: '',
    },
  },
  server: {
    cleartext: true,
    url: 'http://10.200.77.5:5174/',
  },
};

export default config;
