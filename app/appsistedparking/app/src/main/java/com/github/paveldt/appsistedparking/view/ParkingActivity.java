package com.github.paveldt.appsistedparking.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

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
    }
}