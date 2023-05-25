#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>

// Define the plugin using the CAP_PLUGIN Macro, and
// each method the plugin supports using the CAP_PLUGIN_METHOD macro.
CAP_PLUGIN(JPushPlugin, "JPush",
         CAP_PLUGIN_METHOD(startJPush, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(setAlias, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(deleteAlias, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(addTags, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(deleteTags, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(cleanTags, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(setBadgeNumber, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(removeListeners, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(getRegistrationID, CAPPluginReturnPromise);
)
