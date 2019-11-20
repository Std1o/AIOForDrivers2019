package com.stdio.aiofordrivers2019;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.stdio.aiofordrivers2019.app.AppController;
import com.stdio.aiofordrivers2019.app.CustomRequest;
import com.stdio.aiofordrivers2019.helper.NotificationsHelper;
import com.stdio.aiofordrivers2019.helper.PrefManager;
import com.stdio.aiofordrivers2019.helper.Urls;
import com.stdio.aiofordrivers2019.model.modelOrders;
import com.stdio.aiofordrivers2019.model.modelTakenOrders;

import static android.os.PowerManager.SCREEN_DIM_WAKE_LOCK;

public class Taxometr extends AppCompatActivity {

    String TAG = "666 - Таксометр";
    RequestQueue queue;
    private boolean gpsFix;
    private LocationManager manager;
    long mLastLocationMillis;
    Location mLastLocation;

    double last_long = 0.0;
    double last_lat = 0.0;


    float last_speed;
    long last_time = 0;
    float dist_speed;
    float all_d_speed;
    double speed_km_h = 0;


    int min_price;
    float price_for_km;
    float min_km;
    float minut_price;

    int drive_wait_minut;
    int drive_wait_minut_full = 0;

    float m;
    float price;

    int start = 0;
    float all;
    float d_gps;

    long last_out_gps_time = 0;
    int but_status_start = 0;

    String GPS_COORDS = "no", tvwait, textDistance, money, textTime_wait, orderStatusToSend, orderId;
    Boolean taxomStatus = false;


    int checktax = 0;

    TextView tvWait, MONEY, tvTimeWait, tvDistance;
    private PrefManager pref;
    private Handler handler;

    private Chronometer mChronometer;

    Button butStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxometr);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pref = new PrefManager(this);
        queue = Volley.newRequestQueue(this);
        handler = new Handler();

        butStart = (Button) findViewById(R.id.tog_start);

        tvWait = (TextView) findViewById(R.id.tv_wait);
        MONEY = (TextView) findViewById(R.id.money);
        tvTimeWait = (TextView) findViewById(R.id.textTime_wait);
        tvDistance = (TextView) findViewById(R.id.textDistance);

        mChronometer = (Chronometer) findViewById(R.id.chronometer);
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.setFormat("время:\n %s");
        mChronometer.start();
        mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long elapsedMillis = SystemClock.elapsedRealtime()
                        - mChronometer.getBase();

                if (elapsedMillis > 50000) {

                }
            }
        });

        if (getIntent().getExtras().getString("orderType").equals("zakazFree")) {


            HashMap<String, String> tarif = pref.getFreeTarif();


            price = Integer.valueOf(tarif.get("min_price"));
            price_for_km = Integer.valueOf(tarif.get("price_for_km"));
            min_km = Integer.valueOf(tarif.get("min_km"));
            minut_price = Integer.valueOf(tarif.get("minut_price"));

            orderId = getIntent().getExtras().getString("orderId");

            checktax = 0;
        }


        if (getIntent().getExtras().getString("orderType").equals("zakaz")) {

            price = Integer.valueOf(getIntent().getExtras().getString("minPrice"));
            price_for_km = Integer.valueOf(getIntent().getExtras().getString("priceForKm"));
            min_km = Integer.valueOf(getIntent().getExtras().getString("minKm"));
            minut_price = Integer.valueOf(getIntent().getExtras().getString("waitMinut"));
            orderId = getIntent().getExtras().getString("orderId");

            butStart.setText("Пауза");

            handler.postDelayed(runnable, 100);

            sendDataOrderToServer("sendDataOrder");

            if (getIntent().getExtras().getString("fix").equals("no")) {
                checktax = 1;
                start = 1;
            } else {
                butStart.setEnabled(false);
                taxomStatus = true;

            }

        }

        if (getIntent().getExtras().getString("orderType").equals("zakazAgain")) {

            price = Integer.valueOf(getIntent().getExtras().getString("minPrice"));
            price_for_km = Integer.valueOf(getIntent().getExtras().getString("priceForKm"));
            min_km = Integer.valueOf(getIntent().getExtras().getString("minKm"));
            minut_price = Integer.valueOf(getIntent().getExtras().getString("waitMinut"));
            orderId = getIntent().getExtras().getString("orderId");

            all_d_speed = Float.valueOf(getIntent().getExtras().getString("trip_km"));
            drive_wait_minut_full = Integer.valueOf(getIntent().getExtras().getString("trip_wait"));

            butStart.setText("Пауза");

            handler.postDelayed(runnable, 100);

            sendDataOrderToServer("sendDataOrder");

            if (getIntent().getExtras().getString("fix").equals("no")) {
                checktax = 1;
                start = 1;
            } else {
                taxomStatus = true;

                butStart.setEnabled(false);
                ;

            }

        }


        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListaner);

        // ????   Location loc = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        manager.addGpsStatusListener(gpsStatusListener);


        checkEnabled();





        butStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (start == 0) {

                    butStart.setText("Пауза");
                    start = 1;
                    if (checktax == 1) {
                        handler.postDelayed(runnable, 100);
                    }
                } else {
                    butStart.setText("Старт");
                    start = 0;
                    if (checktax == 1) {
                        handler.removeCallbacks(runnable);
                    }
                }
            }
        });




        Button tog_finish = (Button) findViewById(R.id.tog_finish);
        tog_finish.setClickable(false);

        tog_finish.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (checktax == 1) {

                    sendDataOrderToServer("sendCompliteOrder");
                }

                if(taxomStatus) {
                    sendDataOrderToServer("sendCompliteOrder");
                    handler.removeCallbacks(runnable);
                }
                String trip_wait = drive_wait_minut_full + "";
                String trip_km = String.format("%.1f", all_d_speed / 1000) + "";
                Intent intent = new Intent(Taxometr.this, activityForResultTripInfo.class);
                intent.putExtra("price", MONEY.getText().toString().trim());
                intent.putExtra("trip_km", trip_km + " км");
                intent.putExtra("trip_wait", tvTimeWait.getText().toString().trim());

                startActivity(intent);
                finish();


            }
        });


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);





        NotificationsHelper.createNotification("Таксометр", Taxometr.class, 0);

    }






    //========================================


    GpsStatus.Listener gpsStatusListener = new GpsStatus.Listener()
    {
        public void onGpsStatusChanged(int event)
        {

            switch (event) {
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    if (mLastLocation != null)
                        gpsFix = (SystemClock.elapsedRealtime() - mLastLocationMillis) < 3000;
                    if (gpsFix) { // A fix has been acquired.



                    }
                    else { // The fix has been lost.
                        outGPS();
                    }

                    break;
            }



        }
    };


    private LocationListener locListaner = new LocationListener() {

        @Override
        public void onLocationChanged(Location argLocation) {
            printLocation(argLocation);
            if (argLocation == null) return;

            mLastLocationMillis = SystemClock.elapsedRealtime();
            mLastLocation = argLocation;
            Log.e("GPS", "> OK" );
            checkEnabled() ;
        }


        @Override
        public void onProviderDisabled(String arg0) {
            // checkEnabled() ;

        }

        @Override
        public void onProviderEnabled(String arg0) {

        }

        @Override
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {


        }
    };

    /**
     * ---------------------------Растчет стоимости поездки при обнаруженных спутниках---------------
     * */
    private void printLocation(Location loc) {
        if (loc != null)
        {

            GPS_COORDS=loc.getLatitude()+"," +loc.getLongitude()+"|";

            if(start==1){
                //---------расчет скорости----------------------
                dist_speed=loc.getSpeed() *( (loc.getTime() - last_time)/1000);
                //---------проверка на огромную скорость------------------

                if (dist_speed>40){dist_speed=0;}

                if (dist_speed>2.5){
                    all_d_speed = all_d_speed + dist_speed;


                    speed_km_h=loc.getSpeed()*3.6;


                    if (all_d_speed>(min_km*1000)){
                        price=price+(dist_speed*price_for_km/1000);

                        tvwait="Расчет по км";

                        drive_wait_minut=0;


                    }

                    else{tvwait="Бесплатные километры";}
                }
                if (dist_speed < 2.5){
                    long wait_minut=   (loc.getTime() - last_time)/1000;
                    if (wait_minut>100){wait_minut=0;}
                    drive_wait_minut=(int) (drive_wait_minut+wait_minut);

                    tvwait="Расчет по времени";

                    if (drive_wait_minut>40){
                        m= minut_price/60*((loc.getTime() - last_time)/1000);
                        price=price+m;
                        drive_wait_minut_full=(int) (drive_wait_minut_full+((loc.getTime() - last_time)/1000));
                        tvwait="ОЖИДАНИЕ ";
                    }

                }


                all=all_d_speed/1000;
                textDistance=String.format("%.1f", all )+" км" ;

                last_time=loc.getTime();
                textTime_wait=(drive_wait_minut_full/60)+ " мин";
            }

            last_long = 0;

        }
               money=String.format("%.0f", price) + " р";
               tvWait.setText(tvwait);
               MONEY.setText(money);
               tvTimeWait.setText(textTime_wait);
               tvDistance.setText(textDistance);
    }

    /**
     * ---------------------------Растчет стоимости поездки без спутников---------------
     * */
    private void outGPS() {

        long l = SystemClock.elapsedRealtime();

        long wait_minut=   (l-last_out_gps_time)/1000;

        if (wait_minut==0){wait_minut=1;}
        if (wait_minut>100){wait_minut=0;}


        if(start==1){


            tvDistance.setText(wait_minut+"55");

            drive_wait_minut=(int) (drive_wait_minut+wait_minut);
            drive_wait_minut_full=(int) (drive_wait_minut_full+wait_minut);
            //if (drive_wait_minut>12){

            price=price+(minut_price/60*wait_minut);

            drive_wait_minut=0;
          //  textTime_wait=(drive_wait_minut_full/60)+" мин";


        }
            last_out_gps_time=l;

            //	tvwait.setText("noGPS "+drive_wait_minut+" - "+drive_wait_minut_full+" "+ wait_minut);
            tvwait="Спутники не доступны ";

            money= String.format("%.0f", price) + " р";

            tvWait.setText(tvwait);
            MONEY.setText(money);
            tvTimeWait.setText(textTime_wait);
            tvDistance.setText(textDistance);



    }

    private void checkEnabled() {
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {

        }

        else {

            //tvEnabledGPS.setTextColor(R.color.fon_red);
            showSettingsAlert();
        }
    }





    @Override
    public void onPause() {
        super.onPause();

        Log.e(TAG, "ONPAUSE");
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.e(TAG, "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //   myThread.run();

        Log.e(TAG, "onResume()");
    }



    @Override
    protected void onStop() {
        super.onStop();

        Log.e(TAG, "onStop()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();


        Log.e(TAG, "onRestart()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        handler.removeCallbacks(runnable);
        Log.e(TAG, "onDestroy()");
    }



    /**
     * ---------------------Вызов настроек жпс ---------------------------------------------
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("Настройки GPS");

        // Setting Dialog Message
        alertDialog.setMessage("GPS не включено. Перейти в меню настроек?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Установки", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                startActivity(new Intent(
                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                dialog.cancel();
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }




    private void sendDataOrderToServer(String command){




        String url = pref.getCityUrl() + Urls.TAKE_ORDER_URL;


        Map<String, String> map = new HashMap<>();

        map.put("command", command);
        map.put("driverId", pref.getDriverId());




        map.put("orderId", orderId);

        map.put("hash", pref.getHash());


        String trip_wait=drive_wait_minut_full+"";
        String trip_km=String.format("%.1f", all_d_speed/1000 )+"";

        map.put("GPS_COORDS", GPS_COORDS);
        map.put("money", price+"");
        map.put("trip_km", trip_km);
        map.put("trip_wait", trip_wait);


        //    Log.e("666", "Autorize - " + map + "\n" + url);


        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url,

                new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //          Log.e("666", "Autorize - " + response);
                        try {
                            String status = response.getString("st");



                            if (status.equals("0")) {


                            }
                            if (status.equals("-1")) {

                            }


                            // ===========
                            if (status.equals("2")) {


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

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        // super.onBackPressed();

        Toast.makeText(getBaseContext(), "Закройте заказ !!!",
                Toast.LENGTH_SHORT).show();

    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
      /* do what you need to do */
            sendDataOrderToServer("sendDataOrder");

            Log.e("666", "sendDataOrder - start");

      /* and here comes the "trick" */
            handler.postDelayed(this, 50000);
        }
    };
}
