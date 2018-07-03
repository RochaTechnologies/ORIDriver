package com.rochatech.oridriver;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class Fragment_Wizard_NotificationAlert extends Fragment {

    Context context;
    View view;
    TextView txtTitleMsg, txtSubtitle1Msg, txtSubtitle2Msg, txtNoteMsg;
    Button btnPrimaryAction, btnSecundaryAction;

    /**/
    String DisplayOpt = "";
    String Email = "";
    String GivenName = "";
    String Gender = "";

    public Fragment_Wizard_NotificationAlert() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_wizard_notificationalert, container, false);
        FragInitAppControlers();
        Bundle bundle = getArguments();
        DisplayOpt = bundle.getString("DisplayOpt");
        switch (DisplayOpt) {
            case "RecoverPassword":
                Email = bundle.getString("RecoverEmail");
                txtTitleMsg.setText("¡Listo, tu correo se envió con éxito!");
                txtSubtitle1Msg.setText("Las instrucciones para recuperar tu contraseña");
                txtSubtitle2Msg.setText("se enviaron al correo " + Email);
                txtNoteMsg.setVisibility(View.INVISIBLE);
                btnPrimaryAction.setText("¡Entendido!");
                btnSecundaryAction.setVisibility(View.INVISIBLE);
                break;
            case "AccountCreated":
                Email = bundle.getString("ori_createdemail");
                GivenName = bundle.getString("ori_createdname");
                Gender = bundle.getString("ori_createdgender");
                switch (Gender) {
                    case "H":
                        txtTitleMsg.setText("¡Bienvenido " + GivenName + " !");
                        break;
                    case "M":
                        txtTitleMsg.setText("¡Bienvenida " + GivenName + " !");
                        break;
                }
//                txtSubtitle1Msg.setText("Se envió un correo de confirmación al correo");
//                txtSubtitle2Msg.setText(Email);
//                txtNoteMsg.setVisibility(View.VISIBLE);
//                btnPrimaryAction.setText("Agregar ahora");
//                btnSecundaryAction.setVisibility(View.VISIBLE);
                break;
            case "CreditCardAssigned":
                txtTitleMsg.setText("¡Tarjeta guardada con éxito!");
                txtSubtitle1Msg.setText("Su método de pago a sido guardado con éxito");
                txtSubtitle2Msg.setText("inicie sesión para comenzar a disfrutar de nuestro servicio");
                txtNoteMsg.setVisibility(View.INVISIBLE);
                btnPrimaryAction.setText("Deslice hacia abajo para cerrar");
                btnSecundaryAction.setVisibility(View.INVISIBLE);
                break;
        }
        btnPrimaryAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (DisplayOpt) {
                    case "RecoverPassword":
                        /*Close the dialog*/
                        break;
                    default:
                        /*Close the dialog*/
                        break;
                }
            }
        });
        btnSecundaryAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        btnSecundaryAction = view.findViewById(R.id.btnSecondaryAction);
    }
}
