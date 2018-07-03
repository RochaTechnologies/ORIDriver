package com.rochatech.oridriver;

//import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rochatech.webService.*;
import com.rochatech.library.Common;

public class Fragment_MainFavContent extends Fragment {

    TextView TotalApproved;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_mainfavcontent, container, false);
        TotalApproved = v.findViewById(R.id.FavPassengerCount);
        try {
            Bundle args = getArguments();
            int totalCount = args.getInt("totalapproved");
            String msg = (totalCount > 1) ? totalCount + " Pasajeros" : totalCount + " Pasajero";
            TotalApproved.setText(msg);
        } catch (Exception e) {

        }
        return v;
    }
}
