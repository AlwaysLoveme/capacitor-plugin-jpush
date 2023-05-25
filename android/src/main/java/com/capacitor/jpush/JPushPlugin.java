package com.capacitor.jpush;

import android.Manifest;
import android.app.AppOpsManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.Bridge;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.PermissionState;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.data.JPushConfig;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
@CapacitorPlugin(
        name = "JPush",
        permissions = {
                @Permission(
                        alias = JPushPlugin.LOCAL_NOTIFICATIONS,
                        strings = {
                                Manifest.permission.POST_NOTIFICATIONS
                        }
                ),
        })
public class JPushPlugin extends Plugin {
    static final String LOCAL_NOTIFICATIONS = "notifications";
    private static JPushPlugin instance;
    private static final String TAG = "JPushPlugin";
    private final JPush implementation = new JPush();


    static String notificationTitle;
    static String notificationAlert;
    static Map<String, Object> notificationExtras = new HashMap<String, Object>();

    static String openNotificationTitle;
    static String openNotificationAlert;
    static Map<String, Object> openNotificationExtras = new HashMap<String, Object>();

    BroadcastReceiver permissionReceiver;

    private static final String CHANNEL_ID = "channel_id";
    private static final String CHANNEL_NAME = "channel_name";
    private static final String CHANNEL_DESCRIPTION = "channel_description";

    private interface PermissionsCallback {
        void onPermissionGranted();

        void onPermissionDenied();
    }


    private void jPushInit(Context context, String appKey, String channel, boolean debugMode) {
        JPushInterface.setDebugMode(debugMode);
        JPushInterface.setChannel(context, channel);

        JPushConfig config = new JPushConfig();
        config.setjAppKey(appKey);
        JPushInterface.init(context, config);
        JPushInterface.setNotificationCallBackEnable(context, true);

        if (openNotificationAlert != null) {
            notificationAlert = null;
            transmitNotificationOpen(openNotificationTitle, openNotificationAlert, openNotificationExtras);
        }
        if (notificationAlert != null) {
            transmitNotificationReceive(notificationTitle, notificationAlert, notificationExtras);
        }
    }


    @Override
    public void load() {
        super.load();
        instance = this;

        String appKey = getConfig().getString("appKey");
        String channel = getConfig().getString("channel");
        boolean debugMode = getConfig().getBoolean("debugMode", true);
//        boolean isProduction = getConfig().getBoolean("isProduction", true);
        Log.d(TAG, "极光推送初始化" + appKey + "," + channel);

        Context context = getContext();
        jPushInit(context, appKey, channel, debugMode);
        Log.d("通知权限开启状态", "" + hasPushPermission());
        // 检查通知权限已开启
//        if (hasPushPermission()) {
//            jPushInit(context, appKey, channel, debugMode);
//        } else {
//            // 通知权限未开启，需要申请通知权限
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                requestNotificationPermission(context, new PermissionsCallback() {
//                    @Override
//                    public void onPermissionGranted() {
//                        // 申请通知权限成功，可以进行初始化
//                        Log.d("权限已开启", "" + hasPushPermission());
//                        jPushInit(context, appKey, channel, debugMode);
//                    }
//
//                    @Override
//                    public void onPermissionDenied() {
//                        // 用户拒绝了通知权限，无法进行初始化
//                        Log.d("权限已拒绝", "" + hasPushPermission());
//                    }
//                });
//            }
//        }
    }


    private void requestNotificationPermission(Context context, PermissionsCallback callback) {
        // 先清除之前的通知，避免干扰用户
        NotificationManagerCompat.from(context).cancelAll();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESCRIPTION);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.jpush_btn_bg_green_playable)
                    .setContentTitle("title")
                    .setContentText("content")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(1, builder.build());
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.jpush_btn_bg_green_playable)
                    .setContentTitle("title")
                    .setContentText("content")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(1, builder.build());
        }
//        NotificationManagerCompat.from(context).cancelAll();
//        Intent intent = new Intent();
//        intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
//        intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent);

        // 使用 BroadcastReceiver 监听通知权限的变化
        BroadcastReceiver permissionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("权限变化", "");
                if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                    // 用户已经允许通知权限
                    context.unregisterReceiver(this);
                    callback.onPermissionGranted();
                } else {
                    // 用户拒绝了通知权限
                    callback.onPermissionDenied();
                    Log.d("权限已拒绝", "" + hasPushPermission());
                    context.unregisterReceiver(this);
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.app.action.NOTIFICATION_POLICY_ACCESS_GRANTED_CHANGED");
        context.registerReceiver(permissionReceiver, filter);
    }

    @PluginMethod
    public void startJPush(PluginCall call) {
        String appKey = call.getString("appKey");
        String channel = call.getString("channel");
        boolean debugMode = call.getBoolean("debugMode", true);

        Context context = getContext();
        jPushInit(context, appKey, channel, debugMode);
        call.resolve();
    }

    @PluginMethod
    public void getRegistrationID(PluginCall call) {
        String value = JPushInterface.getRegistrationID(getContext());
        JSObject ret = new JSObject();
        ret.put("registrationId", value);
        call.resolve(ret);
    }

    @PluginMethod
    public void setDebugMode(PluginCall call) {
        boolean value = Boolean.TRUE.equals(call.getBoolean("debugMode"));
        JPushInterface.setDebugMode(value);
        call.resolve();
    }

    @PluginMethod
    public void setAlias(PluginCall call) {
        String alias = call.getString("alias");
        int sequence = call.hasOption("sequence") ? call.getInt("sequence", -1) : -1;
        JPushInterface.setAlias(getContext(), sequence, alias);
        call.resolve();
    }

    @PluginMethod
    public void deleteAlias(PluginCall call) {
        int sequence = call.getInt("sequence", -1);
        JPushInterface.deleteAlias(getContext(), sequence);
        call.resolve();
    }

    @PluginMethod
    public void setTags(PluginCall call) throws JSONException {
        JSArray tags = call.getArray("tags", JSArray.from(String.class));
        if (tags == null || tags.length() == 0) {
            call.reject("tags参数不能为空");
            return;
        }

        List<String> tagList = tags.toList();
        Set<String> tagSet = new HashSet<>(tagList);

        JPushInterface.setTags(getContext(), 0, tagSet);
        JSObject ret = new JSObject();
        ret.put("success", true);
        call.resolve(ret);
    }

    @PluginMethod
    public void deleteTags(PluginCall call) throws JSONException {
        JSArray tags = call.getArray("tags", JSArray.from(String.class));
        if (tags == null || tags.length() == 0) {
            call.reject("tags参数不能为空");
            return;
        }

        List<String> tagList = tags.toList();
        Set<String> tagSet = new HashSet<>(tagList);
        JPushInterface.deleteTags(getContext(), 0, tagSet);
        JSObject ret = new JSObject();
        ret.put("success", true);
        call.resolve(ret);
    }

    @PluginMethod
    public void cleanTags(PluginCall call) {
        JPushInterface.cleanTags(getContext(), 0);
        call.resolve();
    }

    @PluginMethod
    public void setBadgeNumber(PluginCall call) {
        int badge = call.getInt("badge", 0);
        JPushInterface.setBadgeNumber(getContext(), badge);
        call.resolve();

    }

    @PluginMethod
    public void checkPermissions(PluginCall call) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            JSObject permissionsResultJSON = new JSObject();
            permissionsResultJSON.put(LOCAL_NOTIFICATIONS, getNotificationPermissionText());
            call.resolve(permissionsResultJSON);
        } else {
            super.checkPermissions(call);
        }
    }

    @PluginMethod
    public void requestPermissions(PluginCall call) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            JSObject permissionsResultJSON = new JSObject();
            permissionsResultJSON.put(LOCAL_NOTIFICATIONS, getNotificationPermissionText());
            call.resolve(permissionsResultJSON);
        } else {
            if (getPermissionState(LOCAL_NOTIFICATIONS) != PermissionState.GRANTED) {
                requestPermissionForAlias(LOCAL_NOTIFICATIONS, call, "permissionsCallback");
            }
        }
    }

    @PermissionCallback
    private void permissionsCallback(PluginCall call) {
        JSObject permissionsResultJSON = new JSObject();
        permissionsResultJSON.put("notifications", getNotificationPermissionText());
        call.resolve(permissionsResultJSON);
    }

    @PluginMethod
    public void openNotificationSetting(PluginCall call) {
        Context context = getContext();
        NotificationManagerCompat.from(context).cancelAll();
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        call.resolve();
    }

    private String getNotificationPermissionText() {
        if (hasPushPermission()) {
            return "granted";
        } else {
            return "denied";
        }
    }

    // 检查是否有通知权限
    private boolean hasPushPermission() {
        Context context = getContext();
        if (Build.VERSION.SDK_INT >= 24) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            return notificationManager.areNotificationsEnabled();
        } else {
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            ApplicationInfo appInfo = context.getApplicationInfo();
            String pkg = context.getPackageName();
            int uid = appInfo.uid;
            try {
                Class<?> appOpsClass = Class.forName(AppOpsManager.class.getName());
                Method checkOpNoThrowMethod = appOpsClass.getMethod("checkOpNoThrow", int.class, int.class, String.class);
                Field opPostNotificationValue = appOpsClass.getDeclaredField("OP_POST_NOTIFICATION");
                int value = opPostNotificationValue.getInt(Integer.class);
                Object result = checkOpNoThrowMethod.invoke(appOpsManager, value, uid, pkg);
                assert result != null;
                return Integer.parseInt(result.toString()) == AppOpsManager.MODE_ALLOWED;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private static JSObject getMessageObject(String message, Map<String, Object> extras) {
        JSObject data = new JSObject();
        try {
            data.put("content", message);
            JSObject jExtras = new JSObject();
            for (Entry<String, Object> entry : extras.entrySet()) {
                if (entry.getKey().equals(JPushInterface.EXTRA_EXTRA)) {
                    JSONObject jo;
                    if (TextUtils.isEmpty((String) entry.getValue())) {
                        jo = new JSONObject();
                    } else {
                        jo = new JSONObject((String) entry.getValue());
                        String key;
                        Iterator<String> keys = jo.keys();
                        while (keys.hasNext()) {
                            key = keys.next().toString();
                            jExtras.put(key, jo.getString(key));
                        }
                    }
                    jExtras.put(JPushInterface.EXTRA_EXTRA, jo);
                } else {
                    jExtras.put(entry.getKey(), entry.getValue());
                }
            }
            if (jExtras.length() > 0) {
                data.put("rawData", jExtras);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    private static JSObject getNotificationObject(String title, String alert, Map<String, Object> extras) {
        JSObject data = new JSObject();
        try {
            data.put("title", title);
            data.put("content", alert);
            JSObject jExtras = new JSObject();
            for (Entry<String, Object> entry : extras.entrySet()) {
                if (entry.getKey().equals(JPushInterface.EXTRA_EXTRA)) {
                    JSONObject jo;
                    if (TextUtils.isEmpty((String) entry.getValue())) {
                        jo = new JSONObject();
                    } else {
                        jo = new JSONObject((String) entry.getValue());
                        String key;
                        Iterator<String> keys = jo.keys();
                        while (keys.hasNext()) {
                            key = keys.next().toString();
                            jExtras.put(key, jo.getString(key));
                        }
                    }
                    jExtras.put(JPushInterface.EXTRA_EXTRA, jo);
                } else {
                    jExtras.put(entry.getKey(), entry.getValue());
                }
            }
            if (jExtras.length() > 0) {
                data.put("rawData", jExtras);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 自定义通知
     */
    static void transmitMessageReceive(String message, Map<String, Object> extras) {
        if (instance == null) {
            return;
        }
        JSObject data = getMessageObject(message, extras);
        instance.notifyListeners("messageReceived", data);
    }

    /**
     * 消息点击
     */
    static void transmitNotificationOpen(String title, String alert, Map<String, Object> extras) {
        if (instance == null) {
            return;
        }
        JSObject data = getNotificationObject(title, alert, extras);
        instance.notifyListeners("notificationOpened", data);
        JPushPlugin.openNotificationTitle = null;
        JPushPlugin.openNotificationAlert = null;
    }

    /**
     * 收到消息推送
     */
    static void transmitNotificationReceive(String title, String alert, Map<String, Object> extras) {
        if (instance == null) {
            return;
        }
        JSObject data = getNotificationObject(title, alert, extras);
        instance.notifyListeners("notificationReceived", data);
        JPushPlugin.notificationTitle = null;
        JPushPlugin.notificationAlert = null;
    }

    static void transmitReceiveRegistrationId(String rId) {
        if (instance == null) {
            return;
        }
        JSObject data = new JSObject();
        data.put("registrationId", rId);
        instance.notifyListeners("receiveRegistrationId", data);
    }


}
