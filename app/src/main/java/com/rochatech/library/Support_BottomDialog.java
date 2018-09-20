package com.rochatech.library;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.rochatech.oridriver.Fragment_LoadingView;
import com.rochatech.oridriver.Fragment_Wizard_CreateAccount;
import com.rochatech.oridriver.Fragment_Wizard_CreditCard;
import com.rochatech.oridriver.Fragment_Wizard_ForgotPassword;
import com.rochatech.oridriver.Fragment_Wizard_NotificationAlert;
import com.rochatech.oridriver.Fragment_Wizard_ProfilePicture;
import com.rochatech.oridriver.R;

@SuppressWarnings({"WeakerAccess","unused"})
public class Support_BottomDialog extends BottomSheetDialogFragment {

    String FragmentName = "";

//    public void Support_BottomDialog(String FragmentName) {
//        this.FragmentName = FragmentName;
//    }
    public void Support_BottomDialog_SetFragmentName(String fragName) {this.FragmentName = fragName;}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.template_bottomdialog_wizard, container, false);
        /*Fragment Setup*/
        Fragment fragment = null;
//        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
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
            case "LoadingView":
                fragment = new Fragment_LoadingView();
                break;
            case "Alert":
                fragment = new Fragment_Wizard_NotificationAlert();
                break;
        }
        transaction.setCustomAnimations(R.animator.anim_slide_inright, R.animator.anim_slide_outleft);
        transaction.replace(R.id.dialogframecontainer, fragment).commit();
        /*End Fragment Setup*/
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        DisplayMetrics metrics = new DisplayMetrics();
//        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        getDialog().getWindow().setGravity(Gravity.BOTTOM);
//        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, (int) (metrics.heightPixels * 0.40));// here i have fragment height 30% of window's height you can set it as per your requirement
//        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}
