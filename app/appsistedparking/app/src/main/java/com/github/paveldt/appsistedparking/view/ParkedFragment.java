package com.github.paveldt.appsistedparking.view;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import android.speech.tts.TextToSpeech;
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
import com.github.paveldt.appsistedparking.model.ParkingLocation;
import com.github.paveldt.appsistedparking.model.ParkingState;
import com.github.paveldt.appsistedparking.util.WebRequestQueue;

public class ParkedFragment extends Fragment {

    String location;
    String site;

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

    public void setLocation(String location) {
        this.location = location;
    }

    public void setSite(String site) {
        this.site = site;
    }

    private void initExitButton(View view) {
        Button exitButton = view.findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                final Context context = getContext();
                RequestQueue queue = WebRequestQueue.getInstance(context);
                String url = "http://10.0.2.2:8080/parking/exit?location=" + location + "&site=" + site;

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        // if exit parking was correct && parking state is parked.
                        if (result.toLowerCase().equals("true")) {
                            ParkingState.getInstance().setParkingState(ParkingState.NOT_PARKED);
                            Toast.makeText(context, "Exited parking lot successfully", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "<--ERROR--> " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

                // get parking state
                int parkingState = ParkingState.getInstance().getParkingState();
                if (parkingState == ParkingState.PARKED) {
                    // Add the request to the RequestQueue.
                    queue.add(stringRequest);
                } else {
                    String msg = "Error - invalid state detected. Exiting parking lot while not parked.";
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}