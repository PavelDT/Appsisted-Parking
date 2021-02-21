package com.github.paveldt.appsistedparking.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.github.paveldt.appsistedparking.R;
import com.github.paveldt.appsistedparking.model.ParkingLocation;

public class ParkingFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // initialize view
        // todo -- update fragment_map resource to a new one.
        View view = inflater.inflate(R.layout.fragment_parking, container, false);

        return view;
    }

    // todo -- this flops and crashes WIP
    public void updateParkingRecommendation(ParkingLocation parkingLocation) {
        TextView parkingRecomendationText = getActivity().findViewById(R.id.parkingRecommendation);

        // todo -- format this properly
        parkingRecomendationText.setText(parkingLocation.toString());
    }
}
