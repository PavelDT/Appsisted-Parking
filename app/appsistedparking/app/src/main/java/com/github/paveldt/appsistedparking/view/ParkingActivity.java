package com.github.paveldt.appsistedparking.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
import com.github.paveldt.appsistedparking.model.User;
import com.github.paveldt.appsistedparking.util.JSONUtil;
import com.github.paveldt.appsistedparking.util.WebRequestQueue;
import com.google.android.material.navigation.NavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
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
    // user data
    User user;
    ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);

        // set up request queue
        queue = WebRequestQueue.getInstance(this);

        Criteria criteria = new Criteria();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        provider = locationManager.getBestProvider(criteria, false);

        // initialise user object holding metadata
        Bundle b = getIntent().getExtras();
        if (b == null) {
            // something went wrong
            throw new RuntimeException("Failed to load user details");
        } else {
            initUser(b.getString("username"), b.getString("location"), b.getString("site"), b.getFloat("balance"));
        }

        // initialize text to speech
        initTTS();

        // user location and map fragment initialisation
        initMapFragment();

        // initialise the parking related fragments
        initParkingFragment();
        initParkedFragment();

        // initialize controls
        initLogoutButton();

        // initialize slide menu
        initToggleDrawer();

        // initialize settings drawer options
        initSettings();

        // initialize update settings button
        initUpdateSettingsButton();

        // set the user's initial balance
        updateUserBalance(0);
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
        // in order to access the drawer panel, the parent menu needs to be referenced
        // access the NavigationView that stores the panel
        final NavigationView parentView = findViewById(R.id.navViewDrawer);
        // use the navigation view to get the header, and then the button in the header
        final Button logoutButton = parentView.getHeaderView(0).findViewById(R.id.logoutBtn);
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
     * Initializes the update settings button in the drawer panel.
     * Enables user to modify their settings
     */
    private void initUpdateSettingsButton() {
        // in order to access the drawer panel, the parent menu needs to be referenced
        // access the NavigationView that stores the panel
        final NavigationView parentView = findViewById(R.id.navViewDrawer);
        // use the navigation view to get the header, and then the button in the header
        final Button updateSettingsButton = parentView.getHeaderView(0).findViewById(R.id.updateSettingsBtn);
        final Spinner locationSpinner = parentView.getHeaderView(0).findViewById(R.id.locationSpinner);
        final Spinner siteSpinner = parentView.getHeaderView(0).findViewById(R.id.siteSpinner);

        final Context context = this;

        updateSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // store the selections of settings
                String location = (String) locationSpinner.getSelectedItem();
                String site = (String) siteSpinner.getSelectedItem();

                String url = "http://10.0.2.2:8080/user/settings/update?username=" + URLEncoder.encode(user.getUsername()) +
                        "&location=" + URLEncoder.encode(location) +
                        "&site=" + URLEncoder.encode(site);

                // update the user object.
                user.updataSettings(location, site);

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        // display a message to the user
                        if (result.toLowerCase().equals("true")) {
                            Toast.makeText(context, "Successfully updated settings", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Failed to updated settings", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Error updating settings " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

                // Add the request to the RequestQueue.
                queue.add(stringRequest);
            }
        });
    }

    /**
     * Initialises user object - lifecycle is one per login
     * @param username - username of user
     * @param location - preferred parking location of user
     * @param site - preferred site of parking of user
     * @param balance - user's balance
     */
    private void initUser(String username, String location, String site, float balance) {
        if (location.equals("none")) {
            user = new User(username);
        } else {
            user = new User(username, location, site, balance);
        }
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
        // safety to ensure that map is ready for usage and distance is ready to be calculated
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
            parkingFragment.park(qrCodeData, user.getUsername());
        } else {
             Toast.makeText(this, "Error processing QRCode", Toast.LENGTH_LONG).show();
        }
    }

    private void suggestParkingLocation() {
        final Context context = this;
        // web request to request parking location.
        // Instantiate the RequestQueue.
        // build request url that requires username and password as params
        String url = "http://10.0.2.2:8080/location/status?location=" + URLEncoder.encode(user.getPreferredLocation()) +
                "&site=" + URLEncoder.encode(user.getPreferredSite()) +
                "&username=" + URLEncoder.encode(user.getUsername());

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

    private void initToggleDrawer() {
        DrawerLayout drawerLayout = findViewById(R.id.parkingLayoutDrawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    /**
     * This function is very overloaded due to the poor web-request framework
     * Initialises settings and allows user to save / update settings.
     */
    private void initSettings() {

        final Context context = this;
        // web request to request parking directory of all locations and sites.
        String url = "http://10.0.2.2:8080/location/";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                if (!result.equals("")) {
                    // parse json array to String array
                    List<String> locations = new ArrayList<>();
                    locations.add("none");
                    locations.addAll(JSONUtil.jsonToStringList(result));

                    // in order to access the drawer panel, the parent menu needs to be referenced
                    // access the NavigationView that stores the panel
                    final NavigationView parentView = findViewById(R.id.navViewDrawer);
                    // use the navigation view to get the header, and then the spinner in the header
                    final Spinner locationSettingSpinner = parentView.getHeaderView(0).findViewById(R.id.locationSpinner);

                    ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, locations);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    //Setting the ArrayAdapter data on the Spinner
                    locationSettingSpinner.setAdapter(adapter);

                    if (!user.getPreferredLocation().equals("none")) {
                        // force the spinners to set the correct value
                        int index = adapter.getPosition(user.getPreferredLocation());
                        locationSettingSpinner.setSelection(index);
                    }

                    // set what to do when an item is selected
                    // another web request for the below dropdown
                    locationSettingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            // set url and encode the location
                            String innerUrl = "http://10.0.2.2:8080/location/site?location=" + URLEncoder.encode(locationSettingSpinner.getSelectedItem().toString());
                            StringRequest stringRequest = new StringRequest(Request.Method.GET, innerUrl, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String result) {
                                    List<String> sites = new ArrayList<>();
                                    sites.add("none");
                                    sites.addAll(JSONUtil.jsonToStringList(result));
                                    // update the dropdown to store the sites for the location
                                    Spinner siteSettingSpinner = parentView.getHeaderView(0).findViewById(R.id.siteSpinner);
                                    ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, sites);
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    //Setting the ArrayAdapter data on the Spinner
                                    siteSettingSpinner.setAdapter(adapter);

                                    if (!user.getPreferredLocation().equals("none") && !user.getPreferredSite().equals("none")) {
                                        // force the spinners to set the correct value
                                        int index = adapter.getPosition(user.getPreferredSite());
                                        siteSettingSpinner.setSelection(index);
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(context, "Error loading settings: " + error.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });

                            // add the 2nd request.
                            queue.add(stringRequest);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });
                } else {
                    String msg = "Failed to get parking status.";
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error loading settings: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    /**
     * Update the user's balance
     * @param change - the change to be subtracted from the user's balance. To add to the balance
     *                 provide a negative number.
     */
    protected void updateUserBalance(float change) {
        float balance = user.getBalance() - change;
        user.setBalance(balance);

        // in order to access the drawer panel, the parent menu needs to be referenced
        // access the NavigationView that stores the panel
        final NavigationView parentView = findViewById(R.id.navViewDrawer);
        // update the
        TextView balanceText = parentView.getHeaderView(0).findViewById(R.id.balanceLabel);
        // double doesn't have to string, so this was used instead
        balanceText.setText("£ " + String.valueOf(balance));

    }
}