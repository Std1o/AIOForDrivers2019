package com.stdio.aiofordrivers2019;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;


import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


import com.stdio.aiofordrivers2019.app.AppController;
import com.stdio.aiofordrivers2019.app.CustomRequest;
import com.stdio.aiofordrivers2019.helper.PrefManager;
import com.stdio.aiofordrivers2019.helper.Urls;

public class robotDialog extends Activity {

    String IdOrderToBuy;
    RequestQueue queue;
    private PrefManager pref;
    private ProgressDialog pDialog;
    AlertDialog dlg;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playDefaultNotificationSound();

        pref = new PrefManager(this);
        queue = Volley.newRequestQueue(this);
        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Отправка запроса...");

        Intent intent = getIntent();
        String text = "";
        if (intent.hasExtra("orderInfo")) text = intent.getStringExtra("orderInfo");


        if (intent.getExtras().getString("orderId").equals("0")) {
            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();


        } else {
            IdOrderToBuy = intent.getExtras().getString("orderId");


        }




        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Диспетчерская");
        alert.setIcon(android.R.drawable.ic_dialog_info);
        alert.setMessage(text);


        if (!intent.getExtras().getString("orderId").equals("0")) {

            alert.setPositiveButton("Взять заказ", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    takeOrderFromDriver(IdOrderToBuy);

                    robotDialog.this.finish();
                }
            });


        }


        alert.setNegativeButton("Ознакомлен", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                robotDialog.this.finish();
            }
        });


          dlg = alert.create();

          dlg.show();

    }


    private void playDefaultNotificationSound() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();
    }


    private void takeOrderFromDriver(final String order) {


        String url = pref.getCityUrl() + Urls.TAKE_ORDER_URL;


        Map<String, String> map = new HashMap<>();


        map.put("driverId", pref.getDriverId());
        map.put("hash", pref.getHash());
        map.put("order", order);

        map.put("command", "takeOrderFromDriver");


        Log.e("666", "Autorize - " + map + "\n" + url);


        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url,

                new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //          Log.e("666", "Autorize - " + response);
                        try {
                            String status = response.getString("st");



                            if (status.equals("0")) {
                                Toast.makeText(getBaseContext(),
                                        response.getString("message"),
                                        Toast.LENGTH_SHORT).show();

                            }
                            if (status.equals("-1")) {

                                Toast.makeText(getBaseContext(),
                                        response.getString("message"),
                                        Toast.LENGTH_SHORT).show();
                            }


                            // ===========
                            if (status.equals("2")) {

                            }

                            if (status.equals("3")) {


                            }

                            if (status.equals("4")) {


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


        //================================================







    }


    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            //  pDialog = null;
        }
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

}