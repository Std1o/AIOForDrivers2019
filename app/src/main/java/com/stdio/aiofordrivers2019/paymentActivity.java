package com.stdio.aiofordrivers2019;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Map;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.stdio.aiofordrivers2019.adapter.OrdersRVAdapter;
import com.stdio.aiofordrivers2019.adapter.PaymentAdapter;
import com.stdio.aiofordrivers2019.helper.NotificationsHelper;
import com.stdio.aiofordrivers2019.helper.PrefManager;
import com.stdio.aiofordrivers2019.helper.Urls;
import com.stdio.aiofordrivers2019.model.ModelOrders;
import com.stdio.aiofordrivers2019.model.ModelPayment;

public class paymentActivity extends AppCompatActivity {


    private PrefManager pref;
    private RecyclerView rv;
    ArrayList<ModelPayment> list = new ArrayList<>();
    TextView amountBalanceText;
    PaymentAdapter adapter;
    RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        amountBalanceText = findViewById(R.id.amountBalanceText);
        amountBalanceText.setText(MainActivity.driverMoney);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        pref = new PrefManager(getApplicationContext());
        queue = Volley.newRequestQueue(this);
        initRecyclerView();

        NotificationsHelper.createNotification("Оплата заказов", paymentActivity.class, 0);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getPayments();
    }

    private void initRecyclerView() {
        rv = findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        initializeAdapter();
    }

    private void initializeAdapter(){
        adapter = new PaymentAdapter(list, paymentActivity.this);
        rv.setAdapter(adapter);
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



    private void getPayments() {

        String url = pref.getCityUrl() + Urls.INFO_PAYMENT_URL;


        Map<String, String> map = new HashMap<>();

        map.put("command", "getFreeOrders");
        map.put("driverId", pref.getDriverId());
        map.put("hash", pref.getHash());


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
                               // ArrList.clear();

                                JSONArray jArr = response.getJSONArray("pays");

                                  Log.e("666", jArr.toString());
                                int all = jArr.length();
                                for (int i = 0; i < jArr.length(); i++) {
                                    JSONObject obj = jArr.getJSONObject(i);

                                    System.out.println(obj);
                                    list.add(new ModelPayment(obj.getString("t1"), obj.getString("t3"), obj.getString("t2")));


                                }



                                adapter.notifyDataSetChanged();

                            }
                            if (status.equals("-1")) {

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

}
