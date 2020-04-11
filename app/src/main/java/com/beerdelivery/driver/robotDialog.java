package com.beerdelivery.driver;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;


import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

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
import java.util.List;
import java.util.Map;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.beerdelivery.driver.helper.PrefManager;
import com.beerdelivery.driver.helper.Urls;
import com.beerdelivery.driver.model.ModelOrders;

import retrofit2.Call;
import retrofit2.Callback;

public class robotDialog extends Activity implements OnMapReadyCallback, PermissionsListener {

    String IdOrderToBuy;
    RequestQueue queue;
    private PrefManager pref;
    public static String orderId, from, toAddress, time, price, info, orderPrice, textTariff;
    TextView textDateTime, textFrom, textTo, priceValue, tvOrderPrice, tvTextTariff;

    CardView btnTakeOrderAuto, btnDeclineOrder;
    ImageButton infoButton;

    private Marker markerView;
    private Point destinationPoint;
    private Point originPoint;

    public static LatLng origin, destination;
    // variables for adding location layer
    private MapView mapView;
    private MapboxMap mapboxMap;
    // variables for adding location layer
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    // variables for calculating and drawing a route
    private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";
    private NavigationMapRoute navigationMapRoute;
    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_ToolBar);
        super.onCreate(savedInstanceState);
        //access tokens of your account in strings.xml
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_order_review);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Заказ №" + orderId);
        playDefaultNotificationSound();

        pref = new PrefManager(this);
        queue = Volley.newRequestQueue(this);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(robotDialog.this);

        Intent intent = getIntent();
        String text = "";
        if (intent.hasExtra("orderInfo")) text = intent.getStringExtra("orderInfo");


        if (intent.getExtras().getString("orderId").equals("0")) {
            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
        } else {
            IdOrderToBuy = intent.getExtras().getString("orderId");
        }

        initViews();
        setInfo();
        setButtonListeners();
    }

    private void initViews() {
        textDateTime = findViewById(R.id.textDateTime);
        textFrom = findViewById(R.id.textFrom);
        textTo = findViewById(R.id.textTo);
        priceValue = findViewById(R.id.priceValue);
        btnTakeOrderAuto = findViewById(R.id.btnTakeOrderAuto);
        btnDeclineOrder = findViewById(R.id.btnDeclineOrder);
        infoButton = findViewById(R.id.infoButton);
        tvOrderPrice = findViewById(R.id.orderPrice);
        tvTextTariff = findViewById(R.id.textTariff);
    }

    private void setInfo() {
        textDateTime.setText(time);
        textFrom.setText(from);
        textTo.setText(toAddress);
        priceValue.setText(price);
        tvOrderPrice.setText(orderPrice);
        tvTextTariff.setText(textTariff);
    }

    private void setButtonListeners() {
        btnTakeOrderAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeOrderFromDriver(IdOrderToBuy);
                TakenOrderActivity.origin = origin;
                TakenOrderActivity.destination = destination;
                TakenOrderActivity.orderId = IdOrderToBuy;
                TakenOrderActivity.from = from;
                TakenOrderActivity.toAddress = toAddress;
                TakenOrderActivity.time = time;
                TakenOrderActivity.price = price;
                TakenOrderActivity.statusOrder = "20";
                TakenOrderActivity.info = info;
                TakenOrderActivity.textTariff = textTariff;
                startActivity(new Intent(robotDialog.this, TakenOrderActivity.class));
                finish();
            }
        });

        btnDeclineOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(robotDialog.this);
                builder.setMessage(info)
                        .setIcon(R.drawable.info)
                        .setNegativeButton("Ок",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(getString(R.string.navigation_guidance_day), new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                enableLocationComponent(style);

                // Check marker if existed => delete
                if(destinationPoint!=null)
                    markerView.remove();
                // draw marker
                markerView = mapboxMap.addMarker(new MarkerOptions()
                        .position(destination)
                        .title("Eiffel Tower "));

                destinationPoint = Point.fromLngLat(destination.getLongitude(), destination.getLatitude());
                originPoint = Point.fromLngLat(origin.getLongitude(), origin.getLatitude());

                // get router to draw Direction
                getRoute(originPoint, destinationPoint);
            }
        });

        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(origin)
                .include(destination).build();
        mapboxMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, PointHelper.getPoint().x, 250, 30));
        mapboxMap.setLatLngBoundsForCameraTarget(bounds);
    }

    private void getRoute(Point origin, Point destination) {
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, retrofit2.Response<DirectionsResponse> response) {
                        // You can get the generic HTTP info about the response
                        Log.d(TAG, "Response code: " + response.code());
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Log.e(TAG, "No routes found");
                            return;
                        }
                        currentRoute = response.body().routes().get(0);

                        // Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(TAG, "Error: " + throwable.getMessage());
                    }
                });
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
// Activate the MapboxMap LocationComponent to show user location
// Adding in LocationComponentOptions is also an optional parameter
            locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(this, loadedMapStyle);
            locationComponent.setLocationComponentEnabled(true);
// Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationComponent(mapboxMap.getStyle());
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    private void playDefaultNotificationSound() {
        Uri notification = Uri.parse("android.resource://com.beerdelivery.driver/raw/notify_sound");
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();
    }


    private void takeOrderFromDriver(final String order) {


        String url = pref.getCityUrl() + Urls.TAKE_ORDER_URL;


        Map<String, String> map = new HashMap<>();


        map.put("driverId", pref.getDriverId());
        map.put("hash", pref.getHash());
        map.put("order", order);

        map.put("command", "takeOrderFromDriver");


        Log.e("666", "Autorize - " + map + "\n" + url);


        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url,

                new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //          Log.e("666", "Autorize - " + response);
                        try {
                            String status = response.getString("st");



                            if (status.equals("0")) {
                                Toast.makeText(getBaseContext(),
                                        response.getString("message"),
                                        Toast.LENGTH_SHORT).show();

                            }
                            if (status.equals("-1")) {

                                Toast.makeText(getBaseContext(),
                                        response.getString("message"),
                                        Toast.LENGTH_SHORT).show();
                            }


                            // ===========
                            if (status.equals("2")) {

                            }

                            if (status.equals("3")) {


                            }

                            if (status.equals("4")) {


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
    }
}