/// <reference types="capacitor-plugin-jpush" />

import { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.ionic.demotest',
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
      appKey: '4dde5a4f2c47443953bcf027',
      channel: '',
    },
  },
  server: {
    cleartext: true,
    // url: 'http://10.200.186.220:5173',
  },
};

export default config;
