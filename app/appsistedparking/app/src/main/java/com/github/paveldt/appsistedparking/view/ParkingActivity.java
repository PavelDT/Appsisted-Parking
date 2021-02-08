package com.github.paveldt.appsistedparking.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.paveldt.appsistedparking.R;

public class ParkingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);

        // initialize map fragment
        Fragment mapFragment = new MapFragment();
        // open the map fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.mapFrame, mapFragment).commit();

        // initialize controls
        initLogoutButton();


        Log.i("REEEff", "" + (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ));
        Log.i("REEEff", ""  + (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED));

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
}