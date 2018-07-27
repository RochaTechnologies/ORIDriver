package com.rochatech.library;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rochatech.oridriver.Fragment_Wizard_CreateAccount;
import com.rochatech.oridriver.Fragment_Wizard_CreditCard;
import com.rochatech.oridriver.Fragment_Wizard_ForgotPassword;
import com.rochatech.oridriver.Fragment_Wizard_ProfilePicture;
import com.rochatech.oridriver.R;

public class Support_BottomDialog extends BottomSheetDialogFragment {

    String FragmentName = "";

    public void Support_BottomDialog(String FragmentName) {
        this.FragmentName = FragmentName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.template_bottomdialog_wizard, container, false);
        /*Fragment Setup*/
        Fragment fragment = null;
        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        switch (FragmentName) {
            case "PasswordRecover":
                fragment = new Fragment_Wizard_ForgotPassword();
                break;
            case "ProfilePicture":
                fragment = new Fragment_Wizard_ProfilePicture();
                break;
            case "CreateAccount":
                fragment = new Fragment_Wizard_CreateAccount();
                break;
            case "CreditCard":
                fragment = new Fragment_Wizard_CreditCard();
                break;
        }
        transaction.replace(R.id.dialogframecontainer, fragment);
        transaction.commit();
        /*End Fragment Setup*/
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        getDialog().getWindow().setGravity(Gravity.BOTTOM);
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, (int) (metrics.heightPixels * 0.40));// here i have fragment height 30% of window's height you can set it as per your requirement
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}
