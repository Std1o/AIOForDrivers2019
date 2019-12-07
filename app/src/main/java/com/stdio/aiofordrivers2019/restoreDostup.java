package com.stdio.aiofordrivers2019;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.android.material.textfield.TextInputLayout;
import com.stdio.aiofordrivers2019.app.AppController;
import com.stdio.aiofordrivers2019.app.CustomRequest;
import com.stdio.aiofordrivers2019.helper.PrefManager;
import com.stdio.aiofordrivers2019.helper.Urls;

public class restoreDostup extends AppCompatActivity {

    TextInputLayout inputLayoutPhone, inputLayoutCode;

    EditText inputPhone, inputCode;


    LinearLayout LayoutInfo;

    private ProgressDialog pDialog;
    private PrefManager pref;

    RequestQueue queue;


    ImageView logo;
    TextView tvInfo;

    // String PROJECT_NUMBER = "570865019109";
    String IMEI;

    int buttonCheck = 0;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.next) {

            hideSoftKeyboard();
            if (validatePhone() && validateSmsCode()) {
                Autorize();
            }
            else {
                return false;
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore_dostup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Идет загрузка...");
        pDialog.setCancelable(false);
        queue = Volley.newRequestQueue(this);
        pref = new PrefManager(this);


        logo = (ImageView) findViewById(R.id.imageView);
        tvInfo = (TextView) findViewById(R.id.tv_reg_info);

        LayoutInfo = (LinearLayout) findViewById(R.id.layout_fio);


        inputLayoutPhone = (TextInputLayout) findViewById(R.id.input_layout_phone);
        inputLayoutPhone.setVisibility(View.VISIBLE);
        inputLayoutCode = (TextInputLayout) findViewById(R.id.input_layout_code);
        inputLayoutCode.setVisibility(View.VISIBLE);

        inputPhone = (EditText) findViewById(R.id.input_phone);
        inputCode = (EditText) findViewById(R.id.input_code);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        IMEI = tMgr.getDeviceId();
        pref.setTaxiUrl("http://bdsystem.ru/");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reg, menu);
        return true;
    }

    private void Autorize() {
        String url = "http://some-company.svkcom.ru" + Urls.REGISTER_APPLY_URL;
        Map<String, String> map = new HashMap<>();

        map.put("command", "send_phone");
        map.put("code", inputCode.getText().toString());
        map.put("driverPhone", inputPhone.getText().toString());
        map.put("app", getResources().getString(R.string.app_vr));

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
                                tvInfo.setText(response.getString("message"));

                            }
                            if (status.equals("1")) {
                                pref.setMobileNumber(response.getString("phone"));
                                pref.setDriverId(response.getString("driverId"));
                                pref.setHash(response.getString("hash"));
                                pref.createDriver(
                                        response.getString("name"),
                                        response.getString("famaly"),
                                        response.getString("marka"),
                                        response.getString("number"),
                                        response.getString("color")
                                );
                                tvInfo.setText(getResources().getText(R.string.tv_reg_input_sms_code));
                                buttonCheck = 1;

                                pref.setPreOrders("0");
                                Intent intent = new Intent(restoreDostup.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("666", "Autorize - " + e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            // pDialog = null;
        }
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }


    //======================проверка телефона ================================

    private static boolean isValidPhoneNumber(String mobile) {
        String regEx = "^[0-9]{11}$";
        return mobile.matches(regEx);
    }

    private boolean validatePhone() {
        if (!isValidPhoneNumber(inputPhone.getText().toString())) {
            inputLayoutPhone.setError(getString(R.string.err_msg_phone));
            requestFocus(inputPhone);
            return false;
        } else {
            inputLayoutPhone.setErrorEnabled(false);
        }

        return true;
    }

    //======================проверка ввода кода ================================
    private static boolean isValidSmsCode(String code) {
        String regEx = "^[0-9]{4}$";
        return code.matches(regEx);
    }

    private boolean validateSmsCode() {
        if (!isValidSmsCode(inputCode.getText().toString())) {
            inputLayoutCode.setError(getString(R.string.err_msg_sms_code));
            requestFocus(inputCode);
            return false;
        } else {
            inputLayoutCode.setErrorEnabled(false);
        }

        return true;
    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    /**
     * Hides the soft keyboard
     */
    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

}
