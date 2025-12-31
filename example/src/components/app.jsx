import React from 'react';

import { SafeArea } from 'capacitor-plugin-safe-area';
import { f7ready, App, View } from 'framework7-react';
import { SplashScreen } from '@capacitor/splash-screen';
import { StatusBar, Style } from '@capacitor/status-bar';

import routes from '../js/routes';
import store from '../js/store';

const MyApp = () => {
  // Framework7 Parameters
  const f7params = {
    name: 'com.ionic.demotest', // App name
    theme: 'auto', // Automatic theme detection
    colors: {
      primary: '#00a76f',
    },

    // App store
    store: store,
    // App routes
    routes: routes,
    iosTranslucentBars: false,
  };

  f7ready(() => {
    // Call F7 APIs here
    console.log(111111);
    SplashScreen.hide();
    SafeArea.setImmersiveNavigationBar();
    SafeArea.getSafeAreaInsets().then(({ insets }) => {
      for (const [key, value] of Object.entries(insets)) {
        document.documentElement.style.setProperty(`--f7-safe-area-${key}`, `${value}px`);
      }
      StatusBar.setStyle({ style: Style.Dark });
    });
  });

  React.useEffect(() => {}, []);

  return (
    <App {...f7params}>
      {/* Your main view, should have "view-main" class */}
      <View main className="safe-areas" url="/" />
    </App>
  );
};
export default MyApp;
