package com.github.paveldt.appsistedparking.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.paveldt.appsistedparking.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Arrays;


public class MapFragment extends Fragment implements LocationListener {

    @Override
    public void onLocationChanged(Location location) {
        for (int i = 0; i < 40; i++) {
            Log.i("<<EH>>", "\t\tLOC CHANGED!: " + location);
        }
    }


    private Location userLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // initialize view
        View view = inflater.inflate(R.layout.fragment_map, container, false);


        // initialize map fragment
        final SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.googleMap);
        // use an asynchronous map
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {

                // clear any default items
                googleMap.clear();

                // wait for map to load before adding markers and zooms etc.
                googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        // create latlons for stirling and user location
                        LatLng stirlingLatLng = new LatLng(56.144947260528994, -3.9204526421331267);
                        LatLng userLatLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());

                        // add stirling university marker
                        MarkerOptions stirlingMarkerOpt = new MarkerOptions();
                        stirlingMarkerOpt.position(stirlingLatLng);
                        stirlingMarkerOpt.title("Univ. Stirling");
                        Marker stirlingMarker = googleMap.addMarker(stirlingMarkerOpt);

                        // add user location marker
                        MarkerOptions userMarkerOpt = new MarkerOptions();
                        userMarkerOpt.position(userLatLng);
                        userMarkerOpt.title("Me");
                        Marker userMarker = googleMap.addMarker(userMarkerOpt);

                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        // add stirling lat-long
                        builder.include(stirlingLatLng);
                        builder.include(userLatLng);
                        LatLngBounds bounds = builder.build();


                        // updates camera position to include the user's starting position and
                        // stirling university parking. The padding ensures both locations are visible
                        int padding = 200;
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                        googleMap.moveCamera(cameraUpdate);

                        // todo -- currently doesn't do anything as there is no title etc added
                        // display marker info on the map
                        userMarker.showInfoWindow();
                        stirlingMarker.showInfoWindow();
                    }
                });
            }
        });

        return view;
    }

    public float calcDistanceToLocationKM(LatLng currentLocation, LatLng destination) {
        float[] results = new float[1];
        Location.distanceBetween(currentLocation.latitude, currentLocation.longitude,
                destination.latitude, destination.longitude, results);

        // the Location class api guarantees that there is only 1 result, or it throws an exception
        return results[0];
    }

    public void setUserLocation(Location locUpdate) {
        userLocation = locUpdate;
    }

}