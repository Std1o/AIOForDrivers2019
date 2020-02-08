package com.beerdelivery.driver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.NotificationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.beerdelivery.driver.adapter.ChatAdapter;
import com.beerdelivery.driver.helper.NotificationsHelper;
import com.beerdelivery.driver.helper.PrefManager;
import com.beerdelivery.driver.helper.Urls;
import com.beerdelivery.driver.model.ChatMessageModel;
import com.beerdelivery.driver.model.ModelOrders;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<ChatMessageModel> dataList = new ArrayList<>();
    private RecyclerView rv;
    ImageView sendButton, ivClose;
    CardView leftBtn;
    EditText messageEditText;
    ChatAdapter adapter;
    PrefManager prefManager;
    RequestQueue queue;
    public static boolean isChatOpened = false;
    private final CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        prefManager = new PrefManager(getApplicationContext());
        queue = Volley.newRequestQueue(this);
        initRecyclerView();
        initViews();
        setListeners();
        getMessages();
        doSomeWork();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(378);
        NotificationsHelper.createNotification("Чат", ChatActivity.class, 0);
    }

    private void initRecyclerView() {
        rv = (RecyclerView) findViewById(R.id.rvChat);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setItemAnimator(new SlideInUpAnimator());
        initializeAdapter();
    }

    private void initViews() {
        sendButton = findViewById(R.id.sendButton);
        ivClose = findViewById(R.id.ivClose);
        leftBtn = findViewById(R.id.leftBtn);
        messageEditText = findViewById(R.id.messageEditText);
    }

    private void setListeners() {
        sendButton.setOnClickListener(this);
        ivClose.setOnClickListener(this);
        leftBtn.setOnClickListener(this);
    }

    private void getMessages() {
        String url = prefManager.getCityUrl() + Urls.MESSAGES_URL;

        Map<String, String> map = new HashMap<>();

        map.put("command", "chatList");
        map.put("driverId", prefManager.getDriverId());
        map.put("hash", prefManager.getHash());

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url,

                new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("666", "Chat - " + response);
                        try {
                            String status = response.getString("st");

                            if (status.equals("0")) {
                                JSONArray jArr = response.getJSONArray("chat");
                                for (int i = 0; i < jArr.length(); i++) {
                                    JSONObject obj = jArr.getJSONObject(i);
                                    if (dataList.size()-1 < i) {
                                        dataList.add(new ChatMessageModel(obj.getString("mess"), obj.getString("name")));
                                        adapter.notifyItemInserted(dataList.size() - 1);
                                        rv.smoothScrollToPosition(dataList.size() - 1);
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
                headers.put("User-agent", "Motolife Linux Android");
                return headers;
            }
        };
        queue.add(postRequest);
    }

    private void sendMessage() {
        String url = prefManager.getCityUrl() + Urls.MESSAGES_URL;

        Map<String, String> map = new HashMap<>();

        map.put("command", "newMes");
        map.put("driverId", prefManager.getDriverId());
        map.put("hash", prefManager.getHash());
        map.put("name", prefManager.getDriverName());
        map.put("mes", messageEditText.getText().toString());

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url,

                new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("666", "Chat - " + response);
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
                headers.put("User-agent", "Motolife Linux Android");
                return headers;
            }
        };
        queue.add(postRequest);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sendButton:
                sendMessage();
                dataList.add(new ChatMessageModel(messageEditText.getText().toString(), prefManager.getDriverName()));
                adapter.notifyItemInserted(dataList.size() - 1);
                rv.smoothScrollToPosition(dataList.size() - 1);
                messageEditText.setText("");
                break;
            case R.id.ivClose:
            case R.id.leftBtn:
                finish();
                break;
        }
    }

    private void doSomeWork() {
        disposables.add(getObservable()
                // Run on a background thread
                .subscribeOn(Schedulers.io())
                // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getObserver()));
    }

    private Observable<? extends Long> getObservable() {
        return Observable.interval(0, 5, TimeUnit.SECONDS);
    }

    private DisposableObserver<Long> getObserver() {
        return new DisposableObserver<Long>() {

            @Override
            public void onNext(Long value) {
                getMessages();
                System.out.println(dataList.size());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        isChatOpened = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isChatOpened = false;
    }

    private void initializeAdapter() {
        adapter = new ChatAdapter(dataList, this);
        rv.setAdapter(adapter);
    }
}
