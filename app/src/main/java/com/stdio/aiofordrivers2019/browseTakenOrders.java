package com.stdio.aiofordrivers2019;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.stdio.aiofordrivers2019.adapter.takenOrdersCustomListAdapter;
import com.stdio.aiofordrivers2019.app.AppController;
import com.stdio.aiofordrivers2019.app.CustomRequest;
import com.stdio.aiofordrivers2019.helper.NotificationsHelper;
import com.stdio.aiofordrivers2019.helper.PrefManager;
import com.stdio.aiofordrivers2019.helper.Urls;
import com.stdio.aiofordrivers2019.model.modelOrders;
import com.stdio.aiofordrivers2019.model.modelTakenOrders;

public class browseTakenOrders extends AppCompatActivity {

    // Movies json url
    String url;
    private ProgressDialog pDialog;
    private List<modelTakenOrders> ordersList = new ArrayList<modelTakenOrders>();
    private ListView listView;
    private takenOrdersCustomListAdapter adapter;
    private PrefManager pref;
    RequestQueue queue;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_free_orders);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        pref = new PrefManager(this);
        queue = Volley.newRequestQueue(this);

        listView = (ListView) findViewById(R.id.list);
        adapter = new takenOrdersCustomListAdapter(this, ordersList);
        listView.setAdapter(adapter);

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");


        url = Urls.TAKE_ORDER_URL;

        Log.e("666", "onCreate()");

        getTakenOrders();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub

                String orderId = ((TextView) arg1.findViewById(R.id.tv_order_id)).getText().toString();

                alertOrderInfo(((TextView) arg1.findViewById(R.id.tv_clent_point)).getText().toString()
                        , "№ "
                                + orderId,
                        ((TextView) arg1.findViewById(R.id.tv_order_status)).getText().toString(),
                        orderId,
                        ((TextView) arg1.findViewById(R.id.tv_client_phone)).getText().toString()
                );

            }
        });


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        NotificationsHelper.createNotification("Взятые заказы", browseTakenOrders.class, 0);

    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void getTakenOrders() {



        String url = pref.getCityUrl() + Urls.TAKEN_ORDER_LIST_URL;


        Map<String, String> map = new HashMap<>();

        map.put("command", "getDriverOrders");
        map.put("driverId", pref.getDriverId());
        map.put("hash", pref.getHash());


        //    Log.e("666", "Autorize - " + map + "\n" + url);


        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url,

                new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //          Log.e("666", "Autorize - " + response);
                        try {
                            String status = response.getString("st");



                            if (status.equals("0")) {
                                ordersList.clear();

                                JSONArray jArr = response.getJSONArray("orders");

                                //       Log.e("666", jArr.toString());
                                int all = jArr.length();
                                for (int i = 0; i < jArr.length(); i++) {
                                    JSONObject obj = jArr.getJSONObject(i);


                                    modelTakenOrders order = new modelTakenOrders();

                                    order.setorderId(obj.getString("orderID"));
                                    order.setorderTime(obj.getString("orderTime"));
                                    order.setorderTarif(obj.getString("orderTarif"));
                                    order.setclientPlace(obj.getString("orderPlace"));
                                    order.setclientRoute(obj.getString("orderRoute"));
                                    order.setorderInfo(obj.getString("orderInfo"));

                                    order.setorderStatus(obj.getString("status"));

                                    order.setclientPhone(obj.getString("clientPhone"));

                                    ordersList.add(order);
                                }


                                toolbar.setSubtitle("Взято: " + all);
                                adapter.notifyDataSetChanged();

                            }
                            if (status.equals("-1")) {

                            }


                            // ===========
                            if (status.equals("2")) {


                            }

                            if (status.equals("3")) {
                                ordersList.clear();
                                adapter.notifyDataSetChanged();

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


        //===================================================================================






    }


    public void alertOrderInfo(String mes, String title, String t, final String order, final String clientPhone) {
        AlertDialog.Builder builder = new AlertDialog.Builder(browseTakenOrders.this);
        builder.setTitle(title).setMessage(mes);
        if (t.equals("1")) {


            builder.setNeutralButton("Просмотрено", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    dialog.cancel();
                }
            });
        }


        if (t.equals("20")) {

            builder.setNeutralButton("Отказаться", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    Intent intent = new Intent(browseTakenOrders.this, activityForResultBadDriverMes.class);
                    intent.putExtra("orderId", order);
                    startActivityForResult(intent, 1);

                    dialog.cancel();
                }
            });

            builder.setNegativeButton("Звонок клиенту", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    String number = String.format("tel:%s", clientPhone);
                    if (ActivityCompat.checkSelfPermission(browseTakenOrders.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(number)));
                    dialog.cancel();

                }
            });


            builder.setPositiveButton("Еду к клиенту", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    takeOrderFromDriver(order, "changeOrderStatus_30", "");
                    dialog.cancel();
                }
            });
        }


        if (t.equals("30")) {


            builder.setNeutralButton("Отказаться", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(browseTakenOrders.this, activityForResultBadDriverMes.class);
                    intent.putExtra("orderId", order);
                    startActivityForResult(intent, 1);
                    dialog.cancel();
                }
            });

            builder.setNegativeButton("Звонок клиенту", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    String number = String.format("tel:%s", clientPhone);
                    if (ActivityCompat.checkSelfPermission(browseTakenOrders.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(number)));
                    dialog.cancel();

                }
            });


            builder.setPositiveButton("Прибыл к клиенту", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    takeOrderFromDriver(order, "changeOrderStatus_40", "");
                    dialog.cancel();
                }
            });
        }


        if (t.equals("40")) {


            builder.setNeutralButton("Отказаться", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(browseTakenOrders.this, activityForResultBadDriverMes.class);
                    intent.putExtra("orderId", order);
                    startActivityForResult(intent, 1);
                    dialog.cancel();
                }
            });

            builder.setNegativeButton("Звонок клиенту", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    String number = String.format("tel:%s", clientPhone);
                    if (ActivityCompat.checkSelfPermission(browseTakenOrders.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(number)));
                    dialog.cancel();

                }
            });


            builder.setPositiveButton("Ожидание оплаты", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    takeOrderFromDriver(order, "changeOrderStatus_40_2","");
                    dialog.cancel();
                }
            });
        }

        if (t.equals("80")) {
            builder.setTitle("Отказной заказ").setMessage("Вы отказались от заказа. Заказ будет снят с вас  после просмотра администратором");

            builder.setNeutralButton("Просмотрено", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    dialog.cancel();
                }
            });


        }

        AlertDialog alert = builder.create();
        alert.show();

    }


    @Override
    public void onPause() {
        super.onPause();

        //     myThread.interrupt();
        Log.e("666", "ONPAUSE");
    }

    @Override
    protected void onStart() {
        super.onStart();


        Log.e("666", "onStart()");

    }

    @Override
    protected void onResume() {
        super.onResume();
        //   myThread.run();

        Log.e("666", "onResume()");
    }


    @Override
    protected void onStop() {
        super.onStop();


        Log.e("666", "onStop()");

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        //  handler.postDelayed(runnable, 100);
        Log.e("666", "onRestart()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.e("666", "onDestroy()");
    }






    private void takeOrderFromDriver(final String order, String command, String res) {


        String url = pref.getCityUrl() + Urls.TAKE_ORDER_URL;


        Map<String, String> map = new HashMap<>();


        map.put("driverId", pref.getDriverId());
        map.put("hash", pref.getHash());
        map.put("command", command);
        map.put("order", order);
        map.put("res", res);

            Log.e("666", "Autorize - " + map + "\n" + url);


        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url,

                new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                                Log.e("666", "Autorize - " + response);
                        try {
                            String status = response.getString("st");



                            if (status.equals("-1")) {

                            }

                            if (status.equals("2")) {
                                Intent timerIntent = new Intent(browseTakenOrders.this, TimerActivity.class);
                                timerIntent.putExtra("minutPrice", response.getInt("minutPrice"));
                                timerIntent.putExtra("orderPrice", response.getInt("orderPrice"));
                                timerIntent.putExtra("order", response.getInt("orderId"));
                                timerIntent.putExtra("waitMinut", response.getInt("waitMinut"));
                                startActivity(timerIntent);
                            }


                            // ===========

                            if (status.equals("3")) {
                                alertOrderInfo(response.getString("message")
                                        , "Заказ № " + order, "1", order,"");

                            }


                            getTakenOrders();


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


    private void playDefaultNotificationSound() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if(requestCode==1)
            {
                takeOrderFromDriver(data.getStringExtra("orderId"), "changeOrderStatusDel", data.getStringExtra("reazon"));



            }

        }

        else {
            Toast.makeText(this, "Изменения не произведены", Toast.LENGTH_SHORT).show();

        }
    }



}

