package com.beerdelivery.driver;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.beerdelivery.driver.helper.PrefManager;
import com.beerdelivery.driver.helper.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class TimerActivity extends AppCompatActivity implements View.OnClickListener {

    private final CompositeDisposable disposables = new CompositeDisposable();

    public static int minutPrice = 0;
    public static int orderPrice = 0;
    public static int order = 0;
    public static int waitMinut = 0;

    public static int seconds = 0;
    public static int minutes = 0;
    TextView tvTimer, tvPrice;

    public static int waitPrice = 0;

    private PrefManager pref;
    RequestQueue queue;

    Intent intentService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        pref = new PrefManager(this);
        queue = Volley.newRequestQueue(this);
        intentService = new Intent(this, MyService.class);

        tvTimer = findViewById(R.id.tvTimer);
        tvPrice = findViewById(R.id.tvPrice);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
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
        Log.e("666", minutPrice + " " + orderPrice + " " + order + " " + waitMinut);
        tvPrice.setText("Стоимость доставки: " + (orderPrice + waitPrice));
        setButtonsOnClick();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyService.disposables.clear();
        stopService(intentService);
        seconds = 0;
        minutes = 0;
        waitPrice = 0;

        minutPrice = 0;
        orderPrice = 0;
        order = 0;
        waitMinut = 0;
        disposables.clear(); // clearing it : do not emit after destroy
    }

    @Override
    protected void onStop() {
        disposables.clear(); // clearing it : do not emit after destroy
        startService(intentService);
        super.onStop();
    }

    @Override
    protected void onResume() {
        Log.e("666", minutPrice + " " + orderPrice + " " + order + " " + waitMinut);
        tvPrice.setText("Стоимость доставки: " + (orderPrice + waitPrice));
        String strSeconds = (seconds >= 10) ? seconds + "" : "0" + seconds;
        String strMinutes = (minutes >= 10) ? minutes + "" : "0" + minutes;
        tvTimer.setText(strMinutes + ":" + strSeconds);
        MyService.disposables.clear();
        stopService(intentService);
        doSomeWork();
        super.onResume();
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
                System.out.println(seconds);
                seconds++;
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

    private void setButtonsOnClick() {
        CardView btnComplete, btnCancel;
        btnComplete = findViewById(R.id.btnComplete);
        btnCancel = findViewById(R.id.btnCancel);
        btnComplete.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnComplete:
                completeOrder(true, String.valueOf(order), "sendCompliteOrder", String.valueOf(waitPrice + orderPrice), String.valueOf(minutes), "");
                break;
            case R.id.btnCancel:
                cancel();
                break;
        }
    }

    private void completeOrder(boolean forComplete, final String order, String command, String money, String waitminut, String res) {


        String url = pref.getCityUrl() + Urls.TAKE_ORDER_URL;


        Map<String, String> map = new HashMap<>();


        map.put("driverId", pref.getDriverId());
        map.put("hash", pref.getHash());
        map.put("command", command);

        if (forComplete) {
            map.put("orderId", order);
            map.put("money", money);
            map.put("waitminut", waitminut);
        }
        else {
            map.put("order", order);
            map.put("res", res);
        }

        Log.e("666", "Autorize - " + map + "\n" + url);


        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url,

                new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("666", "Autorize - " + response);
                        try {
                            String status = response.getString("st");
                            if (status.equals("3")) {
                                Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                            Intent intent = new Intent(TimerActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

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

    private void cancel() {
        final View view = getLayoutInflater().inflate(R.layout.edit_text_dialog, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Отказ");
        alertDialog.setCancelable(false);
        alertDialog.setMessage("Причина отказа");


        final EditText etComments = (EditText) view.findViewById(R.id.etComments);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                completeOrder(false, String.valueOf(order), "changeOrderStatusDel", "", "", etComments.getText().toString());
            }
        });


        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });


        alertDialog.setView(view);
        alertDialog.show();
    }
}
