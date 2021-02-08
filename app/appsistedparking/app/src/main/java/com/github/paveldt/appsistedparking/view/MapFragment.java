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


public class MapFragment extends Fragment implements LocationListener {

    private LocationManager locationManager;
    private Location userLocation;

    @Override
    public void onLocationChanged(Location location) {
        userLocation = location;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // initialize view
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // todo -- test
        statusCheck();

        // initialize map fragment
        final SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.googleMap);
        // use an asynchronous map
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {

                // clear any default items
                googleMap.clear();


                // lattitude and longtitude of the university
                LatLng parkingDestination = new LatLng(56.144947260528994, -3.9204526421331267);
                // user's current lattitude and longtitude
                // todo
                // LatLng userPosition = getLocation();

                // add marker for stirling university
                MarkerOptions stirlingMarkerOpt = new MarkerOptions();
                stirlingMarkerOpt.position(parkingDestination);
                Marker stirlingMarker = googleMap.addMarker(stirlingMarkerOpt);

                // todo -- remove this once the bound camera works
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(parkingDestination, 13));


                // set camera between current user's position and the parking location
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                // add stirling lat-long
                builder.include(parkingDestination);
                // add user's lat-long
                // todo -- add this back once permissions are sorted
//                if (userPosition != null) {
//                    builder.include(userPosition);
//                }
//                LatLngBounds bounds = builder.build();
//                // create a camera update
//                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 13);
//                // update the camera
//                googleMap.moveCamera(cameraUpdate);
////                stirlingMarker.showInfoWindow();

                // todo remove this
                // testing onclick for api keey
                //googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                //    @Override
                //    public void onMapClick(LatLng latLng) {
                //        MarkerOptions mo = new MarkerOptions();
                //        mo.position(latLng);
                //
                //        googleMap.clear();
                //
                //        mo.title("TEST " + latLng.latitude + " " + latLng.longitude);
                //
                //        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                //        googleMap.addMarker(mo);
                //    }
                //});

            }
        });

        return view;
    }

    // based on https://stackoverflow.com/questions/32290045
    protected LatLng getLocation() {

        if (checkLocationPermission()) {

            locationManager = (LocationManager) this.getContext().getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true));

            //You can still do this if you like, you might get lucky:
            Location location = locationManager.getLastKnownLocation(bestProvider);
            locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
            double lat = location.getLatitude();
            double lon = location.getLongitude();
            return new LatLng(lat, lon);
        } else {
            return null;
        }
    }

    private boolean checkLocationPermission() {
        Log.i("REEE", ""  + (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)));
        Log.i("REEE", ""  + (PackageManager.PERMISSION_GRANTED));


        if(ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
           return true;
        } else {
            Log.i("AAAAAADFASDf", "bb");
            String[] permissions = new String[]{"android.permission.ACCESS_FINE_LOCATION"};
            ActivityCompat.requestPermissions(this.getActivity(), permissions, 0);
        }

        return ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}