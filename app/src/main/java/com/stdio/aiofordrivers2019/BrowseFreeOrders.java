package com.stdio.aiofordrivers2019;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.stdio.aiofordrivers2019.adapter.OrdersRVAdapter;
import com.stdio.aiofordrivers2019.helper.NotificationsHelper;
import com.stdio.aiofordrivers2019.helper.PrefManager;
import com.stdio.aiofordrivers2019.helper.Urls;
import com.stdio.aiofordrivers2019.model.ModelOrders;

public class BrowseFreeOrders extends AppCompatActivity {

    // Movies json url
    String url;
    private ProgressDialog pDialog;
    private List<ModelOrders> ordersList = new ArrayList<ModelOrders>();
    private RecyclerView rv;
    private PrefManager pref;
    RequestQueue queue;
    OrdersRVAdapter adapter;

    int intNow = 1, intAdvance = 0;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    private Handler handler;
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
        initRecyclerView();

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Отправка запроса...");

        intNow = 1;


        Log.e("666", "onCreate()");


        getFreeOrders();

        handler = new Handler();
        handler.postDelayed(runnable, 100);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        NotificationsHelper.createNotification("Обзор заказов", BrowseFreeOrders.class, 0);

    }

    private void initRecyclerView() {
        rv = findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        initializeAdapter();

        rv.addOnItemTouchListener(
                new RecyclerItemClickListener(BrowseFreeOrders.this, rv ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        String[] coords_store;
                        String[] coords_client;

                        coords_store = ordersList.get(position).getCoords_store().split(",");
                        coords_client = ordersList.get(position).getCoords_client().split(",");

                        double originLatitude = Double.parseDouble(coords_store[0]);
                        double originLongitude = Double.parseDouble(coords_store[1]);

                        System.out.println(originLatitude);
                        System.out.println(originLongitude);

                        double destinationLatitude = Double.parseDouble(coords_client[0]);
                        double destinationLongitude = Double.parseDouble(coords_client[1]);

                        OrderReviewActivity.origin = new LatLng(originLatitude, originLongitude);
                        OrderReviewActivity.destination = new LatLng(destinationLatitude, destinationLongitude);
                        OrderReviewActivity.orderId = ((TextView) view.findViewById(R.id.orderIdText)).getText().toString();
                        OrderReviewActivity.from = ((TextView) view.findViewById(R.id.textFrom)).getText().toString();
                        OrderReviewActivity.toAddress = ((TextView) view.findViewById(R.id.textTo)).getText().toString();
                        OrderReviewActivity.time = ((TextView) view.findViewById(R.id.textDateTime)).getText().toString();
                        OrderReviewActivity.price = ((TextView) view.findViewById(R.id.priceValue)).getText().toString();
                        OrderReviewActivity.info = ordersList.get(position).getorderInfo();

                        startActivity(new Intent(BrowseFreeOrders.this, OrderReviewActivity.class));
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                }));
    }

    private void initializeAdapter(){
        adapter = new OrdersRVAdapter(ordersList, BrowseFreeOrders.this);
        rv.setAdapter(adapter);
    }


    private void getFreeOrders() {

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
              //          Log.e("666", "Autorize - " + response);
                        try {
                            String status = response.getString("st");

                            if (response.getString("beep").equals("yes")){playDefaultNotificationSound();}

                            if (status.equals("0")) {
                                ordersList.clear();

                                JSONArray jArr = response.getJSONArray("orders");

                         //       Log.e("666", jArr.toString());
                                int all = jArr.length();
                                for (int i = 0; i < jArr.length(); i++) {
                                    JSONObject obj = jArr.getJSONObject(i);

                                    System.out.println(obj);


                                    ModelOrders order = new ModelOrders();
                                    order.setStat("");
                                    order.setorderId(obj.getString("orderID"));
                                    order.setorderTime(obj.getString("orderTime"));
                                    order.setorderTarif(obj.getString("orderTarif"));
                                    order.setclientPlace(obj.getString("orderPlace"));
                                    order.setclientRoute(obj.getString("orderRoute"));
                                    order.setorderInfo(obj.getString("orderInfo"));
                                    order.setCoords_store(obj.getString("coords"));
                                    order.setCoords_client(obj.getString("coords_2"));

                                    ordersList.add(order);

                                }


                                toolbar.setSubtitle("Свободных: " + all);
                                adapter.notifyDataSetChanged();

                            }
                            if (status.equals("-1")) {

                            }


                            // ===========
                            if (status.equals("2")) {

                                ordersList.clear();
                                adapter.notifyDataSetChanged();
                                Toast.makeText(getApplicationContext(),
                                        "Выберите что нибудь !!!!!", Toast.LENGTH_SHORT).show();
                            }

                            if (status.equals("3")) {
                                ordersList.clear();
                                adapter.notifyDataSetChanged();

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


        //===================================================================================


    }


    public void alertOrderInfo(String mes, String title, int t, final String order) {
        AlertDialog.Builder builder = new AlertDialog.Builder(BrowseFreeOrders.this);


        builder.setTitle(title)

                .setMessage(mes);


        builder.setNegativeButton("Прочитано", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

        if (t == 0) {
            builder.setPositiveButton("Взять заказ", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    takeOrderFromDriver(order);

                    dialog.cancel();
                }
            });
        }

        AlertDialog alert = builder.create();
        alert.show();

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
        System.out.println(OrderReviewActivity.orderIsTaken);
        if (OrderReviewActivity.orderIsTaken) {
            OrderReviewActivity.orderIsTaken = false;

            TakenOrderActivity.origin = OrderReviewActivity.origin;
            TakenOrderActivity.destination = OrderReviewActivity.destination;
            TakenOrderActivity.orderId = OrderReviewActivity.orderId;
            TakenOrderActivity.from = OrderReviewActivity.from;
            TakenOrderActivity.toAddress = OrderReviewActivity.toAddress;
            TakenOrderActivity.time = OrderReviewActivity.time;
            TakenOrderActivity.price = OrderReviewActivity.price;
            TakenOrderActivity.statusOrder = "20";
            TakenOrderActivity.info = OrderReviewActivity.info;
            startActivity(new Intent(this, BrowseTakenOrders.class));
            startActivity(new Intent(this, TakenOrderActivity.class));
            finish();
        }
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

        handler.removeCallbacks(runnable);
        Log.e("666", "onDestroy()");
    }


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
      /* do what you need to do */
            getFreeOrders();

           Log.e("666", "getFreeOrders() - start");

      /* and here comes the "trick" */
            handler.postDelayed(this, 15000);
        }
    };


    public void takeOrderFromDriver(final String order) {


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
                           Log.e("666", "Autorize - " + response);
                        try {
                            String status = response.getString("st");



                            if (status.equals("0")) {


                                alertOrderInfo(response.getString("message")
                                        , "Заказ № " + order, 1, order);

                            }
                            if (status.equals("-1")) {

                                alertOrderInfo(response.getString("message")
                                        , "Заказ № " + order, 1, order);
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


    private void playDefaultNotificationSound() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();
    }


    private void setBackgroundRed(CheckBox chex) {
        final int sdk = Build.VERSION.SDK_INT;
        if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
            chex.setBackgroundDrawable(getResources().getDrawable(R.drawable.b_red));
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                chex.setBackground(getResources().getDrawable(R.drawable.b_red));
            }
        }

    }



    private void setBackgroundGreen(CheckBox chex) {
        final int sdk = Build.VERSION.SDK_INT;
        if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
            chex.setBackgroundDrawable(getResources().getDrawable(R.drawable.b_yes));
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                chex.setBackground(getResources().getDrawable(R.drawable.b_yes));
            }
        }

    }

}