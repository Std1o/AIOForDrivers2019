package com.beerdelivery.driver;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

public class activityForResultBadDriverMes extends AppCompatActivity {

    TextInputLayout inputLayoutBadReazon;

    EditText inputReazon;
    String orderId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_for_result_bad_driver_mes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validatereazon();
            }
        });


         orderId=getIntent().getExtras().getString("orderId");
         inputLayoutBadReazon = (TextInputLayout) findViewById(R.id.input_layout_bad_reazon);
         inputReazon = (EditText) findViewById(R.id.input_reazon);
    }



    private boolean validatereazon() {
        if (inputReazon.getText().toString().trim().isEmpty()) {
            inputLayoutBadReazon.setError("Введите причину отказа");
            requestFocus(inputReazon);
            return false;
        } else {
            Intent intentMessage=new Intent();
            intentMessage.putExtra("reazon", inputReazon.getText().toString().trim());
            intentMessage.putExtra("orderId", orderId);
            setResult(RESULT_OK,intentMessage);
            finish();
        }

        return true;
    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
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
}
