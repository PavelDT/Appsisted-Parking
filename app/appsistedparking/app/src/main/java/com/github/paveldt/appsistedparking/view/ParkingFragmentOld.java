package com.github.paveldt.appsistedparking.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.github.paveldt.appsistedparking.R;

public class ParkingFragmentOld extends Fragment {

    private String jsonInfo = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // initialize view
        // todo -- update fragment_map resource to a new one.
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        Log.i("???????xxxx", (getActivity() == null) + "");

        TextView parkingRecomendationText = view.findViewById(R.id.fragmentFrame);
//        TextView parkingRecomendationText2 = getChildFragmentManager().findFragmentById(R.id.ZZZZZ).getview;
        Log.i("???????xxxx", (parkingRecomendationText == null) + "");
//        Log.i("???????xxxx", (parkingRecomendationText2 == null) + "");

        parkingRecomendationText.setText(jsonInfo);

        return view;
    }

    // todo -- this flops and crashes WIP
    public void updateParkingRecommendation(String jsonResult) {

        jsonInfo = jsonResult;
//        Log.i("YE 👀: ", (aa == null) + "");
//        Log.i("YE 👀: ", (aa.findViewById(R.id.parkingRecommendation) == null) + "");

//        TextView parkingRecomendationText = aa.findViewById(R.id.parkingRecommendation);
//
//
//        // todo -- format this properly
//        parkingRecomendationText.setText(parkingLocation.toString());
    }
}
