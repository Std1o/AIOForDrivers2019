package com.beerdelivery.driver;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class activityForResultTripInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_for_result_trip_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        TextView tvPrice = (TextView) findViewById(R.id.tv_check_price);
        TextView tvKm = (TextView) findViewById(R.id.tv_check_km);
        TextView tvMinut = (TextView) findViewById(R.id.tv_check_minut);

        tvPrice.setText(getIntent().getExtras().getString("price"));
        tvKm.setText(getIntent().getExtras().getString("trip_km"));
        tvMinut.setText(getIntent().getExtras().getString("trip_wait"));



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
