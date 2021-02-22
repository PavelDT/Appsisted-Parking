package com.github.paveldt.appsistedparking.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.paveldt.appsistedparking.R;

public class ParkingFragment extends Fragment {

    private String parkingInfo;

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
        View view =  inflater.inflate(R.layout.fragment_parking, container, false);

        TextView parkingInfoTextview = view.findViewById(R.id.parkingInfo);
        parkingInfoTextview.setText(parkingInfo);

        return view;
    }

    public void setParkingInfo(String parkingInfo) {
        this.parkingInfo = parkingInfo;
        // Required empty public constructor
    }
}