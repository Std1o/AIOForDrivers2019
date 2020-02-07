package com.beerdelivery.driver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.beerdelivery.driver.adapter.ChatAdapter;
import com.beerdelivery.driver.model.ChatMessageModel;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<ChatMessageModel> dataList;
    private RecyclerView rv;
    ImageView sendButton, ivClose;
    EditText messageEditText;
    ChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initRecyclerView();
        initViews();
        setListeners();
    }

    private void initRecyclerView() {
        rv=(RecyclerView)findViewById(R.id.rvChat);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        initializeData();
        initializeAdapter();
    }

    private void initViews() {
        sendButton = findViewById(R.id.sendButton);
        ivClose = findViewById(R.id.ivClose);
        messageEditText = findViewById(R.id.messageEditText);
    }

    private void setListeners() {
        sendButton.setOnClickListener(this);
        ivClose.setOnClickListener(this);
    }

    private void initializeData(){
        dataList = new ArrayList<>();
        dataList.add(new ChatMessageModel("Сообщение 1", "Stdio"));
        dataList.add(new ChatMessageModel("Сообщение 2", "Stdio"));
        dataList.add(new ChatMessageModel("Сообщение 3", "Stdio"));
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sendButton:
                dataList.add(new ChatMessageModel(messageEditText.getText().toString(), "Stdio"));
                adapter.notifyDataSetChanged();
                break;
            case R.id.ivClose:
                finish();
                break;
        }
    }

    private void initializeAdapter(){
        adapter = new ChatAdapter(dataList, this);
        rv.setAdapter(adapter);
    }
}
