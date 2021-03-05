package com.github.paveldt.appsistedparking.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.paveldt.appsistedparking.R;
import com.github.paveldt.appsistedparking.model.ParkingLocation;
import com.github.paveldt.appsistedparking.model.ParkingState;
import com.github.paveldt.appsistedparking.util.WebRequestQueue;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Locale;


public class ParkingActivity extends AppCompatActivity implements LocationListener {

    private MapFragment mapFragment;
    private ParkingFragment parkingFragment;
    private ParkedFragment parkedFragment;
    private LocationManager locationManager;
    private String provider;
    private final DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private int notificationCounter = 0;
    private TextToSpeech tts;
    // tracks if the user is currently parked in one of the parking lots.
    private ParkingState parkingState = ParkingState.getInstance();
    private RequestQueue queue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);

        // set up request queue
        queue = WebRequestQueue.getInstance(this);

        Criteria criteria = new Criteria();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        provider = locationManager.getBestProvider(criteria, false);

        // initialize text to speech
        initTTS();

        // user location and map fragment initialisation
        initMapFragment();

        // initialise the parking related fragments
        initParkingFragment();
        initParkedFragment();

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
                // reset state when logging out.
                parkingState.setParkingState(ParkingState.NOT_PARKED);
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
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentFrame, mapFragment).commit();

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

        // 1 KM remaining to location and user is not parked
        // run the parking suggestion functionality
        if (distanceRemaining < 1 && parkingState.getParkingState() == ParkingState.NOT_PARKED) {
            suggestParkingLocation();
        }

        // when exiting the parking lot, once further than 1.05 KM (1km and 50 meters) change the
        // state to not_parked.
        if (parkingState.getParkingState() == ParkingState.EXITING_PARKING_LOT && distanceRemaining > 1.05) {
            parkingState.setParkingState(ParkingState.NOT_PARKED);
        }
    }

    private void initParkingFragment() {
        parkingFragment = new ParkingFragment();
    }

    private void initParkedFragment() {
        parkedFragment = new ParkedFragment();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // intent to handle the result
        IntentResult ir = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // check if data was back
        if (ir.getContents() != null) {
            String qrCodeData = ir.getContents();
            Log.i("QRCode ", "successfully read qr code: " + qrCodeData);
            // change state of parking
            parkingFragment.park(qrCodeData);
        } else {
             Toast.makeText(this, "Error processing QRCode", Toast.LENGTH_LONG).show();
        }
    }

    private void suggestParkingLocation() {
        final Context context = this;
        // web request to request parking location.
        // Instantiate the RequestQueue.
        // build request url that requires username and password as params
        String parkingLocationName = "stirling";
        String url = "http://10.0.2.2:8080/parking/locationstatus?location=" + parkingLocationName;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                // feed back given, user can proceed to park
                if (!result.toLowerCase().trim().equals("error") && !result.isEmpty()) {
                    // hide the map fragment and show the parking fragment
                    // there is risk of not processing the update on the google map and thus
                    // losing some state. Losing this state is ok as the google map needs
                    // to be hidden once parking.
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentFrame, parkingFragment).commitAllowingStateLoss();

                    // parse the json response into an ojbect and send it to the parking fragment
                    ParkingLocation pl = new ParkingLocation(result);
                    parkingFragment.setParkingInfo(pl);

                    // notification
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, getResources().getString(R.string.notification_channel));
                    builder.setSmallIcon(R.drawable.ic_message_notification);
                    builder.setContentTitle("Parking Recommendation");
                    builder.setContentText("Park at lot " + pl.getRecommendedLocation() +
                                           " with " + pl.getAvailableSpots() + " available spots.");
                    builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                    // incrementing notification count means that the notification can be re-issued multiple times
                    notificationManager.notify(notificationCounter, builder.build());
                    notificationCounter++;

                    // tell the user where to park via TTS
                    String txt = "Please park at parking lot " + pl.getRecommendedLocation().toLowerCase() +
                                 " with " + pl.getAvailableSpots() + " available spots";
                    tts.speak(txt, TextToSpeech.QUEUE_FLUSH, null);

                    // update parking state
                    parkingState.setParkingState(ParkingState.PARKING);
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

    public void parkFragmentSwitch(String parkingCode) {
        // use QR code for site and location
        String[] data = parkingCode.split("\\+");
        parkedFragment.setLocation(data[0]);
        parkedFragment.setSite(data[1]);

        // switch views
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentFrame, parkedFragment).commitAllowingStateLoss();
    }

    public void exitParkingLotFragmentSwitch() {
        // switch from parked fragment to map fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentFrame, mapFragment).commitAllowingStateLoss();
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
        Button testButton = findViewById(R.id.TEST_BTN);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                suggestParkingLocation();
            }
        });
    }
}