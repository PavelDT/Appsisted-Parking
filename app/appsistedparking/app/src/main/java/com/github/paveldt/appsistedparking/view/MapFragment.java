package com.github.paveldt.appsistedparking.view;


import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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


public class MapFragment extends Fragment {


    private Location userLocation;
    private final LatLng stirlingLatLng = new LatLng(56.144947260528994, -3.9204526421331267);
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

                // enables zoom controls
                map.getUiSettings().setZoomControlsEnabled(true);

                // wait for map to load before adding markers and zooms etc.
                map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        // creating a reference to the map object is very important and the
                        // OnMapReadCallback is the only opportunity to do so.
                        googleMap = map;
                        updateMapView();
                    }
                });
            }
        });

        return view;
    }

    /**
     * Updates the map view by updating the zoom to ensure that both the user and parking location
     * are visibile.
     */
    public void updateMapView() {

        // remove everything - removes markers, radius notations and info windows.
        googleMap.clear();

        // todo - these latlongs and markers are reused in a parculiar way
        //        they could be initialized only once to save some resources.
        // create latlon for user location
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
        drawParkingRadius(googleMap);

        // updates camera position to include the user's starting position and
        // stirling university parking. The padding ensures both locations are visible
        int padding = 200;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        googleMap.moveCamera(cameraUpdate);

        // display marker info on the map
        stirlingMarker.showInfoWindow();
    }

    /**
     * Checks if the map object is initialized and ready for usage.
     * @return returns true if the map has completed its ready callback.
     */
    public boolean mapReady() {
        return googleMap != null;
    }

    /**
     * Draws a circle around the parking location
     * The circle signifies when the user will be instructed where to park
     * @param googleMap
     */
    private void drawParkingRadius(GoogleMap googleMap) {
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

    /**
     * Calculates direct line distance between the user and the parking location
     * @return float - distance in KM between the user and parking destination
     */
    public float calcDistanceToLocationKM() {

        float[] results = new float[1];
        Location.distanceBetween(userLocation.getLatitude(), userLocation.getLongitude(),
                stirlingLatLng.latitude, stirlingLatLng.longitude, results);

        // the Location class api guarantees that there is only 1 result, or it throws an exception
        return results[0] / 1000;
    }

    public void setUserLocation(Location locUpdate) {
        userLocation = locUpdate;
    }

}