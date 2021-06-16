package com.chatter.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;

import com.chatter.R;
import com.chatter.classes.PermissionUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean permissionDenied = false;

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private Marker marker;
    private Location lastLocation = null;
    private LatLng selectedLocationCoordinates = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        FloatingActionButton buttonSendLocation = findViewById(R.id.buttonSendLocation);
        buttonSendLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("selectedLocation", selectedLocationCoordinates);

                setResult(1, returnIntent);
                finish();

            }
        });

        FloatingActionButton buttonGoToCurrentLocation = findViewById(R.id.buttonGoToCurrentLocation);
        buttonGoToCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();
            }
        });


    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {
                if(marker != null) {
                    marker.remove();
                }
                selectedLocationCoordinates = latLng;
                marker = mMap.addMarker(new MarkerOptions()
                        .position(selectedLocationCoordinates)
                        .title("Selected location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        selectedLocationCoordinates, mMap.getMaxZoomLevel()));
            }
        });
        getDeviceLocation();

    }

    @SuppressLint("VisibleForTests")
    private void getDeviceLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //daca nu are permisiuni - cere
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
            return;
        } else {
            //daca are, du-l la locatie
            fusedLocationClient = new FusedLocationProviderClient(this);
            Task<Location> locationResult = fusedLocationClient.getLastLocation();
            locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        Location aux = task.getResult();
                        if(aux != null) {
                            if (lastLocation == null) { // lastLocation null cand se deschide prima oara activitatea
                                //adaug marker acum la pornire, in rest pune doar utilizatorul
                                selectedLocationCoordinates = new LatLng(aux.getLatitude(), aux.getLongitude());
                                marker = mMap.addMarker(new MarkerOptions()
                                        .position(selectedLocationCoordinates)
                                        .title("Current location"));
                            }
                            lastLocation = task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(lastLocation.getLatitude(),
                                            lastLocation.getLongitude()), 3));

                        }
                    }
                }
            });
        }
    }


    @SuppressLint("VisibleForTests")
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
                fusedLocationClient = new FusedLocationProviderClient(this);
            }
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            enableMyLocation();
        } else {
            permissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (permissionDenied) {
            showMissingPermissionError();
            permissionDenied = false;
        }
    }

    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

}
