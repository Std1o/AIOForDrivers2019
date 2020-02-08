package com.beerdelivery.driver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;
import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.Style;
import com.beerdelivery.driver.helper.PrefManager;
import com.beerdelivery.driver.helper.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.beerdelivery.driver.app.AppController.TAG;

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
        Log.d(TAG, "Message data payload: " + remoteMessage.getData());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Map data = remoteMessage.getData();
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            Log.d(TAG, "Message data payload: " + remoteMessage.getData().get("type"));
            if (remoteMessage.getData().get("type").equals("info")) createNotification("Диспетчерская:", remoteMessage.getData().get("body"));

            if (remoteMessage.getData().get("type").equals("robotOrder")) getFreeOrders(remoteMessage.getData().get("orderID"));
            if (remoteMessage.getData().get("type").equals("mes")) {
                if (!ChatActivity.isChatOpened) {
                    createNotification("Диспетчерская:", remoteMessage.getData().get("body"));
                }
            }

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
        Context context = getBaseContext();
        System.out.println("Message data payload " + body);

        int notificationCode = 378;
        NotificationManager notificationManager;;

        Uri defaultRingtone = null;
        defaultRingtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("BeerDelivery",
                    "Beer Delivery",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Channel description");
            notificationManager.createNotificationChannel(channel);
            NotificationCompat.Builder notificationCompat = new NotificationCompat.Builder(context, "BeerDelivery")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setDefaults(Notification.DEFAULT_SOUND).setAutoCancel(true)
                    .setContentText(body)
                    .setContentIntent(pendingIntent)
                    .setSound(defaultRingtone)
                    .setContentTitle(getString(R.string.app_name))
                    .setAutoCancel(true)
                    .setContentText(body);
            notificationManager.notify(notificationCode, notificationCompat.build());
        } else {
            notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            NotificationCompat.Builder notificationCompat = new NotificationCompat.Builder(context, "BeerDelivery")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setSound(defaultRingtone)
                    .setContentIntent(pendingIntent)
                    .setContentTitle(getString(R.string.app_name))
                    .setAutoCancel(true)
                    .setContentText(body);
            notificationManager.notify(notificationCode, notificationCompat.build());
        }
    }

    private void getFreeOrders(final String orderId) {

        RequestQueue queue = Volley.newRequestQueue(this);

        int intNow = 1;
        int intAdvance = 0;

        PrefManager pref = new PrefManager(FCMService.this);

        String url = pref.getCityUrl() + Urls.GET_FREE_ORDERS_URL;


        Map<String, String> map = new HashMap<>();

        map.put("command", "getFreeOrders");
        map.put("driverId", pref.getDriverId());
        map.put("hash", pref.getHash());
        map.put("now", intNow + "");
        map.put("advance", intAdvance + "");

        //    Log.e("666", "Autorize - " + map + "\n" + url);


        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url,

                new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("st");

                            if (status.equals("0")) {
                                JSONArray jArr = response.getJSONArray("orders");
                                for (int i = 0; i < jArr.length(); i++) {
                                    JSONObject obj = jArr.getJSONObject(i);

                                    System.out.println(obj);

                                    if (obj.getString("orderID").equals(orderId)) {
                                        robotDialog.time = obj.getString("orderTime");
                                        robotDialog.price = obj.getString("orderTarif");
                                        robotDialog.from = obj.getString("orderPlace");
                                        robotDialog.toAddress = obj.getString("orderRoute");
                                        robotDialog.info = obj.getString("orderInfo");
                                        robotDialog.orderId = obj.getString("orderID");
                                        robotDialog.orderPrice = obj.getString("stzakaz");
                                        robotDialog.textTariff = obj.getString("tarif");

                                        String[] coords_store;
                                        String[] coords_client;

                                        coords_store = obj.getString("coords").split(",");
                                        coords_client = obj.getString("coords_2").split(",");

                                        double originLatitude = Double.parseDouble(coords_store[0]);
                                        double originLongitude = Double.parseDouble(coords_store[1]);

                                        double destinationLatitude = Double.parseDouble(coords_client[0]);
                                        double destinationLongitude = Double.parseDouble(coords_client[1]);

                                        robotDialog.origin = new LatLng(originLatitude, originLongitude);
                                        robotDialog.destination = new LatLng(destinationLatitude, destinationLongitude);

                                        Intent intent = new Intent(FCMService.this, robotDialog.class);
                                        intent.putExtra("orderId", orderId);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                        startActivity(intent);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("666", "Autorize - " + e);
                            Toast.makeText(getApplicationContext(), "Ошибка связи с сервером", Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Ошибка связи с сервером", Toast.LENGTH_SHORT).show();
                        Log.e("666", "Autorize - " + error);
                    }
                }) {
            @Override

            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();//
                // headers.put("Content-Type", "text/html; charset=utf-8");
                headers.put("User-agent", "Motolife Linux Android");
                return headers;
            }
        };
        queue.add(postRequest);
    }
}
