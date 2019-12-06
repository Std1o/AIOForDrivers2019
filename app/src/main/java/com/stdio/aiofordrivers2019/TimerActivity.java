package com.stdio.aiofordrivers2019;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.stdio.aiofordrivers2019.helper.PrefManager;
import com.stdio.aiofordrivers2019.helper.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class TimerActivity extends AppCompatActivity {

    private final CompositeDisposable disposables = new CompositeDisposable();

    int minutPrice = 0;
    int orderPrice = 0;
    int order = 0;
    int waitMinut = 0;

    int seconds = 0;
    int minutes = 0;
    TextView tvTimer, tvPrice;

    int waitPrice = 0;

    private PrefManager pref;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        pref = new PrefManager(this);
        queue = Volley.newRequestQueue(this);

        tvTimer = findViewById(R.id.tvTimer);
        tvPrice = findViewById(R.id.tvPrice);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                minutPrice = 0;
            } else {
                minutPrice = extras.getInt("minutPrice");
                orderPrice = extras.getInt("orderPrice");
                order = extras.getInt("order");
                waitMinut = extras.getInt("waitMinut");
            }
        } else {
            minutPrice = (int) savedInstanceState.getSerializable("minutPrice");
            orderPrice = (int) savedInstanceState.getSerializable("orderPrice");
            order = (int) savedInstanceState.getSerializable("order");
            waitMinut = (int) savedInstanceState.getSerializable("waitMinut");
        }

        Log.e("666", minutPrice  + " " + orderPrice + " " + order + " " + waitMinut);
        tvPrice.setText("Стоимость доставки: " + (orderPrice + waitPrice));

        doSomeWork();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear(); // clearing it : do not emit after destroy
    }

    /*
     * simple example using interval to run task at an interval of 2 sec
     * which start immediately
     */
    private void doSomeWork() {
        disposables.add(getObservable()
                // Run on a background thread
                .subscribeOn(Schedulers.io())
                // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getObserver()));
    }

    private Observable<? extends Long> getObservable() {
        return Observable.interval(0, 1, TimeUnit.SECONDS);
    }

    private DisposableObserver<Long> getObserver() {
        return new DisposableObserver<Long>() {

            @Override
            public void onNext(Long value) {
                seconds += 1;
                if (seconds >= 60) {
                    minutes++;
                    seconds = seconds % 60;

                    if (minutes > waitMinut) {
                        waitPrice += minutPrice;
                        tvPrice.setText("Стоимость доставки: " + (orderPrice + waitPrice));
                    }
                }
                String strSeconds = (seconds >= 10) ? seconds + "" : "0" + seconds;
                String strMinutes = (minutes >= 10) ? minutes + "" : "0" + minutes;
                tvTimer.setText(strMinutes + ":" + strSeconds);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

    public void onClick(View view) {
        completeOrder(String.valueOf(order), "sendCompliteOrder", String.valueOf(waitPrice + orderPrice), String.valueOf(minutes));
    }

    private void completeOrder(final String order, String command, String money, String waitminut) {


        String url = pref.getCityUrl() + Urls.TAKE_ORDER_URL;


        Map<String, String> map = new HashMap<>();


        map.put("driverId", pref.getDriverId());
        map.put("hash", pref.getHash());
        map.put("command", command);
        map.put("orderId", order);
        map.put("money", money);
        map.put("waitminut", waitminut);

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
                                TimerActivity.this.finish();
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
