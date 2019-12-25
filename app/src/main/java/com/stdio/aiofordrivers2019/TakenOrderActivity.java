package com.stdio.aiofordrivers2019;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TakenOrderActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener {

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
    public static String orderId;
    private PrefManager pref;
    RequestQueue queue;
    public static String from, toAddress, time, price;
    TextView textDateTime, textFrom, textTo, priceValue, paymentTypeText;
    Toolbar toolbar;

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

        pref = new PrefManager(this);
        queue = Volley.newRequestQueue(this);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        initViews();
        setInfo();
    }

    public void startNavigator(View view) {
        startNavigator(String.valueOf(origin.getLatitude()), String.valueOf(origin.getLongitude()));
    }

    public final void startNavigator(String latitude, String longitude) {
        Uri uri;
        boolean z = true;
        StringBuilder sb = new StringBuilder();
        sb.append("yandexnavi://build_route_on_map?lat_to=");
        sb.append(latitude);
        sb.append("&lon_to=");
        sb.append(longitude);
        uri = Uri.parse(sb.toString());
        Intent intent = new Intent("android.intent.action.VIEW", uri);
        Context context = getApplicationContext();
        List list = null;
        PackageManager packageManager = context != null ? context.getPackageManager() : null;
        if (packageManager != null) {
            list = packageManager.queryIntentActivities(intent, 0);
        }
        if (list == null) {
            Toast.makeText(this, "Не удалось запустить навигатор, т. к. на смартфоне не установлена программа Яндекс.Навигатор", Toast.LENGTH_SHORT).show();
        }
        if (list.size() <= 0) {
            z = false;
        }
        if (z) {
            startActivity(intent);
        }
    }

    private void initViews() {
        textDateTime = findViewById(R.id.textDateTime);
        textFrom = findViewById(R.id.textFrom);
        textTo = findViewById(R.id.textTo);
        priceValue = findViewById(R.id.priceValue);
        paymentTypeText = findViewById(R.id.paymentTypeText);
    }

    private void setInfo() {
        textDateTime.setText(time);
        textFrom.setText(from);
        textTo.setText(toAddress);
        priceValue.setText(price);
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