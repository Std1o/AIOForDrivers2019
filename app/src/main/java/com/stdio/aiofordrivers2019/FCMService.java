package com.stdio.aiofordrivers2019;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static com.stdio.aiofordrivers2019.app.AppController.TAG;

public class FCMService extends FirebaseMessagingService {

    public static final int MESSAGE_NOTIFICATION_ID = 435345;

    public FCMService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        System.out.println("AAAAAAAAAAAAAAAAAAA");
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            Log.d(TAG, "Message data payload: " + remoteMessage.getMessageType());

            if (remoteMessage.getMessageType().equals("info")) createNotification("Диспетчерская:", remoteMessage.getMessageType());

            if (remoteMessage.getMessageType().equals("robotOrder")) robotDialog(remoteMessage.getMessageType(), "data.getString(orderID)");

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    // Creates notification based on title and body received
    private void createNotification(String title, String body) {
        String bigText = body;

        Context context = getBaseContext();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_logo_notif).setContentTitle(title)
                .setDefaults(Notification.DEFAULT_SOUND).setAutoCancel(true)
                .setContentText(body);

        Notification notification = new NotificationCompat.BigTextStyle(mBuilder)
                .bigText(bigText).build();

        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());
    }


    private void robotDialog(String orderInfo, String orderId){

        Intent intent = new Intent(FCMService.this, robotDialog.class);
        intent.putExtra("orderInfo", orderInfo);
        intent.putExtra("orderId", orderId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
    }
}
