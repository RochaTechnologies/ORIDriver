package com.rochatech.oridriver;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.rochatech.library.Common;
import com.rochatech.webService.*;


public class Fragment_Wizard_ForgotPassword extends Fragment {
    Context context;
    connectToService _svcConnection;
    Common obj;
    String email = "";
    Button btnRecoverPasswordPressed;
    EditText txtRecoverMail;

    public Fragment_Wizard_ForgotPassword() {
        // Required empty public constructor
    }

    /*Initialize instance of the Interface*/
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wizard_forgotpassword, container, false);
        obj = new Common(context);
        _svcConnection = new connectToService(context, obj.GetSharedPreferencesValue(context, "SessionToken"));
        btnRecoverPasswordPressed = view.findViewById(R.id.btnRecoverPasswordPressed);
        txtRecoverMail = view.findViewById(R.id.txtRecoverMail);
        btnRecoverPasswordPressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obj.ShowLoadingScreen(getActivity(),"Enviando correo");
                email = txtRecoverMail.getText().toString();
                String Errors = AnyErrors(email);
                if (Errors.trim().isEmpty()) {
//                    SendRecoverEmail(email);
                    Bundle bundle = new Bundle();
                    bundle.putString("DisplayOpt","RecoverPassword");
                    bundle.putString("RecoverEmail",email);
                    obj.CloseLoadingScreen();
                    Fragment fragment = new Fragment_Wizard_NotificationAlert();
                    fragment.setArguments(bundle);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.animator.anim_slide_inright, R.animator.anim_slide_outleft);
                    transaction.replace(R.id.dialogframecontainer,fragment).commit();
                } else {
                    obj.CloseLoadingScreen();
                    Common.DialogStatusAlert(context, Errors, getResources().getString(R.string.ORIGlobal_AnyErrorsTitle),"Error");
                }
            }
        });

        //region Hide Keyboard
        txtRecoverMail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Common.HideKeyboard(v, context);
            }
        });
        //endregion

        return view;
    }

    //region WebService Call
    private void SendRecoverEmail(String email) {
        _svcConnection.ForgotPassword(email);
    }
    //endregion

    private String AnyErrors(String base) {
        String result = "";
        result = (base.trim().isEmpty()) ? getResources().getString(R.string.WizardForgotPassword_EmptyEmail) : "";
        result = (!Common.IsEmail(base)) ? getResources().getString(R.string.WizardForgotPassword_InvalidEmail) : "";
        return result;
    }
}
