package com.stdio.aiofordrivers2019;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.stdio.aiofordrivers2019.R;
import com.stdio.aiofordrivers2019.adapter.CityCustomListAdapter;
import com.stdio.aiofordrivers2019.app.AppController;
import com.stdio.aiofordrivers2019.app.CustomRequest;
import com.stdio.aiofordrivers2019.helper.PrefManager;
import com.stdio.aiofordrivers2019.model.modelCity;


public class ChooseCity extends AppCompatActivity {

    // Movies json url
    private static final String url = "http://transport-116.ru/owner/api/AllInOneCityChose.php";
    private ProgressDialog pDialog;
    private List<modelCity> cityList = new ArrayList<modelCity>();
    private ListView listView;
    private CityCustomListAdapter adapter;
    private PrefManager pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_city);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        pref = new PrefManager(this);

        listView = (ListView) findViewById(R.id.list);
        adapter = new CityCustomListAdapter(this, cityList);
        listView.setAdapter(adapter);

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub

                String city_name = ((TextView) arg1.findViewById(R.id.cityName)).getText().toString();

                uploadDriverCity(city_name);

            }
        });


        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Log.d(TAG, response.toString());
                        hidePDialog();

                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject obj = response.getJSONObject(i);
                                modelCity city = new modelCity();
                                city.setCityName(obj.getString("title"));
                                city.setCityThumbnailUrl(obj.getString("image"));
                                // adding movie to movies array
                                cityList.add(city);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //   VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePDialog();

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq);
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
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
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


    private void uploadDriverCity(final String city) {

        Map<String, String> params = new HashMap<String, String>();
        //Adding parameters

        params.put("command", "setDriverCity");


         params.put("city", city);
         params.put("driverId", pref.getDriverId());


        //   Log.e("666", params.toString());
        showpDialog();




        CustomRequest jsonObjReq = new CustomRequest(
                Request.Method.POST,
                url, params,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("666", response.toString());

                        try {


                                if(response.getString("status").equals("ok")){

                                    Toast.makeText(getApplicationContext(), "Ваш город: " + city, Toast.LENGTH_SHORT).show();

                                    pref.setDriverCity(city, response.getString("cityId"),response.getString("taxiUrl"));

                                    Intent intentMessage=new Intent();
                                    intentMessage.putExtra("city", city);
                                    setResult(RESULT_OK, intentMessage);

                               //     Toast.makeText(getApplicationContext(),
                                 //           response.getString("cityId") + response.getString("taxiUrl"), Toast.LENGTH_SHORT).show();

                                    finish();


                                }
                                else {
                                    Toast.makeText(getApplicationContext(),
                                            response.getString("message"),
                                            Toast.LENGTH_SHORT).show();
                                }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Ошибка: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }


                        hidePDialog();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("666", "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Ошибка связи с сервером", Toast.LENGTH_SHORT).show();
                hidePDialog();
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq, "tag");


    }

}

