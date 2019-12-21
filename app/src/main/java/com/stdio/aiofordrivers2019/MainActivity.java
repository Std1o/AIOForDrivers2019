package com.stdio.aiofordrivers2019;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.stdio.aiofordrivers2019.helper.NotificationsHelper;
import com.stdio.aiofordrivers2019.helper.PrefManager;
import com.stdio.aiofordrivers2019.helper.Urls;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private PrefManager pref;
    TextView city, driverName, tvTypeWork, tvDriverStatus, tvDriverMoney, tvClassAuto, tvInfo,
            tvBlock, tvApp, tvTakeOrders;
    View header;
    RequestQueue queue;
    String driverStatus;

    NotificationsHelper n;

    ImageView userFoto;
    final int REQUEST_PERMISSIONS = 3;
    private CoordinatorLayout coordinatorLayout;

    String[] permissions = new String[]{
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION}; // Here i used multiple permission check


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(R.string.app_name);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        city = (TextView) findViewById(R.id.tv_menu_city);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("tagg", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        pref.setGcm(token);
                        Log.d("tagg", token);
                    }
                });


        header = LayoutInflater.from(this).inflate(R.layout.nav_header_main, null);

        city = (TextView) header.findViewById(R.id.tv_menu_city);
        driverName = (TextView) header.findViewById(R.id.tv_menu_driver_name);
        userFoto = (ImageView) header.findViewById(R.id.img_user_foto);



        pref = new PrefManager(getApplicationContext());
        queue = Volley.newRequestQueue(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                        == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            if (!pref.isLoggedIn()) {
                logout();
                finish();
            } else {

                city.setText(pref.getDriverCity());
                startService(new Intent(this, ServiceSendCoords.class));


              takeInfo("takeStartInfo");
                driverName.setText(pref.getDriverName());

                if (pref.getIsFirstStart()) {
                    pref.setIsFirstStart(false);
                    Log.e("666", "RECREATE");
                    recreate();
                }
            }
        } else {
            requestReadPermission();
        }


       /* tvInfo = (TextView) findViewById(R.id.tvInfo);
        tvDriverStatus = (TextView) findViewById(R.id.tv_driver_status);

        tvTypeWork = (TextView) findViewById(R.id.tv_type_work);

        tvDriverMoney = (TextView) findViewById(R.id.tv_driver_money);

        tvApp = (TextView) findViewById(R.id.tv_app);
        tvTakeOrders = (TextView) findViewById(R.id.tv_take_orders);

        tvClassAuto = (TextView) findViewById(R.id.tv_class_auto);*/

        CardView btnOrders = findViewById(R.id.btnOrders);
        btnOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BrowseFreeOrders.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(intent);
            }
        });

        CardView btnFinishedOrders = findViewById(R.id.btnFinishedOrders);
        btnFinishedOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, browseTakenOrders.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(intent);
            }
        });

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        n = new NotificationsHelper(this);
        n.createNotification("Такси", MainActivity.class, 0);
    }

    private void requestReadPermission() {
        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!pref.isLoggedIn()) {
                        logout();
                        finish();
                    } else {

                        city.setText(pref.getDriverCity());
                        startService(new Intent(this, ServiceSendCoords.class));


                        takeInfo("takeStartInfo");
                        driverName.setText(pref.getDriverName());
                    }
                } else {
                    //Permission has not been granted. Notify the user.
                    Toast.makeText(MainActivity.this, "Permission is Required", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_free_orders) {
            Intent intent = new Intent(MainActivity.this, BrowseFreeOrders.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);


        } else if (id == R.id.nav_taken_orders) {
            Intent intent = new Intent(MainActivity.this, browseTakenOrders.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);

        } else if (id == R.id.nav_pay) {

            Intent intent = new Intent(this, paymentActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == R.id.nav_foto) {

            Intent intent = new Intent(this, UploadFotoActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == R.id.nav_h_orders) {

            Intent intent = new Intent(this, OldOrdersActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        return true;
    }


    private void logout() {
        pref.clearSession();

        Intent intent = new Intent(MainActivity.this, restoreDostup.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);


    }


    @Override
    protected void onRestart() {
        super.onRestart();
        n.createNotification("Такси", MainActivity.class, 0);
        Log.e("666", "onRestart()");
        takeInfo("takeStartInfo");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopService(new Intent(MainActivity.this, ServiceSendCoords.class));

        NotificationsHelper.cancelAllNotifications();
        Log.e("MAIN", "onDestroy()");
    }


    private void takeInfo(String command) {


        String url = "http://bdsystem.ru/" + Urls.START_INFO_URL;


        Map<String, String> map = new HashMap<>();

        map.put("command", command);
        map.put("driverId", pref.getDriverId());
        map.put("hash", pref.getHash());
        map.put("preOrder", pref.getPreOrders());
        map.put("driverGCM", pref.getGsm());


        Log.e("666", "Autorize - " + map + "\n" + url);


        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url,

                new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("666", "Autorize - " + response);
                        try {
                            String status = response.getString("st");


                            if (status.equals("0")) {

                                /*tvTypeWork.setText(response.getString("driverTypeWork"));

                                tvDriverMoney.setText("Баланс:\n" + response.getInt("driverMoney") + " р.");

                                tvClassAuto.setText(response.getString("classAuto"));
                                if (response.getInt("driverMoney") > 299) {

                                    tvDriverMoney.setBackgroundResource(R.drawable.b_yes);

                                } else {
                                    tvDriverMoney.setBackgroundResource(R.drawable.b_red);
                                }


                                if (response.getString("driverStatus").equals("0")) {
                                    tvDriverStatus.setText("Статус:\nСвободен");
                                    tvDriverStatus.setBackgroundResource(R.drawable.b_yes);

                                }

                                if (response.getString("driverStatus").equals("1")) {
                                    tvDriverStatus.setText("Статус:\nЗанят");
                                    tvDriverStatus.setBackgroundResource(R.drawable.b_red);
                                }*/

                                driverStatus = response.getString("driverStatus");


                                if (!response.getString("appVer").equals(getResources().getString(R.string.app_vr))) {

                                    goGooglePlay();
                                    finish();
                                }


                                /*tvTakeOrders.setText("Взятые\nзаказы:\n" + response.getInt("takenOrders"));
                                if (response.getInt("takenOrders") > 0) {

                                    tvTakeOrders.setBackgroundResource(R.drawable.b_yes);

                                } else {
                                    tvTakeOrders.setBackgroundResource(R.drawable.b_no);
                                }


                                if (response.getString("active").equals("0")) {
                                    tvInfo.setText("Ожидайте активации");
                                } else {
                                    tvInfo.setText("");
                                }*/


                            }
                            if (status.equals("-1")) {

                            }

                            // =========== смена Статуса=====
                            if (status.equals("1")) {
                                if (response.getString("message").equals("free")) {
                                    driverStatus = "0";
                                    tvDriverStatus.setText("Статус:\nСвободен");
                                    tvDriverStatus.setBackgroundResource(R.drawable.b_yes);
                                }
                                if (response.getString("message").equals("busy")) {
                                    driverStatus = "1";
                                    tvDriverStatus.setText("Статус:\nЗанят");
                                    tvDriverStatus.setBackgroundResource(R.drawable.b_red);
                                }


                            }
                            // ===========
                            if (status.equals("2")) {

                                Toast.makeText(getApplicationContext(),
                                        "Изменения внесены", Toast.LENGTH_SHORT).show();

                            }

                            if (status.equals("3")) {


                            }

                            if (status.equals("4")) {


                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("666", "Autorize - " + e);
                            noinet();

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        noinet();

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


    public void changeDriverStatus(View view) {


        if (driverStatus.equals("0")) {
            takeInfo("status_busy");
        }
        if (driverStatus.equals("1")) {
            takeInfo("status_free");
        }

    }


    public void goGooglePlay() {

        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }

    }

    public void goTakenOrders(View v) {
        Intent intent = new Intent(MainActivity.this, browseTakenOrders.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }


    public void noinet() {


        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Ошибка связи с сервером!", Snackbar.LENGTH_LONG)
                .setAction("ПОВТОР", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        takeInfo("takeStartInfo");
                    }
                });

        // Changing message text color
        snackbar.setActionTextColor(Color.RED);

        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);

        snackbar.show();


    }

}