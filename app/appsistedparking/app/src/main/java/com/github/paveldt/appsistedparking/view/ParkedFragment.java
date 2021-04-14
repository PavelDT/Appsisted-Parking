package com.github.paveldt.appsistedparking.view;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.paveldt.appsistedparking.R;
import com.github.paveldt.appsistedparking.model.ParkingState;
import com.github.paveldt.appsistedparking.util.WebRequestQueue;

import java.net.URLEncoder;

public class ParkedFragment extends Fragment {

    private String location;
    private String site;

    /**
     * Required default constructor.
     */
    public ParkedFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_parked, container, false);

        // initializes button to exit the parking lot.
        initExitButton(view);

        return view;
    }

    /**
     * Sets the site which the user parked in
     * @param location - new site
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Sets the site that the user has parked in
     * @param site - new site
     */
    public void setSite(String site) {
        this.site = site;
    }

    /**
     * Initialises the "exit parkingsite" button. Enables user to leave a parking site
     * @param view
     */
    private void initExitButton(View view) {
        Button exitButton = view.findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                final Context context = getContext();
                RequestQueue queue = WebRequestQueue.getInstance(context);
                String url = "http://10.0.2.2:8080/parking/exit?location=" + URLEncoder.encode(location) +
                        "&site=" + URLEncoder.encode(site);

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        // if exit parking was correct && parking state is parked.
                        if (result.toLowerCase().equals("true")) {
                            ParkingState.getInstance().setParkingState(ParkingState.EXITING_PARKING_LOT);
                            Toast.makeText(context, "Exited parking lot successfully", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Error exiting parking site:" + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

                // get parking state
                int parkingState = ParkingState.getInstance().getParkingState();
                if (parkingState == ParkingState.PARKED) {
                    // Add the request to the RequestQueue.
                    queue.add(stringRequest);
                    // update fragments to go back to the map fragment
                    ((ParkingActivity)getActivity()).exitParkingLotFragmentSwitch();
                } else {
                    String msg = "Error - invalid state detected. Exiting parking lot while not parked.";
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}