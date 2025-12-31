package com.capacitor.jpush;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import androidx.core.app.NotificationManagerCompat;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.data.JPushConfig;

import com.getcapacitor.Bridge;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginHandle;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;

import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.NotificationMessage;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.json.JSONException;

@CapacitorPlugin(
        name = "JPush",
        permissions = {@Permission(alias = JPushPlugin.LOCAL_NOTIFICATIONS, strings = {Manifest.permission.POST_NOTIFICATIONS})}
)
public class JPushPlugin extends Plugin {

    private final JPush jPushTools = new JPush();

    public static Bridge staticBridge = null;

    static final String LOCAL_NOTIFICATIONS = "permission";
    private static final String TAG = "JPushPlugin";


    private void jPushInit(PluginCall call) {
        JSObject ret = new JSObject();

        try {
            Context context = getContext();
            String appKey = getConfig().getString("appKey", "");
            String channel = getConfig().getString("channel", "");
            boolean debugMode = getConfig().getBoolean("debugMode", true);
            if (Objects.equals(appKey, "")) {
                ret.put("success", false);
                ret.put("message", "请在 capacitor.config.ts(capacitor.config.json) 中配置 appKey");
                call.reject("极光推送初始化失败", "0", ret);
                return;
            }
            JPushInterface.setDebugMode(debugMode);
            JPushInterface.setChannel(context, channel);
            JLogger.d(TAG, "[极光推送初始化] " + appKey + "," + channel);

            JPushConfig config = new JPushConfig();
            config.setjAppKey(appKey);
            JPushInterface.init(context, config);
            JPushInterface.setNotificationCallBackEnable(context, true);

            ret.put("success", false);
            call.resolve(ret);
        } catch (Exception e) {
            JLogger.e(TAG, "[极光推送初始化异常] " + e);
            ret.put("success", false);
            ret.put("message", e.getMessage());
            call.reject("极光推送初始化失败", "0", ret);
        }

    }

    @Override
    public void load() {
        super.load();
        staticBridge = this.bridge;
    }

    @PluginMethod
    public void startJPush(PluginCall call) {
        this.jPushInit(call);
    }

    @PluginMethod
    public void getRegistrationID(PluginCall call) {
        try {
            String value = JPushInterface.getRegistrationID(getContext());
            JSObject ret = new JSObject();
            ret.put("registrationId", value);
            call.resolve(ret);
        } catch (Exception e) {
            JLogger.e(TAG, "[获取RegistrationID异常] " + e);
            call.reject("获取RegistrationID失败", "0");
        }
    }

    @PluginMethod
    public void setDebugMode(PluginCall call) {
        boolean value = Boolean.TRUE.equals(call.getBoolean("debugMode"));
        JPushInterface.setDebugMode(value);
        call.resolve();
    }

    @PluginMethod
    public void setAlias(PluginCall call) {
        try {
            String alias = call.getString("alias");
            JPushInterface.setAlias(getContext(), jPushTools.sequenceNumber(call), alias);
            JSObject ret = new JSObject();
            ret.put("success", true);
            call.resolve(ret);
        } catch (Exception e) {
            JLogger.e(TAG, "[设置别名异常] " + e);
            call.reject("设置别名失败", "0");
        }

    }

    @PluginMethod
    public void deleteAlias(PluginCall call) {
        JPushInterface.deleteAlias(getContext(), jPushTools.sequenceNumber(call));
        JSObject ret = new JSObject();
        ret.put("success", true);
        call.resolve(ret);
    }

    @PluginMethod
    public void setTags(PluginCall call) throws JSONException {
        JSArray tags = jPushTools.getTagsValue(call);
        List<String> tagList = tags.toList();
        Set<String> tagSet = new HashSet<>(tagList);

        JPushInterface.setTags(getContext(), 0, tagSet);
        JSObject ret = new JSObject();
        ret.put("success", true);
        call.resolve(ret);
    }

    @PluginMethod
    public void deleteTags(PluginCall call) throws JSONException {
        JSArray tags = jPushTools.getTagsValue(call);
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
        JSObject ret = new JSObject();
        ret.put("success", true);
        call.resolve(ret);
    }

    @PluginMethod
    public void setBadgeNumber(PluginCall call) {
        Integer badge = call.getInt("badge", 0);
        JPushInterface.setBadgeNumber(getContext(), badge != null ? badge : 0);
        JSObject ret = new JSObject();
        ret.put("success", true);
        call.resolve(ret);
    }

    @PluginMethod
    public void checkPermissions(PluginCall call) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            JSObject permissionsResultJSON = new JSObject();
            permissionsResultJSON.put(LOCAL_NOTIFICATIONS, "granted");
            call.resolve(permissionsResultJSON);
        } else {
            super.checkPermissions(call);
        }
    }

    @PluginMethod
    public void requestPermissions(PluginCall call) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            JSObject permissionsResultJSON = new JSObject();
            permissionsResultJSON.put(LOCAL_NOTIFICATIONS, "granted");
            call.resolve(permissionsResultJSON);
        } else {
            if (getPermissionState(LOCAL_NOTIFICATIONS) != PermissionState.GRANTED) {
                requestPermissionForAlias(LOCAL_NOTIFICATIONS, call, "permissionsCallback");
            }
        }
    }

    @PermissionCallback
    private void permissionsCallback(PluginCall call) {
        this.checkPermissions(call);
    }

    @PluginMethod
    public void openNotificationSetting(PluginCall call) {
        Context context = getContext();
        NotificationManagerCompat.from(context).cancelAll();
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        call.resolve();
    }


    static void transmitReceiveRegistrationId(String rId) {
        JPushPlugin jPushInstance = JPushPlugin.getJPushInstance();
        if (jPushInstance != null) {
            JSObject data = new JSObject();
            data.put("registrationId", rId);
            jPushInstance.notifyListeners("receiveRegistrationId", data);
        }
    }

    public static JPushPlugin getJPushInstance() {
        if (staticBridge != null && staticBridge.getWebView() != null) {
            PluginHandle handle = staticBridge.getPlugin("JPush");
            if (handle == null) {
                return null;
            }
            return (JPushPlugin) handle.getInstance();
        }
        return null;
    }

    public static void handleNotificationListener(String eventName, Object message) {
        try {
            String title = "";
            String content = "";
            String extraInfo = "";

            JLogger.e(TAG, "[handleNotificationListener] " + eventName + " " + message);

            JSObject extraObj = new JSObject();
            // 反射获取所有字段
            for (java.lang.reflect.Field field : message.getClass().getFields()) {
                field.setAccessible(true);
                try {
                    Object value = field.get(message);
                    extraObj.put(field.getName(), value);
                } catch (Exception ignored) {}
            }
            // 兼容 private 字段
            for (java.lang.reflect.Field field : message.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    Object value = field.get(message);
                    extraObj.put(field.getName(), value);
                } catch (Exception ignored) {}
            }

            if (message instanceof CustomMessage customMsg) {
                title = customMsg.title;
                content = customMsg.message;
                extraInfo = customMsg.extra;
            } else if (message instanceof NotificationMessage notifyMsg) {
                title = notifyMsg.notificationTitle;
                content = notifyMsg.notificationContent;
                extraInfo = notifyMsg.notificationExtras;
            }

            // 合并原有 extraInfo
            if (extraInfo != null && !extraInfo.isEmpty()) {
                try {
                    JSObject originExtra = new JSObject(extraInfo);
                    for (Iterator<String> it = originExtra.keys(); it.hasNext(); ) {
                        String key = it.next();
                        extraObj.put(key, originExtra.get(key));
                    }
                } catch (Exception ignored) {}
            }

            JSObject data = new JSObject();
            data.put("title", title);
            data.put("content", content);
            JSObject rowDataObj = new JSObject();
            rowDataObj.put("extra", extraObj);
            data.put("rawData", rowDataObj);


            JPushPlugin jPushInstance = JPushPlugin.getJPushInstance();
            if (jPushInstance != null) {
                jPushInstance.notifyListeners(eventName, data);
            }
        } catch (Exception e) {
            JLogger.e(TAG, "[获取推送通知结果出错] " + e);
        }
    }
}
