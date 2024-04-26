package com.capacitor.jpush;

import android.app.Notification;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageService;
public class JPushMessageReceiver extends JPushMessageService{

//    @Override
//    public void onMessage(Context context, CustomMessage message) {
//        super.onMessage(context, message);
//
//        Log.d("JPush Message Received", "" + message);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
////            JPushPlugin.transmitMessageReceive(message.notificationContent, message.notificationExtras);
//        }
//    }
//
    @Override
    public Notification getNotification(Context context, NotificationMessage message){
        Log.d("JPush getNotification", "" + JPushPlugin.instance);
         JPushPlugin.notificationReceived(message.notificationTitle, message.notificationContent, message.notificationExtras);
         return null;
    }

    public void onRegister(Context context, String rid) {
        Log.d("JPush onRegister", "" + rid);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            JPushPlugin.transmitReceiveRegistrationId(rid);
        }
    }
}
