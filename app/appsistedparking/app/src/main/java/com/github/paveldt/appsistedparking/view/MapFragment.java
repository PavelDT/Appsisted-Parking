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

        // initialize map fragment
        final SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.googleMap);
        // use an asynchronous map
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {

                // clear any default items
                googleMap.clear();


//                // lattitude and longtitude of the university
//                LatLng parkingDestination = new LatLng(56.144947260528994, -3.9204526421331267);
//                // add marker for stirling university
//                MarkerOptions stirlingMarkerOpt = new MarkerOptions();
//                stirlingMarkerOpt.position(parkingDestination);
//                Marker stirlingMarker = googleMap.addMarker(stirlingMarkerOpt);
//
//                MarkerOptions userMarkerOpt = new MarkerOptions();
//                userMarkerOpt.position(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()));
//                Marker userMarker = googleMap.addMarker(stirlingMarkerOpt);
//
//                // todo -- remove this once the bound camera works
//                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(parkingDestination, 2));


                // set camera between current user's position and the parking location

                // create a camera update

                // update the camera

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


//                stirlingMarker.showInfoWindow();

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

    public void setUserLocation(Location locUpdate) {
        userLocation = locUpdate;
    }

//    // based on https://stackoverflow.com/questions/32290045
//    private LocationManager locationManager;
//    protected LatLng getLocation() {
//
//        if (checkLocationPermission()) {
//
//            locationManager = (LocationManager) this.getContext().getSystemService(Context.LOCATION_SERVICE);
//            Criteria criteria = new Criteria();
//            String bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true));
//
//            //You can still do this if you like, you might get lucky:
//            Location location = locationManager.getLastKnownLocation(bestProvider);
//            locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
//            double lat = location.getLatitude();
//            double lon = location.getLongitude();
//            return new LatLng(lat, lon);
//        } else {
//            return null;
//        }
//    }
//
//    private boolean checkLocationPermission() {
//        Log.i("REEE", ""  + (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)));
//        Log.i("REEE", ""  + (PackageManager.PERMISSION_GRANTED));
//
//
//        if(ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//           return true;
//        } else {
//            Log.i("AAAAAADFASDf", "bb");
//            String[] permissions = new String[]{"android.permission.ACCESS_FINE_LOCATION"};
//            ActivityCompat.requestPermissions(this.getActivity(), permissions, 0);
//        }
//
//        return ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
//    }
//
//    public void statusCheck() {
//        final LocationManager manager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
//
//        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            buildAlertMessageNoGps();
//
//        }
//    }
//
//    private void buildAlertMessageNoGps() {
//        final AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
//        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
//                .setCancelable(false)
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    public void onClick(final DialogInterface dialog, final int id) {
//                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                    }
//                })
//                .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    public void onClick(final DialogInterface dialog, final int id) {
//                        dialog.cancel();
//                    }
//                });
//        final AlertDialog alert = builder.create();
//        alert.show();
//    }
}