package com.grameenphone.hello.fcm;

import android.util.Log;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class FcmNotificationBuilder {
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String TAG = "FcmNotificationBuilder";
    private static final String SERVER_API_KEY = "AAAAIGkI13Q:APA91bFgVNk-R7gse_e3ERpumy8IDJQZDVsbmbSOlPQMMA0yNMq8pB1wtwbJ9Lo8pJ5CM2IeUgZ573JBkLlV2M-T2TWVqyEJ4a6cg7tpjBGziOxbWEsGjrpuwgO7Q4GGNae7QgYUD9WN";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String AUTHORIZATION = "Authorization";
    private static final String AUTH_KEY = "key=" + SERVER_API_KEY;
    private static final String FCM_URL = "https://fcm.googleapis.com/fcm/send";
    // json related keys
    private static final String KEY_TO = "to";
    private static final String KEY_NOTIFICATION = "notification";
    private static final String KEY_TITLE = "title";
    private static final String KEY_TEXT = "text";
    private static final String KEY_DATA = "data";
    private static final String KEY_FCM_TOKEN = "fcm_token";
    private static final String KEY_ROOM = "room_uid";
    private static final String KEY_NOTIFICATION_TYPE = "type";

    private static final String KEY_CHATS ="whatposted";

    private String mTitle;
    private JSONObject received;
    private String mMessage;
    private String mFirebaseToken;
    private String mReceiverFirebaseToken;
    private String Sender;
    private String roomUid;

    private String notificationtype;

    private FcmNotificationBuilder() {

    }

    public static FcmNotificationBuilder initialize() {
        return new FcmNotificationBuilder();
    }


    public FcmNotificationBuilder setReceived(JSONObject received) {
        this.received = received;
        return this;
    }

    public FcmNotificationBuilder title(String title) {
        mTitle = title;
        return this;
    }

    public FcmNotificationBuilder message(String message) {
        mMessage = message;
        return this;
    }


    public FcmNotificationBuilder sender(String sender) {
        Sender = sender;
        return this;
    }

    public FcmNotificationBuilder firebaseToken(String firebaseToken) {
        mFirebaseToken = firebaseToken;
        return this;
    }

    public FcmNotificationBuilder receiverFirebaseToken(String receiverFirebaseToken) {
        mReceiverFirebaseToken = receiverFirebaseToken;
        return this;
    }

    public FcmNotificationBuilder roomUid(String roomid) {
        roomUid = roomid;
        return this;
    }

    public FcmNotificationBuilder notificationType(String type){
        notificationtype = type;
        return this;
    }


    public void send() {
        RequestBody requestBody = null;
        try {
            requestBody = RequestBody.create(MEDIA_TYPE_JSON, getValidJsonBody().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.Builder()
                .addHeader(CONTENT_TYPE, APPLICATION_JSON)
                .addHeader(AUTHORIZATION, AUTH_KEY)
                .url(FCM_URL)
                .post(requestBody)
                .build();

        Call call = new OkHttpClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e(TAG, "onGetAllUsersFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                Log.e(TAG, "onResponse: " + response.body().string());
            }

        });
    }

    private JSONObject getValidJsonBody() throws JSONException {
        JSONObject jsonObjectBody = new JSONObject();
        jsonObjectBody.put(KEY_TO, mReceiverFirebaseToken);

        JSONObject jsonObjectData = new JSONObject();
        jsonObjectData.put(KEY_FCM_TOKEN, mFirebaseToken);
        jsonObjectData.put(KEY_NOTIFICATION_TYPE, notificationtype);
        jsonObjectData.put(KEY_ROOM, roomUid);


        jsonObjectData.put(KEY_CHATS, received);
        jsonObjectBody.put(KEY_DATA, jsonObjectData);
        return jsonObjectBody;
    }

}