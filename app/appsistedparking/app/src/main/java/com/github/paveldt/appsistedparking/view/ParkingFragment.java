package com.github.paveldt.appsistedparking.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.paveldt.appsistedparking.R;
import com.github.paveldt.appsistedparking.model.ParkingLocation;
import com.github.paveldt.appsistedparking.model.ParkingSite;
import com.github.paveldt.appsistedparking.model.ParkingState;
import com.github.paveldt.appsistedparking.util.WebRequestQueue;
import com.google.zxing.integration.android.IntentIntegrator;

import java.net.URLEncoder;

public class ParkingFragment extends Fragment {

    private ParkingLocation parkingInfo;

    /**
     * Required empty constructor.
     */
    public ParkingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_parking, container, false);

        TextView parkingInfoTextview = view.findViewById(R.id.parkingInfo);
        String locationTxt = "Lot: " + parkingInfo.getRecommendedLocation();
        parkingInfoTextview.setText(locationTxt);

        TextView availableSpotsTextView = view.findViewById(R.id.spotsAvailableInfo);
        String spotsTxt = "Spots: " + parkingInfo.getAvailableSpots();
        availableSpotsTextView.setText(spotsTxt);

        populateOtherLocationsTable((TableLayout) view.findViewById(R.id.otherLocationsTable));

        // initialise QR scanning button
        initQRScanButton(view);

        return view;
    }

    /**
     * Updates parking info - used by parent activity.
     * @param parkingInfo - new parking info
     */
    public void setParkingInfo(ParkingLocation parkingInfo) {
        this.parkingInfo = parkingInfo;
    }

    /**
     * Ppopulates the table storing information about all other parking sites.
     * @param table - Table control to populate.
     */
    private void populateOtherLocationsTable(TableLayout table) {

        // create a header
        TableRow header = new TableRow(this.getContext());
        TextView headerCell1 = new TextView(this.getContext());
        headerCell1.setText("Site");
        headerCell1.setTextColor(Color.BLACK);
        headerCell1.setTextSize(20);
        TextView headerCell2 = new TextView(this.getContext());
        headerCell2.setText("Spots Available");
        headerCell2.setTextColor(Color.BLACK);
        headerCell2.setTextSize(20);
        header.addView(headerCell1);
        header.addView(headerCell2);
        table.addView(header);

        for (ParkingSite p : parkingInfo.getParkingSites()) {
            // row 1 is the parking site name
            TableRow row = new TableRow(this.getContext());
            TextView cell1 = new TextView(this.getContext());
            cell1.setTextSize(20);
            cell1.setText(p.getSite() + " ");
            // row 2 is the number of available spots
            TextView cell2 = new TextView(this.getContext());
            cell2.setTextSize(20);
            cell2.setText(p.getAvailable() + "");

            // colour the row based on how many spots are available
            if (p.getAvailable() < 10) {
                cell2.setTextColor(Color.argb(255, 80,0,0));
            } else if (p.getAvailable() < 50) {
                cell2.setTextColor(Color.argb(255, 255,140,0));
            } else {
                cell2.setTextColor(Color.argb(255, 51,102,0));
            }

            // add the items to the row
            row.addView(cell1);
            row.addView(cell2);
            // add row to table
            table.addView(row);
        }
    }

    /**
     * Initialises Scan for QR Code button.
     * @param view - Parent view of the button.
     */
    private void initQRScanButton(View view) {
        Button qrScanButton = view.findViewById(R.id.qrScanButton);

        qrScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // implementation based on: youtube.com/watch?v=u2pgSu9RhYo
                // activate camera and look for QR Code
                IntentIntegrator ii = new IntentIntegrator(getActivity());
                ii.setPrompt("Access camera for QR Code scan");
                // set orientation to be locked
                ii.setOrientationLocked(true);
                // set the activity to carry out the capture of the qr code
                ii.setCaptureActivity(QRCaptureActivity.class);
                ii.initiateScan();
            }
        });
    }

    public void park(final String parkingCode, String username) {
        // keep a context to be used for the request
        final Context context = getContext();
        // access the request queue via the singleton class
        RequestQueue queue = WebRequestQueue.getInstance(context);

        // check if the QR Code matches the expected code generated by the webservice
        // URL encoding is required due to the + character.
        String url = "http://10.0.2.2:8080/parking/park?qrCode=" + URLEncoder.encode(parkingCode) +
                "&username=" + URLEncoder.encode(username);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                // result received
                if (result.equals("true")) {

                    ((ParkingActivity)getActivity()).parkFragmentSwitch(parkingCode);

                    // code matches, parking lot's capacity is reduced by 1 via the web service
                    // update the state of the user
                    ParkingState.getInstance().setParkingState(ParkingState.PARKED);

                    // find the correct price of the site that the user is being parking in
                    float price = 1.0f;
                    for (ParkingSite s : parkingInfo.getParkingSites()) {
                        // component 1 = site
                        String parkedAtSite = parkingCode.split("\\+")[1];
                        if (s.getSite().equals(parkedAtSite)) {
                            Log.i("MATCHED", " " + s);
                            price = s.getPrice();
                        }
                    }
                    // update user's balance
                    ((ParkingActivity)getActivity()).updateUserBalance(price);

                    // display a different view
                    Toast.makeText(context, "Parked Successfully!", Toast.LENGTH_SHORT).show();

                } else {
                    String msg = "Error during parking state change.";
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "ERROR verifying parking lot access " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // add the request to the volley request queue
        queue.add(stringRequest);
    }
}