package com.stdio.aiofordrivers2019;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.HashMap;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.stdio.aiofordrivers2019.helper.PrefManager;

public class driverInfo extends AppCompatActivity {

    TextInputLayout inputLayoutName, inputLayoutFamaly, inputLayoutPhone, inputLayoutMarka, inputLayoutNumber,
            inputLayoutColor;
    EditText inputName, inputFamaly, inputPhone, inputMarka, inputNummber, inputColor;

    private PrefManager pref;

    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        pref = new PrefManager(this);

        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutFamaly = (TextInputLayout) findViewById(R.id.input_layout_famaly);
        inputLayoutPhone = (TextInputLayout) findViewById(R.id.input_layout_phone);

        inputLayoutMarka = (TextInputLayout) findViewById(R.id.input_layout_marka);
        inputLayoutNumber = (TextInputLayout) findViewById(R.id.input_layout_car_number);
        inputLayoutColor = (TextInputLayout) findViewById(R.id.input_layout_color);

        inputName = (EditText) findViewById(R.id.input_name);
        inputFamaly = (EditText) findViewById(R.id.input_famaly);
        inputPhone = (EditText) findViewById(R.id.input_phone);

        inputMarka = (EditText) findViewById(R.id.input_marka);
        inputNummber = (EditText) findViewById(R.id.input_car_number);
        inputColor = (EditText) findViewById(R.id.input_color);


        HashMap<String, String> profile = pref.getDriverDetails();
        inputName.setText(profile.get("name"));
        inputFamaly.setText(profile.get("famaly"));
        inputPhone.setText(profile.get("phone"));
        inputMarka.setText(profile.get("marka"));
        inputColor.setText(profile.get("color"));
        inputNummber.setText(profile.get("number"));



        inputName.setEnabled(false);
        inputName.setCursorVisible(false);

        inputFamaly.setEnabled(false);
        inputFamaly.setCursorVisible(false);

        inputPhone.setEnabled(false);
        inputPhone.setCursorVisible(false);

        inputMarka.setEnabled(false);
        inputMarka.setCursorVisible(false);

        inputColor.setEnabled(false);
        inputColor.setCursorVisible(false);

        inputNummber.setEnabled(false);
        inputNummber.setCursorVisible(false);

      //  fab.setVisibility(View.GONE);



        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

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
