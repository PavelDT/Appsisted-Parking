package com.github.paveldt.appsistedparking.view;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.github.paveldt.appsistedparking.R;
import com.github.paveldt.appsistedparking.model.ParkingLocation;
import com.github.paveldt.appsistedparking.model.ParkingSite;

public class ParkingFragment extends Fragment {

    private ParkingLocation parkingInfo;

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

        return view;
    }

    public void setParkingInfo(ParkingLocation parkingInfo) {
        this.parkingInfo = parkingInfo;
        // Required empty public constructor
    }

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
}