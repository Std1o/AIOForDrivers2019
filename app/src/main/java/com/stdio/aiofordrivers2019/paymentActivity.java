package com.stdio.aiofordrivers2019;

import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

import com.stdio.aiofordrivers2019.helper.NotificationsHelper;
import com.stdio.aiofordrivers2019.helper.PrefManager;
import com.stdio.aiofordrivers2019.helper.Urls;

public class paymentActivity extends AppCompatActivity {


    private PrefManager pref;

    ListView listView;
    ArrayList<HashMap<String, String>> ArrList;
    RequestQueue queue;
    SimpleAdapter adapter;

    HashMap<String, String> pay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        pref = new PrefManager(getApplicationContext());
        queue = Volley.newRequestQueue(this);
         listView = (ListView)findViewById(R.id.listView);

        ArrList = new ArrayList<HashMap<String, String>>();

        adapter = new SimpleAdapter(this, ArrList, android.R.layout.simple_list_item_2,
                new String[] {"Name", "Tel"},
                new int[] {android.R.id.text1, android.R.id.text2});
        listView.setAdapter(adapter);

        NotificationsHelper.createNotification("Оплата заказов", paymentActivity.class, 0);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getPayments();
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


                                    pay = new HashMap<String, String>();
                                    pay.put("Name", obj.getString("t1"));
                                    pay.put("Tel", obj.getString("t2"));
                                    ArrList.add(pay);


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
