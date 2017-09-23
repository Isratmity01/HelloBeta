package com.grameenphone.hello.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.grameenphone.hello.Activities.MainActivity;
import com.grameenphone.hello.R;
import com.grameenphone.hello.Utils.Constant;
import com.grameenphone.hello.dbhelper.DatabaseHelper;
import com.grameenphone.hello.events.PushNotificationEvent;

import org.greenrobot.eventbus.EventBus;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    EventBus eventBus;
    String ReceivedRoomId;
    int count = 1;
    private DatabaseHelper databaseHelper;


    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        eventBus = EventBus.getDefault();
        databaseHelper = new DatabaseHelper(getApplicationContext());
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());


        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());


            String title = remoteMessage.getData().get("title");
            String message = remoteMessage.getData().get("text");
            String sender = remoteMessage.getData().get("sender");
            String fcmToken = remoteMessage.getData().get("fcm_token");
            String roomid = remoteMessage.getData().get("room_uid");


            sendNotification(title,
                    message,
                    sender,
                    fcmToken,
                    roomid);


        }
    }


    /**
     * Create and show a simple notification containing the received FCM message.
     */
    private void sendNotification(String title,
                                  String message,
                                  String sender,
                                  String firebaseToken,
                                  String roomuid) {


        EventBus.getDefault().post(new PushNotificationEvent(title,
                message,
                sender,
                firebaseToken,
                roomuid));


        Intent intent;
        intent = new Intent(this, MainActivity.class);
        intent.putExtra("room_type", "p2p");

        intent.putExtra(Constant.Notification.ARG_FIREBASE_TOKEN, firebaseToken);

        intent.putExtra("room_uid", roomuid);
        intent.putExtra("room_name", title);


        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        notificationBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }






}