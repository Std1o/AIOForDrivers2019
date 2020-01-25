package com.beerdelivery.driver;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.TextView;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.beerdelivery.driver.helper.PrefManager;
import com.beerdelivery.driver.helper.Urls;

public class OldOrdersActivity extends AppCompatActivity {

    private PrefManager pref;
    RequestQueue queue;


    Toolbar toolbar;



    WebView webView;
    int num1, num2, num3, num4, num5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_orders);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        pref = new PrefManager(this);
        queue = Volley.newRequestQueue(this);


        webView = (WebView)findViewById(R.id.webView);
        webView.addJavascriptInterface(new WebAppInterface(), "Android");

        webView.getSettings().setJavaScriptEnabled(true);


        takeOrderInfoFromDriver();

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


    private void takeOrderInfoFromDriver() {


        String url = pref.getCityUrl() + Urls.INFO_OLD_ORDERS_URL;


        Map<String, String> map = new HashMap<>();


        map.put("driverId", pref.getDriverId());
        map.put("hash", pref.getHash());


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

                                num1 = Integer.parseInt(response.getString("ordersAll"));

                                num2 = Integer.parseInt( response.getString("ordersClose"));
                                num3 = Integer.parseInt(response.getString("ordersBad"));

                                num4 = Integer.parseInt(response.getString("monAll"));
                                num5 = Integer.parseInt(response.getString("monCompany"));
                                //response.getString("monBez"));

                                webView.loadUrl("file:///android_asset/chart.html");
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


        //================================================


    }



    public class WebAppInterface {

        @JavascriptInterface
        public int getNum1() {
            return num1;
        }

        @JavascriptInterface
        public int getNum2() {
            return num2;
        }

        @JavascriptInterface
        public int getNum3() {
            return num3;
        }

        @JavascriptInterface
        public int getNum4() {
            return num4;
        }

        @JavascriptInterface
        public int getNum5() {
            return num5;
        }
    }


}
