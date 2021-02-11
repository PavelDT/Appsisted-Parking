package com.github.paveldt.appsistedparking.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Arrays;


public class MapFragment extends Fragment {


    private Location userLocation;
    private static GoogleMap googleMap;

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
            public void onMapReady(final GoogleMap map) {
                // creating a reference to the map object is very important and the
                // OnMapReadCallback is the only opportunity to do so.
                googleMap = map;
                // enables zoom controls
                googleMap.getUiSettings().setZoomControlsEnabled(true);

                // wait for map to load before adding markers and zooms etc.
                googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        updateMapView();
                    }
                });
            }
        });

        return view;
    }

    public void updateMapView() {

        // remove everything - removes markers, radius notations and info windows.
        googleMap.clear();

        // todo - these latlongs and markers are reused in a parculiar way
        //        they could be initialized only once to save some resources.
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

        // draw a circle around the parking location to signify how close the
        // user has to get to the parking location before being instructed
        // where to park
        drawParkingRadius(stirlingLatLng, googleMap);

        // updates camera position to include the user's starting position and
        // stirling university parking. The padding ensures both locations are visible
        int padding = 200;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        googleMap.moveCamera(cameraUpdate);

        // display marker info on the map
        stirlingMarker.showInfoWindow();
    }

    /**
     * Draws a circle around the parking location
     * The circle signifies when the user will be instructed where to park
     * @param stirlingLatLng
     * @param googleMap
     */
    private void drawParkingRadius(LatLng stirlingLatLng, GoogleMap googleMap) {
        // set the size of the radius and its center
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(stirlingLatLng);
        // todo -- 1km was picked arbitrarily
        circleOptions.radius(1000);

        // colour and border options of the circle
        circleOptions.strokeColor(Color.BLACK);
        circleOptions.fillColor(Color.argb(30, 66, 173, 244));
        circleOptions.strokeWidth(10);

        // add the circle to the map
        googleMap.addCircle(circleOptions);
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