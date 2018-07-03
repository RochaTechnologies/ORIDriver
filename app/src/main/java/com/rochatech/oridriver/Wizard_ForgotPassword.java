package com.rochatech.oridriver;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.rochatech.library.Common;
import com.rochatech.webService.*;

public class Wizard_ForgotPassword extends AppCompatActivity {

    connectToService _svcConnection;
    Common obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wizard_forgotpassword_activity);
        obj = new Common(Wizard_ForgotPassword.this);
        _svcConnection = new connectToService(Wizard_ForgotPassword.this, obj.GetSharedPreferencesValue(Wizard_ForgotPassword.this, "SessionToken"));
        final EditText sendToEmail = findViewById(R.id.RecoverPasswordEmail);
        final Button sendEmail = findViewById(R.id.SendEmailPressed);
        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartSendingEmail(sendToEmail.getText().toString());
            }
        });
        sendToEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Common.HideKeyboard(v, Wizard_ForgotPassword.this);
            }
        });
    }

    private void StartSendingEmail(String email) {
        String Errors = GetActivityErrors(email);
        if (Errors.isEmpty()) {
            _svcConnection.ForgotPassword(email);
            Common.DialogStatusAlert(Wizard_ForgotPassword.this, "¡Correo enviado!", "","Success");
            Intent intent = new Intent(Wizard_ForgotPassword.this, Wizard_Login.class);
            startActivity(intent);
        } else {
            Common.DialogStatusAlert(Wizard_ForgotPassword.this, Errors, getResources().getString(R.string.ORIGlobal_AnyErrorsTitle),"Error");
        }
    }

    private String GetActivityErrors(String base) {
        String result = "";
        result += (base.isEmpty()) ? getResources().getString(R.string.WizardForgotPassword_EmptyEmail) + "\n" : "";
        result += (!Common.IsEmail(base)) ? getResources().getString(R.string.WizardForgotPassword_InvalidEmail) : "";
        return result;
    }
}
