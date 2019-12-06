package com.stdio.aiofordrivers2019;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class TimerActivity extends AppCompatActivity {

    int minutPrice = 0;
    int orderPrice = 0;
    int order = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                minutPrice = 0;
            } else {
                minutPrice = extras.getInt("minutPrice");
                orderPrice = extras.getInt("orderPrice");
                order = extras.getInt("order");
            }
        } else {
            minutPrice = (int) savedInstanceState.getSerializable("minutPrice");
            orderPrice = (int) savedInstanceState.getSerializable("orderPrice");
            order = (int) savedInstanceState.getSerializable("order");
        }

        Log.e("666", minutPrice  + " " + orderPrice + " " + order);
    }
}
