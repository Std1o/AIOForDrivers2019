package com.beerdelivery.driver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<ChatMessageModel> dataList;
    private RecyclerView rv;
    ImageView sendButton, ivClose;
    CardView leftBtn;
    EditText messageEditText;
    ChatAdapter adapter;
    PrefManager prefManager;
    RequestQueue queue;

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
    }

    private void initRecyclerView() {
        rv=(RecyclerView)findViewById(R.id.rvChat);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setItemAnimator(new SlideInUpAnimator());
        initializeData();
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

    private void initializeData(){
        dataList = new ArrayList<>();
        dataList.add(new ChatMessageModel("Сообщение 1", "Stdio"));
        dataList.add(new ChatMessageModel("Сообщение 2", "Stdio"));
        dataList.add(new ChatMessageModel("Сообщение 3", "Stdio"));
    }

    private void getMessages() {

        String url = prefManager.getCityUrl() + Urls.MESSAGES_URL;


        Map<String, String> map = new HashMap<>();

        map.put("command", "chatList");
        map.put("driverId", prefManager.getDriverId());
        map.put("hash", prefManager.getHash());

        //    Log.e("666", "Autorize - " + map + "\n" + url);


        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url,

                new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("666", "Chat - " + response);
                        try {
                            String status = response.getString("st");

                            if (status.equals("0")) {
                                dataList.clear();

                                JSONArray jArr = response.getJSONArray("chat");

                                //       Log.e("666", jArr.toString());
                                int all = jArr.length();
                                for (int i = 0; i < jArr.length(); i++) {
                                    JSONObject obj = jArr.getJSONObject(i);

                                    System.out.println(obj);
                                    dataList.add(new ChatMessageModel(obj.getString("mess"), obj.getString("name")));

                                }
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

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sendButton:
                dataList.add(new ChatMessageModel(messageEditText.getText().toString(), "Stdio"));
                adapter.notifyItemInserted(dataList.size()-1);
                rv.smoothScrollToPosition(dataList.size()-1);
                messageEditText.setText("");
                break;
            case R.id.ivClose:
            case R.id.leftBtn:
                finish();
                break;
        }
    }

    private void initializeAdapter(){
        adapter = new ChatAdapter(dataList, this);
        rv.setAdapter(adapter);
    }
}
