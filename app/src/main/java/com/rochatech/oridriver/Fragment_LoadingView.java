package com.rochatech.oridriver;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Fragment_LoadingView extends Fragment {

    Context context;
    View view;

    public Fragment_LoadingView() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_wizard_profilepicture, container, false);
        Fragment_Wizard_ProfilePicture fragment_wizard_profilePicture = new Fragment_Wizard_ProfilePicture();
        if (getFragmentManager() != null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.animator.anim_slide_inright, R.animator.anim_slide_outleft);
            transaction.replace(R.id.dialogframecontainer,fragment_wizard_profilePicture).commit();
        }
        return view;
    }


}
