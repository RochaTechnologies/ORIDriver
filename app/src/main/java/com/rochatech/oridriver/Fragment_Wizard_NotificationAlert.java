package com.rochatech.oridriver;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class Fragment_Wizard_NotificationAlert extends Fragment {

    Context context;
    View view;
    TextView txtTitleMsg, txtSubtitle1Msg, txtSubtitle2Msg, txtNoteMsg;
    Button btnPrimaryAction;

    /**/
    String DisplayOpt;
    String Email;
    String GivenName;
    String Gender;

    public Fragment_Wizard_NotificationAlert() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_wizard_notificationalert, container, false);
        FragInitAppControlers();
        Bundle bundle = getArguments();
        if (bundle != null) {
            DisplayOpt = bundle.getString("DisplayOpt");
            if (DisplayOpt != null) {
                if (DisplayOpt.contains("RecoverPassword")) {
                    Email = bundle.getString("RecoverEmail");
                    txtTitleMsg.setText(getResources().getString(R.string.ORIFragment_NotificationAlert_EmailSend));
                    txtSubtitle1Msg.setText(getResources().getString(R.string.ORIFragment_NotificationAlert_EmailInstructions));
                    String instructions2 = "se enviaron al correo " + Email;
                    txtSubtitle2Msg.setText(instructions2);
                    txtNoteMsg.setVisibility(View.INVISIBLE);
                    btnPrimaryAction.setText(getResources().getString(R.string.ORIUnderstoodString));
                } else if (DisplayOpt.contains("AccountCreated")) {
                    Email = bundle.getString("ori_createdemail");
                    GivenName = bundle.getString("ori_createdname");
                    Gender = bundle.getString("ori_createdgender");
                    if (Gender != null) {
                        if (Gender.contains("H")) {
                            String maleWelcome = "¡Bienvenido " + GivenName + " !";
                            txtTitleMsg.setText(maleWelcome);
                        } else if (Gender.contains("M")) {
                            String femaleWelcome = "¡Bienvenida " + GivenName + " !";
                            txtTitleMsg.setText(femaleWelcome);
                        }
                    }
                    txtSubtitle1Msg.setText(getResources().getString(R.string.ORIFragment_NotificationAlert_ConfirmSend));
                    txtSubtitle2Msg.setText(Email);
                    txtNoteMsg.setVisibility(View.GONE);
                    btnPrimaryAction.setText(getResources().getString(R.string.ORIUnderstoodString));
                }
            }
        }
        btnPrimaryAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    ((Wizard_Login)getActivity()).CreateAccount_CloseWizardDialog();
                }
            }
        });
        return view;
    }

    private void FragInitAppControlers() {
        txtTitleMsg = view.findViewById(R.id.txtTitleMsg);
        txtSubtitle1Msg = view.findViewById(R.id.txtSubtitle1Msg);
        txtSubtitle2Msg = view.findViewById(R.id.txtSubtitle2Msg);
        txtNoteMsg = view.findViewById(R.id.txtNoteMsg);
        btnPrimaryAction = view.findViewById(R.id.btnPrimaryAction);
    }
}
