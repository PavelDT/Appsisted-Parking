package com.github.paveldt.appsistedparking.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.paveldt.appsistedparking.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class ParkingActivity extends AppCompatActivity implements LocationListener {

    MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);

        // user location and map fragment initialisation
        initMapFragment();

        // initialize controls
        initLogoutButton();
    }

    @Override
    public void onLocationChanged(Location location) {
        for (int i = 0; i < 40; i++) {
            Log.i("<<EH>>", "\t\tLOC CHANGED!: " + location);
        }
    }

    private void initLogoutButton() {
        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent parkingIntent = new Intent(ParkingActivity.this, LoginActivity.class);
                startActivity(parkingIntent);
                finish();
            }
        });
    }

    private void initMapFragment() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // wasteful but necessary, creates a client to access location services
            FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            // requests last known location and triggers a callback once the location is found
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {
                        Log.i("WIN!!!", location.toString());

                        // initialize map fragment
                        mapFragment = new MapFragment();
                        mapFragment.setUserLocation(location);

                        // open the map fragment
                        getSupportFragmentManager().beginTransaction().replace(R.id.mapFrame, mapFragment).commit();

                    } else {
                        // nothing to do, location is inaccessible
                        // this can happen on virtual devices or if location services are entirely disabled.
                        Toast.makeText(ParkingActivity.this, "Error fetching location.", Toast.LENGTH_LONG);
                    }
                }
            });
        } else {
            // request permission
            ActivityCompat.requestPermissions(ParkingActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    private boolean locationPermissionCheck() {


        return false;
    }
}