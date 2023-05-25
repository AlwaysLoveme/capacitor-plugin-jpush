package com.capacitor.jpush;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.data.JPushConfig;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
@CapacitorPlugin(
    name = "JPush",
    permissions = { @Permission(alias = JPushPlugin.LOCAL_NOTIFICATIONS, strings = { Manifest.permission.POST_NOTIFICATIONS }) }
)
public class JPushPlugin extends Plugin {

    static final String LOCAL_NOTIFICATIONS = "permission";
    private static JPushPlugin instance;
    private static final String TAG = "JPushPlugin";

    static String notificationTitle;
    static String notificationAlert;
    static Map<String, Object> notificationExtras = new HashMap<String, Object>();

    static String openNotificationTitle;
    static String openNotificationAlert;
    static Map<String, Object> openNotificationExtras = new HashMap<String, Object>();

    private void jPushInit(PluginCall call) {
        Context context = getContext();
        String appKey = getConfig().getString("appKey", "");
        String channel = getConfig().getString("channel", "");
        boolean debugMode = getConfig().getBoolean("debugMode", true);
        if (Objects.equals(appKey, "")) {
            call.reject("请在 capacitor.config.ts(capacitor.config.json) 中配置 appKey");
            return;
        }
        JPushInterface.setDebugMode(debugMode);
        JPushInterface.setChannel(context, channel);
        Log.d(TAG, "极光推送初始化" + appKey + "," + channel);

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

        call.resolve();
    }

    @Override
    public void load() {
        super.load();
        instance = this;
    }

    @PluginMethod
    public void startJPush(PluginCall call) {
        jPushInit(call);
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
        intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        call.resolve();
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
