package com.capacitor.jpush;

import android.app.Notification;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageService;

public class JPushMessageReceiver extends JPushMessageService {

    // 收到自定义通知回调
    @Override
    public void onMessage(Context context, CustomMessage message) {
        super.onMessage(context, message);
        Log.d("JPush Message Received", "" + message);
        JPushPlugin.handleNotificationListener("notificationReceived", message.title, message.message, message.extra);
    }

    // 收到通知回调
    @Override
    public void onNotifyMessageArrived(Context context, NotificationMessage message) {
        Log.d("JPush getNotification", "" + JPushPlugin.instance);
        JPushPlugin.handleNotificationListener("notificationReceived", message.notificationTitle, message.notificationContent, message.notificationExtras);
    }

    // 点击通知回调
    @Override
    public void onNotifyMessageOpened(Context context, NotificationMessage message) {
        super.onNotifyMessageOpened(context, message);
        Log.d("Notification Clicked", "" + message);
        JPushPlugin.handleNotificationListener("notificationOpened", message.notificationTitle, message.notificationContent, message.notificationExtras);
    }

    public void onRegister(Context context, String rid) {
        Log.d("JPush onRegister", "" + rid);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            JPushPlugin.transmitReceiveRegistrationId(rid);
        }
    }
}
