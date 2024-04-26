package com.capacitor.jpush;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageService;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JPushEventReceiver extends JPushMessageService {

    private static final String TAG = JPushEventReceiver.class.getSimpleName();

    @Override
    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onTagOperatorResult(context, jPushMessage);
        JLogger.d(TAG, "onTagOperatorResult:" + jPushMessage);

        tryCallback(
            jPushMessage,
            new SuccessCallback() {
                @Override
                public void onSuccessCallback(JSONObject resultJson) throws JSONException {
                    Set<String> tags = jPushMessage.getTags();
                    JSONArray tagsJsonArr = new JSONArray();
                    for (String tag : tags) {
                        tagsJsonArr.put(tag);
                    }
                    if (tagsJsonArr.length() != 0) {
                        resultJson.put("tags", tagsJsonArr);
                    }
                }
            }
        );
    }

    @Override
    public void onCheckTagOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onCheckTagOperatorResult(context, jPushMessage);

        JLogger.d(TAG, "onCheckTagOperatorResult:" + jPushMessage);

        tryCallback(
            jPushMessage,
            new SuccessCallback() {
                @Override
                public void onSuccessCallback(JSONObject resultJson) throws JSONException {
                    resultJson.put("tag", jPushMessage.getCheckTag());
                    resultJson.put("isBind", jPushMessage.getTagCheckStateResult());
                }
            }
        );
    }

    @Override
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onAliasOperatorResult(context, jPushMessage);

        JLogger.d(TAG, "onAliasOperatorResult:" + jPushMessage);

        tryCallback(
            jPushMessage,
            new SuccessCallback() {
                @Override
                public void onSuccessCallback(JSONObject resultJson) throws JSONException {
                    if (!TextUtils.isEmpty(jPushMessage.getAlias())) {
                        resultJson.put("alias", jPushMessage.getAlias());
                    }
                }
            }
        );
    }

    @Override
    public void onRegister(Context context, String regId) {
        JLogger.d(TAG, "onRegister:" + regId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            JPushPlugin.transmitReceiveRegistrationId(regId);
        }
    }

    @Override
    public void onMessage(Context context, CustomMessage customMessage) {
        super.onMessage(context, customMessage);
        Log.e(TAG, "接收到消息:" + customMessage);
        //        String msg = customMessage.message;//intent.getStringExtra(JPushInterface.EXTRA_MESSAGE);
        //        Map<String, Object> extras = getNotificationExtras(intent);
        //        JPushPlugin.transmitMessageReceive(msg, extras);
    }

    @Override
    public void onNotifyMessageArrived(Context context, NotificationMessage notificationMessage) {
        super.onNotifyMessageArrived(context, notificationMessage);

        JLogger.d(TAG, "onNotifyMessageArrived:" + notificationMessage);
    }

    @Override
    public void onNotifyMessageOpened(Context context, NotificationMessage notificationMessage) {
        super.onNotifyMessageOpened(context, notificationMessage);
        JLogger.d(TAG, "onNotifyMessageOpened:" + notificationMessage);
    }

    @Override
    public void onMobileNumberOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onMobileNumberOperatorResult(context, jPushMessage);
        JLogger.d(TAG, "onMobileNumberOperatorResult:" + jPushMessage);

        tryCallback(
            jPushMessage,
            new SuccessCallback() {
                @Override
                public void onSuccessCallback(JSONObject resultJson) throws JSONException {
                    if (!TextUtils.isEmpty(jPushMessage.getMobileNumber())) {
                        resultJson.put("mobileNumber", jPushMessage.getMobileNumber());
                    }
                }
            }
        );
    }

    @Override
    public void onMultiActionClicked(Context context, Intent intent) {
        super.onMultiActionClicked(context, intent);
        JLogger.d(TAG, "onMultiActionClicked:" + intent);
    }

    interface SuccessCallback {
        void onSuccessCallback(JSONObject resultJson) throws JSONException;
    }

    public void tryCallback(JPushMessage jPushMessage, SuccessCallback successCallback) {
        JSONObject resultJson = new JSONObject();
        Log.d("消息推送", "" + jPushMessage);

        int sequence = jPushMessage.getSequence();
        try {
            resultJson.put("sequence", sequence);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //        CallbackContext callback = JPushPlugin.eventCallbackMap.get(sequence);

        //        if (callback == null) {
        //            Logger.i(TAG, "Unexpected error, callback is null!");
        //            return;
        //        }

        if (jPushMessage.getErrorCode() == 0) {
            try {
                successCallback.onSuccessCallback(resultJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //            callback.success(resultJson);

        } else {
            try {
                resultJson.put("code", jPushMessage.getErrorCode());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //            callback.error(resultJson);
        }
        //        JPushPlugin.eventCallbackMap.remove(sequence);

    }
}
