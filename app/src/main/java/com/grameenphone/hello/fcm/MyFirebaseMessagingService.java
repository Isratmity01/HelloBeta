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
import com.google.gson.Gson;
import com.grameenphone.hello.Activities.MainActivity;
import com.grameenphone.hello.R;
import com.grameenphone.hello.Utils.Constant;
import com.grameenphone.hello.dbhelper.DatabaseHelper;
import com.grameenphone.hello.events.PushNotificationEvent;
import com.grameenphone.hello.model.Chat;
import com.grameenphone.hello.model.FileModel;
import com.grameenphone.hello.model.NotificationModel;

import org.greenrobot.eventbus.EventBus;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    EventBus eventBus;
    String ReceivedRoomId;
    int count = 1;
    private DatabaseHelper databaseHelper;
    private int Unique_Integer_Number;


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


            String type = remoteMessage.getData().get("type");
            String fcmToken = remoteMessage.getData().get("fcm_token");

            String roomid = remoteMessage.getData().get("room_uid");
            String chats = remoteMessage.getData().get("whatposted");

            if(type == null ) type = "undefined";


            if(type.contains("p2p")){

                if(chats != null) {

                    Gson gson = new Gson();
                    Chat staff = gson.fromJson(chats, Chat.class);


                    if (staff.getType() == null) {

                        Chat receivedchat = new Chat(staff.getSender(), staff.getReceiver(),
                                staff.getSenderUid(), staff.getReceiverUid(),
                                staff.getMessage(),
                                staff.getTimestamp(), staff.getMessageType());

                        databaseHelper.addMessage(roomid ,receivedchat, receivedchat.getChatId(), receivedchat.getReadStatus());


                    } else {
                        FileModel fileModel = new FileModel(staff.getType(), staff.getUrl_file(), staff.getName_file(), staff.getSize_file());
                        Chat receivedchat2 = new Chat(staff.getSender(), staff.getReceiver(),
                                staff.getSenderUid(), staff.getReceiverUid(), staff.getPhotoUrl(), "Image", staff.getTimestamp()
                                , fileModel, staff.getType());
                        databaseHelper.addMessage(roomid ,receivedchat2, receivedchat2.getChatId(), receivedchat2.getReadStatus());


                    }

                    String message = "";
                    String title = staff.getSender();
                    if(staff.getMessageType().contains("txt")) {
                        message = staff.getSender() + " ম্যাসেজ দিয়েছেন";
                    } else if( staff.getMessageType().contains("stk") ){
                        message = staff.getSender() + " স্টিকার পাঠিয়েছেন";
                    } else {
                        message = staff.getSender() + " ছবি পাঠিয়েছেন";
                    }

                    sendNotification(
                            "p2p",
                            title,
                            message,
                            staff.getSender(),
                            fcmToken,
                            roomid);


                }
            }

            if(type.contains("bot")){

                String sender = remoteMessage.getData().get("sender");
                String title = remoteMessage.getData().get("title");
                String text = remoteMessage.getData().get("text");


                sendNotification("bot",
                        title,
                        text,
                        sender,
                        fcmToken,
                        roomid);


            }



            if(type.contains("request")){

                String data = remoteMessage.getData().get("whatposted");
                Gson gsons = new Gson();
                NotificationModel notiData = gsons.fromJson(data,NotificationModel.class);

                String sender = notiData.getSender();
                String title = notiData.getTitle();
                String text = notiData.getText();


                sendNotification("request",
                        title,
                        text,
                        sender,
                        fcmToken,
                        roomid);


            }






        }
    }


    /**
     * Create and show a simple notification containing the received FCM message.
     */
    private void sendNotification(String type,
                                  String title,
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
        intent.putExtra("type", type);

        intent.putExtra(Constant.Notification.ARG_FIREBASE_TOKEN, firebaseToken);

        intent.putExtra("room_uid", roomuid);
        intent.putExtra("room_name", title);


        if(roomuid != null) {
            Unique_Integer_Number = roomuid.hashCode();
        } else if ( type.contains( "bot" ) ){
            Unique_Integer_Number = 0;
        } else {
            Unique_Integer_Number = 1;
        }


        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, Unique_Integer_Number, intent,
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


        notificationManager.notify( Unique_Integer_Number, notificationBuilder.build());
    }






}