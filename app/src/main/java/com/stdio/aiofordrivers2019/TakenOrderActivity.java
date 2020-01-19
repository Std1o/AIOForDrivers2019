package com.stdio.aiofordrivers2019;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
import com.stdio.aiofordrivers2019.helper.PrefManager;
import com.stdio.aiofordrivers2019.helper.Urls;
import com.stdio.aiofordrivers2019.model.ModelTakenOrders;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyFactory;
import java.security.Signature;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TakenOrderActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener, View.OnClickListener {

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
    // variables needed to initialize navigation
    private Button button;

    private Marker markerView;
    private Point destinationPoint;
    private Point originPoint;

    public static LatLng origin, destination;
    public static String orderId, statusOrder;
    private PrefManager pref;
    RequestQueue queue;
    public static String from, toAddress, time, price, info, orderPrice;
    TextView textDateTime, textFrom, textTo, priceValue, tvOrderPrice;
    Toolbar toolbar;
    CardView btnChangeOrderStatus;
    TextView orderStatusText;
    ImageView callBtn;

    Dialog dialog;
    private final String PRIVATE_KEY = "MIIBOgIBAAJBAJKSzm8Zi1jw5XyUYZrhapoowd3bdNIWWkgbM26JyWthrKrXUSs5\n" +
            "cqUAhok4r4BxhP7+F6Mv12mCsJsyegzmP9MCAwEAAQJANqGHVfuUZ6sqLfv0QVEh\n" +
            "daIZWELS0PdJ4TRaQCoVK/NkjI5TMvdk5pFWpqshx4l2O/85tGvKuv8BCyTXGXDL\n" +
            "GQIhAMJIl7CH1h9ID9XSWHW8xBarfc7wDyCDOhyGf7y7rs3PAiEAwSJczpSVbtJT\n" +
            "lEgPeegdV0vd4BcKdqBs3Em1U1lUUr0CIQCWRROepNIHC/PDjJiDKGf6qNX8M01f\n" +
            "9mACJD20uu3vnQIgYYqESsUaD4VkNtCKGGyVXQBxB3s7ipwNPthvHrBP+RUCIGzq\n" +
            "VolA40fGYs4K/fCUNysjMDYF8doAYrrIhHo74Kos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //access tokens of your account in strings.xml
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_taken_order);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Заказ №" + orderId);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TakenOrderActivity.super.onBackPressed();
            }
        });

        btnChangeOrderStatus = findViewById(R.id.btnChangeOrderStatus);
        btnChangeOrderStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String command = "";
                if (statusOrder.equals("20")) {
                    command = "changeOrderStatus_30";
                }
                else if (statusOrder.equals("30")) {
                    command = "changeOrderStatus_40";
                }
                else if (statusOrder.equals("40")) {
                    command = "changeOrderStatus_40_2";
                }
                takeOrderFromDriver(orderId, command, "");
            }
        });

        pref = new PrefManager(this);
        queue = Volley.newRequestQueue(this);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        initViews();
        setInfo();
    }

    public void startNavigator(View view) {
        showDialogForSelectNavigationPoint();
    }

    public final void showDialogForSelectNavigationPoint() {
        dialog = new Dialog(this, R.style.CustomDialog);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_for_select_navigation_point);
        Context context = this;
        AssetManager assetManager = null;
        Typeface createFromAsset = Typeface.createFromAsset(context != null ? context.getAssets() : null, "font/roboto_regular.ttf");
        Context context2 =this;
        if (context2 != null) {
            assetManager = context2.getAssets();
        }
        Typeface createFromAsset2 = Typeface.createFromAsset(assetManager, "font/roboto_medium.ttf");
        TextView textView = dialog.findViewById(R.id.title);
        TextView textView2 = dialog.findViewById(R.id.firstButtonText);
        TextView textView3 = dialog.findViewById(R.id.secondButtonText);
        textView.setTypeface(createFromAsset2);
        textView2.setTypeface(createFromAsset);
        textView3.setTypeface(createFromAsset);
        textView2.setOnClickListener(this);
        textView3.setOnClickListener(this);
        dialog.show();
        return;
    }

    public void showInfo(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    public String sha256rsa(String key, String data) throws SecurityException {
        String trimmedKey = key.replaceAll("-----\\w+ PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        try {
            byte[]         result    = Base64.decode(trimmedKey, Base64.DEFAULT);
            KeyFactory factory   = KeyFactory.getInstance("RSA");
            EncodedKeySpec keySpec   = new PKCS8EncodedKeySpec(result);
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(factory.generatePrivate(keySpec));
            signature.update(data.getBytes());

            byte[] encrypted = signature.sign();
            return Base64.encodeToString(encrypted, Base64.NO_WRAP);
        } catch (Exception e) {
            throw new SecurityException("Error calculating cipher data. SIC!");
        }
    }

    public final void startNavigatorForStore(String latitude, String longitude) {
        Uri uri;
        boolean z = true;
        StringBuilder sb = new StringBuilder();
        sb.append("yandexnavi://build_route_on_map?lat_to=");
        sb.append(latitude);
        sb.append("&lon_to=");
        sb.append(longitude);
        sb.append("&client=211");
        uri = Uri.parse(sb.toString());
        sb.append(("&signature=" + sha256rsa(PRIVATE_KEY, uri.toString())));
        uri = Uri.parse(sb.toString());
        Intent intent = new Intent("android.intent.action.VIEW", uri);
        Context context = getApplicationContext();
        List list = null;
        PackageManager packageManager = context != null ? context.getPackageManager() : null;
        if (packageManager != null) {
            list = packageManager.queryIntentActivities(intent, 0);
        }
        if (list == null || list.size() == 0) {
            // Если нет - будем открывать страничку Навигатора в Google Play
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=ru.yandex.yandexnavi"));
        }
        else {
            startActivity(intent);
        }
    }

    private void startNavigatorForClient() {
        Uri uri;
        StringBuilder sb = new StringBuilder();
        sb.append("yandexnavi://build_route_on_map?");
        sb.append("lat_from="+origin.getLatitude());
        sb.append("&lon_from="+origin.getLongitude());
        sb.append("&lat_to=" + destination.getLatitude());
        sb.append("&lon_to=");
        sb.append(destination.getLongitude());
        sb.append("&client=211");
        uri = Uri.parse(sb.toString());
        sb.append(("&signature=" + sha256rsa(PRIVATE_KEY, uri.toString())));
        uri = Uri.parse(sb.toString());
        Intent intent = new Intent("android.intent.action.VIEW", uri);
        Context context = getApplicationContext();
        List list = null;
        PackageManager packageManager = context != null ? context.getPackageManager() : null;
        if (packageManager != null) {
            list = packageManager.queryIntentActivities(intent, 0);
        }
        if (list == null || list.size() == 0) {
            // Если нет - будем открывать страничку Навигатора в Google Play
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=ru.yandex.yandexnavi"));
        }
        else {
            startActivity(intent);
        }
    }

    public final void onClick(View view) {
        dialog.dismiss();

        if (view.getId() == R.id.firstButtonText) {
            startNavigatorForStore(String.valueOf(origin.getLatitude()), String.valueOf(origin.getLongitude()));
        }
        else if (view.getId() == R.id.secondButtonText) {
            startNavigatorForClient();
        }
    }

    private void initViews() {
        textDateTime = findViewById(R.id.textDateTime);
        textFrom = findViewById(R.id.textFrom);
        textTo = findViewById(R.id.textTo);
        priceValue = findViewById(R.id.priceValue);
        orderStatusText = findViewById(R.id.orderStatusText);
        tvOrderPrice = findViewById(R.id.orderPrice);

        callBtn = findViewById(R.id.callBtn);
        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:89529339193"));
                startActivity(intent);
            }
        });
    }

    private void setInfo() {
        textDateTime.setText(time);
        textFrom.setText(from);
        textTo.setText(toAddress);
        priceValue.setText(price);
        tvOrderPrice.setText(orderPrice);
        setNextOrderStatus(statusOrder);
    }

    private void setNextOrderStatus(String status) {
        if(status.equals("20")){
            orderStatusText.setText("Еду к клиенту");
        }

        if(status.equals("30")){
            orderStatusText.setText("Прибыл к клиенту");
        }

        if(status.equals("40")){
            orderStatusText.setText("Ожидание оплаты");
        }
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
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
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

    private void takeOrderFromDriver(final String order, String command, String res) {


        String url = pref.getCityUrl() + Urls.TAKE_ORDER_URL;


        Map<String, String> map = new HashMap<>();


        map.put("driverId", pref.getDriverId());
        map.put("hash", pref.getHash());
        map.put("command", command);
        map.put("order", order);
        map.put("res", res);

        Log.e("666", "Autorize - " + map + "\n" + url);


        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url,

                new JSONObject(map),
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("666", "Autorize - " + response);
                        try {
                            String status = response.getString("st");



                            if (status.equals("-1")) {

                            }

                            if (status.equals("2")) {
                                Intent timerIntent = new Intent(TakenOrderActivity.this, TimerActivity.class);
                                timerIntent.putExtra("minutPrice", response.getInt("minutPrice"));
                                timerIntent.putExtra("orderPrice", response.getInt("orderPrice"));
                                timerIntent.putExtra("order", response.getInt("orderId"));
                                timerIntent.putExtra("waitMinut", response.getInt("waitMinut"));
                                startActivity(timerIntent);
                            }


                            // ===========

                            if (status.equals("3")) {
                                Toast.makeText(TakenOrderActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                            getTakenOrders();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("666", "Autorize - " + e);
                            Toast.makeText(getApplicationContext(), "Ошибка связи с сервером", Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new com.android.volley.Response.ErrorListener() {
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

    private void getTakenOrders() {



        String url = pref.getCityUrl() + Urls.TAKEN_ORDER_LIST_URL;


        Map<String, String> map = new HashMap<>();

        map.put("command", "getDriverOrders");
        map.put("driverId", pref.getDriverId());
        map.put("hash", pref.getHash());


        //    Log.e("666", "Autorize - " + map + "\n" + url);


        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url,

                new JSONObject(map),
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //          Log.e("666", "Autorize - " + response);
                        try {
                            String status = response.getString("st");

                            System.out.println(response);



                            if (status.equals("0")) {

                                JSONArray jArr = response.getJSONArray("orders");
                                for (int i = 0; i < jArr.length(); i++) {
                                    JSONObject obj = jArr.getJSONObject(i);
                                    if (orderId.equals(obj.getString("orderID"))) {
                                        statusOrder = obj.getString("status");
                                        setNextOrderStatus(statusOrder);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("666", "Autorize - " + e);
                            Toast.makeText(getApplicationContext(), "Ошибка связи с сервером", Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new com.android.volley.Response.ErrorListener() {
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
}
