package ng.riby.androidtest.ui;

import android.Manifest;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import ng.riby.androidtest.R;
import ng.riby.androidtest.viewmodels.MapActivityViewModel;

public class MapActivity extends AppCompatActivity implements View.OnClickListener, RoutingListener {

    private static final String TAG = "MapActivity";

    private GoogleMap mMap;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private boolean mLocationPermission = false;
//    private FusedLocationProviderClient mLocationClient;
    private Location location1, location2;
    private static final float ZOOM = 15f;
    private List<Polyline> polylines;
    private static final int DISTANCE_COLOR = Color.BLACK;

    private MarkerOptions startMarker, stopMarker;

    boolean isStarted = true;
    boolean isGpsAvailable = false;

    private TextView distanceText;
    private Button startStop;

    private MapActivityViewModel mapActivityViewModel;
    private boolean isNetworkAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        startStop = findViewById(R.id.start_stop);
        distanceText = findViewById(R.id.distance_text);

        mapActivityViewModel = ViewModelProviders.of(this).get(MapActivityViewModel.class);
        mapActivityViewModel.initialize(this);

        mapActivityViewModel.getNetworkStatus().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(!aBoolean) {
                    isNetworkAvailable = false;
                } else {
                    isNetworkAvailable = true;
                }
            }
        });

        mapActivityViewModel.getGpsStatus().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(!aBoolean){
                    isGpsAvailable = false;
                    AlertDialog dialog = new AlertDialog.Builder(MapActivity.this)
                            .setMessage("You need to enable your GPS to use this service")
                            .setPositiveButton("Enable GPS", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            })
                            .setNegativeButton("cancel", null)
                            .create();
                    dialog.show();
                }else{
                    isGpsAvailable = true;
                }
            }
        });

//        mLocationClient = new FusedLocationProviderClient(this);
        getLocationPermission();
        startStop.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if(view == startStop){
            if(isStarted) {
                getDeviceLocation();
                startStop.setText(R.string.stop);
            }else{
                mapActivityViewModel.getFusedLocationProviderClient().getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                if(task.isSuccessful()){
                                    location2 = task.getResult();
                                    if(isGpsAvailable) {
                                        plantSecondMarker(new LatLng(location2.getLatitude(), location2.getLongitude()), "Stop");
                                    }
                                    startStop.setText(R.string.start);
//                                        startStop.setBackground();
                                    generateRoute().execute();
                                    String distance = mapActivityViewModel.calculateDistanceCovered(location1, location2);
                                    distanceText.setText(distance);
                                    isStarted = true;
                                }else{
                                    Toast.makeText(MapActivity.this, "Couldn't find location", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );            }
        }
    }

    public Routing generateRoute(){
        Routing route = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.WALKING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(startMarker.getPosition(), stopMarker.getPosition())
                .build();
        return route;
    }

    private void plantFirstMarker(LatLng latLng, String markerTitle){
        startMarker = new MarkerOptions()
                .position(latLng)
                .title(markerTitle);
        mMap.addMarker(startMarker);
    }

    private void plantSecondMarker(LatLng latLng, String markerTitle){
        stopMarker = new MarkerOptions()
                .position(latLng)
                .title(markerTitle);
        mMap.addMarker(stopMarker);
        mapActivityViewModel.insertLocation(location1, location2);
    }

//    This will be called when the start button is pushed
    private void moveToLocation(LatLng latLng, String markerTitle){
        if(startMarker != null){
            if(startMarker.isVisible()){
                startMarker.visible(false);
            }
        }

        if(stopMarker != null){
            if(stopMarker.isVisible()){
                stopMarker.visible(false);
            }
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM));
        plantFirstMarker(latLng, markerTitle);
    }

    public void getDeviceLocation(){
        try{
            if(mLocationPermission){
                Task location = mapActivityViewModel.getFusedLocationProviderClient().getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Location> task) {
                        if(task.isSuccessful()){
                            Location myLocation = task.getResult();
                            if(isStarted) {
                                location1 = myLocation;
                                moveToLocation(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), "Start");
                                isStarted = false;
                            }
                        }else{
                            Toast.makeText(MapActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        }catch (Exception e){

        }
    }



    private void getLocationPermission(){
        Log.i(TAG, "initializeMap: getting location permission... \n ......................");
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this, FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this, COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermission = true;
                initializeMap();
            }else{
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void initializeMap(){
        Log.i(TAG, "initializeMap: initializing Map... \n ......................");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Toast.makeText(MapActivity.this, "Your map is ready", Toast.LENGTH_SHORT).show();
                mMap = googleMap;
                mMap.setMyLocationEnabled(true);
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermission = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermission = false;
                            return;
                        }
                    }
                    mLocationPermission = true;
//                    initialize the map
                    Log.i(TAG, "initializeMap: initializing Map... \n ......................");
                    initializeMap();
                }
        }

    }


    @Override
    public void onRoutingFailure(RouteException e) {
        if(e != null){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> routes, int index) {
        if(polylines.size() > 0){
            for(Polyline line : polylines){
                line.remove();
            }
        }

        polylines = new ArrayList<>();
//        This portion will add the route to the map
        for(int i = 0; i < routes.size(); i++){

            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.color(getResources().getColor(i));
            polylineOptions.width(10 + i * 3);
            polylineOptions.addAll(routes.get(i).getPoints());
            Polyline line = mMap.addPolyline(polylineOptions);
            polylines.add(line);

            Toast.makeText(this, "Distance covered: "+ routes.get(i).getDistanceValue(), Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onRoutingCancelled() {
        for(Polyline line : polylines){
            line.remove();
        }
        polylines.clear();
    }
}
