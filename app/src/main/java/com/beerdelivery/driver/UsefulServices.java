package com.beerdelivery.driver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.beerdelivery.driver.adapter.UsefulServicesAdapter;
import com.beerdelivery.driver.helper.NotificationsHelper;
import com.beerdelivery.driver.helper.PrefManager;
import com.beerdelivery.driver.helper.Urls;
import com.beerdelivery.driver.imageloader.GlideImageLoader;
import com.beerdelivery.driver.model.ServicesModel;
import com.yyydjk.library.BannerLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsefulServices extends AppCompatActivity {

    private PrefManager pref;
    private RecyclerView rv;
    private ArrayList<ServicesModel> servicesList = new ArrayList<>();
    UsefulServicesAdapter adapter;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_useful_services);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        pref = new PrefManager(getApplicationContext());
        queue = Volley.newRequestQueue(this);
        initRecyclerView();

        NotificationsHelper.createNotification("Полезные сервисы", paymentActivity.class, 0);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getPayments();
        initBanner();
    }

    private void initBanner() {
        BannerLayout bannerLayout2 = (BannerLayout) findViewById(R.id.banner2);

        final List<String> urls = new ArrayList<>();
        urls.add("http://img3.imgtn.bdimg.com/it/u=2674591031,2960331950&fm=23&gp=0.jpg");
        urls.add("http://img5.imgtn.bdimg.com/it/u=3639664762,1380171059&fm=23&gp=0.jpg");
        urls.add("http://img0.imgtn.bdimg.com/it/u=1095909580,3513610062&fm=23&gp=0.jpg");
        urls.add("http://img4.imgtn.bdimg.com/it/u=1030604573,1579640549&fm=23&gp=0.jpg");
        urls.add("http://img5.imgtn.bdimg.com/it/u=2583054979,2860372508&fm=23&gp=0.jpg");

        bannerLayout2.setImageLoader(new GlideImageLoader());
        bannerLayout2.setViewUrls(urls);
    }

    private void initRecyclerView() {
        rv = findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        initializeAdapter();
    }

    private void initializeAdapter(){
        adapter = new UsefulServicesAdapter(servicesList, UsefulServices.this);
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

        String url = pref.getCityUrl() + Urls.MESSAGES_URL;


        Map<String, String> map = new HashMap<>();

        map.put("command", "newsList");
        map.put("driverId", pref.getDriverId());
        map.put("hash", pref.getHash());


        Log.e("666", "Autorize - " + map + "\n" + url);


        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url,

                new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                                 Log.e("666", "Services - " + response);
                        try {
                            String status = response.getString("st");

                            if (status.equals("0")) {
                                JSONArray jArr = response.getJSONArray("chat");
                                for (int i = 0; i < jArr.length(); i++) {
                                    JSONObject obj = jArr.getJSONObject(i);
                                    if (obj.getString("type").equals("1")) {
                                        if (servicesList.size()-1 < i) {
                                            servicesList.add(new ServicesModel(obj.getString("info_1"), obj.getString("phone")));
                                            adapter.notifyItemInserted(servicesList.size() - 1);
                                            rv.smoothScrollToPosition(servicesList.size() - 1);
                                        }
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


        //===================================================================================


    }
}
