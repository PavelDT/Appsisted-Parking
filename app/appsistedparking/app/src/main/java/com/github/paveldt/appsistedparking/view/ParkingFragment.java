package com.github.paveldt.appsistedparking.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.github.paveldt.appsistedparking.R;

public class ParkingFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // initialize view
        // todo -- update fragment_map resource to a new one.
        View view = inflater.inflate(R.layout.fragment_parking, container, false);

        return view;
    }
}
