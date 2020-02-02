package com.beerdelivery.driver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.beerdelivery.driver.adapter.ChatAdapter;
import com.beerdelivery.driver.model.ChatMessageModel;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private ArrayList<ChatMessageModel> dataList;
    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initRecyclerView();
    }

    private void initRecyclerView() {
        rv=(RecyclerView)findViewById(R.id.rvChat);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        initializeData();
        initializeAdapter();
    }

    private void initializeData(){
        dataList = new ArrayList<>();
        dataList.add(new ChatMessageModel("Сообщение 1", "Stdio"));
        dataList.add(new ChatMessageModel("Сообщение 2", "Stdio"));
        dataList.add(new ChatMessageModel("Сообщение 3", "Stdio"));
    }

    private void initializeAdapter(){
        ChatAdapter adapter = new ChatAdapter(dataList, this);
        rv.setAdapter(adapter);
    }
}
