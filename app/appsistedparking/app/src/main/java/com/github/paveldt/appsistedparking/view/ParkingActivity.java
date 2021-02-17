package com.github.paveldt.appsistedparking.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.FragmentManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.paveldt.appsistedparking.R;

import java.text.DecimalFormat;
import java.util.Locale;


public class ParkingActivity extends AppCompatActivity implements LocationListener {

    private MapFragment mapFragment;
    private Fragment parkingFragment;
    private LocationManager locationManager;
    private String provider;
    private final DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private int notificationCounter = 0;
    private TextToSpeech tts;
    // tracks if the user is currently parked in one of the parking lots.
    private boolean parked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);

        Criteria criteria = new Criteria();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        provider = locationManager.getBestProvider(criteria, false);

        // initialize text to speech
        initTTS();

        // user location and map fragment initialisation
        initMapFragment();

        // initialise the parking fragment
        initParkingFragment();

        // initialize controls
        initLogoutButton();

        // todo -- remove this
        initTestButton_REMOVE();
    }

    /**
     * Updates map location when the user's location changes
     * @param location - The new location of the user
     */
    @Override
    public void onLocationChanged(Location location) {
        Log.i("LOCATION CHANGED", "-------------------------------------------------------------------");
        // update the map positioning only if the map is ready
        if (mapFragment.mapReady()) {
            mapFragment.setUserLocation(location);
            mapFragment.updateMapView();

            // todo - current implementation relies on a location change
            //        to update the remaining distance.
            //        This is due to the map having to load first befure such
            //        a calculation can occur. Not a problem, but not ideal.
            updateDistanceRemainingTxt();
        }
    }

    /**
     * Required for the location manager to trigger the location changed event when
     * the user's location changes.
     * @throws SecurityException
     */
    @Override
    protected void onResume() throws SecurityException {
        super.onResume();
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    /**
     * Initializes the logout button
     */
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

    /**
     * Initialises the map fragment that holds the google map view
     */
    private void initMapFragment() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // requests last known location and triggers a callback once the location is found
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
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
        } else {
            // request permission
            ActivityCompat.requestPermissions(ParkingActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    /**
     * Updates the distance remaining textbox
     */
    private void updateDistanceRemainingTxt() {
        // safety to ensure that map is ready for usage and idstance is ready to be calculated
        if (!mapFragment.mapReady()) {
            return;
        }

        float distanceRemaining = mapFragment.calcDistanceToLocationKM();
        TextView distanceToLocationTxt = findViewById(R.id.distanceToLocationText);
        // appends a KM to the end of the distance
        distanceToLocationTxt.setText(String.format("%s KM", decimalFormat.format(distanceRemaining)));

        // 1 KM remaining to location, run the parking functionality
        if (distanceRemaining > 1) {
            park();
        }
    }

    private void initParkingFragment() {
        parkingFragment = new ParkingFragment();
    }

    private void park() {
        final Context context = this;
        // web request to request parking location.
        // Instantiate the RequestQueue.
        // todo -- reuse the request queue instead of re-instantiating it
        //         every single time a user registers
        RequestQueue queue = Volley.newRequestQueue(context);
        // build request url that requires username and password as params
        String url = "http://10.0.2.2:8080/parking/locationstatus";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        // todo -- the REST serrvice is currently a place holder
                        //         more processing of the result is required.
                        // feed back given, user can proceed to park
                        if (result.toLowerCase().equals("true")) {
                            // hide the map fragment and show the parking fragment
                            if (!parked) {
                                // there is risk of not processing the update on the google map and thus
                                // losing some state. Losing this state is ok as the google map needs
                                // to be hidden once parking.
                                getSupportFragmentManager().beginTransaction().replace(R.id.mapFrame, parkingFragment).commitAllowingStateLoss();
                                parked = true;
                            }

                            // notification
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, getResources().getString(R.string.notification_channel));
                            builder.setSmallIcon(R.drawable.ic_message_notification);
                            builder.setContentTitle("Appsisted Parking");
                            builder.setContentText("Park at location " + "A");
                            builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

                            NotificationManager notificationManager = getSystemService(NotificationManager.class);
                            // incrementing notification count means that the notification can be re-issued multiple times
                            notificationManager.notify(notificationCounter, builder.build());
                            notificationCounter++;

                            // tell the user where to park via TTS
                            tts.speak("Please park at parking location one", TextToSpeech.QUEUE_FLUSH, null);

                        } else {
                            String msg = "Failed to get parking status.";
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "<--ERROR--> " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    /**
     * Initializes the text to speach object. Important - speech rate is set to 0.7f.
     */
    private void initTTS() {
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                // initialising TTS is complex and doesn't work on all devices
                // verify the initialization worked.
                if (status == TextToSpeech.SUCCESS) {
                    int lang = tts.setLanguage(Locale.ENGLISH);
                    // Slow down TTS speed
                    tts.setSpeechRate(0.7f);
                }
            }
        });
    }

    // todo -- remove this
    private void initTestButton_REMOVE() {
        Button logoutButton = findViewById(R.id.TEST_BTN);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                park();
            }
        });
    }
}