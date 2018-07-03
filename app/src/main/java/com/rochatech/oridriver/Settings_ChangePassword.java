package com.rochatech.oridriver;

import com.rochatech.library.Common;
import com.rochatech.webService.*;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;


public class Settings_ChangePassword extends AppCompatActivity {

    //region Global
    EditText _currentPassword, _newPassword, _confirmPassword;
    Button _updatePass;
    connectToService _svcConnection;
    Common obj;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_changepassword_activity);
        obj = new Common(Settings_ChangePassword.this);
        _svcConnection = new connectToService(Settings_ChangePassword.this, obj.GetSharedPreferencesValue(Settings_ChangePassword.this, "SessionToken"));
        InitAppControls();
        _updatePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obj.ShowLoadingScreen(Settings_ChangePassword.this,"Actualizando contraseña");
                String Errors = CheckForErrors(_currentPassword.getText().toString(), _newPassword.getText().toString(), _confirmPassword.getText().toString());
                if (Errors.trim().isEmpty()) {
                    UpdatePass();
                } else {
                    obj.CloseLoadingScreen();
                    Common.DialogStatusAlert(Settings_ChangePassword.this,Errors,getResources().getString(R.string.ORIGlobal_AnyErrorsTitle),"Error");
                }
            }
        });

        //region Hide keyboard
        _currentPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Common.HideKeyboard(v, Settings_ChangePassword.this);
            }
        });
        _newPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Common.HideKeyboard(v, Settings_ChangePassword.this);
            }
        });
        _confirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Common.HideKeyboard(v, Settings_ChangePassword.this);
            }
        });
        //endregion
    }

    //region WebService Call
    private void UpdatePass() {
        int UID = Integer.parseInt(obj.GetSharedPreferencesValue(Settings_ChangePassword.this, "UID"));
        final String password = Common.SHA256(_newPassword.getText().toString());
        _svcConnection.UpdatePassword(UID, password, new WSResponseListener() {
            @Override
            public void onError(String message) {
                switch (message) {
                    case "Error_InvalidToken":
                        Common.LogoffByInvalidToken(Settings_ChangePassword.this);
                        break;
                }
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                if (SaveSharedPreferences(Common.SHA256(password))) {
                    _currentPassword.setText("");
                    _newPassword.setText("");
                    _confirmPassword.setText("");
                    LinearLayoutCompat linearLayoutCompat = findViewById(R.id.ChangePasswordMainLinear);
                    Snackbar snackbar = Snackbar.make(linearLayoutCompat,"¡Contraseña actualizada!",Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    obj.CloseLoadingScreen();
                } else {
                    obj.CloseLoadingScreen();
                    Common.DialogStatusAlert(Settings_ChangePassword.this,getResources().getString(R.string.ORIGlobal_SharedPreferencesFailed_Msg),getResources().getString(R.string.ORIGlobal_SharedPreferencesFailed_Title),"Error");
                }
            }
        });
    }
    //endregion

    //region Init
    private void InitAppControls() {
        _currentPassword = findViewById(R.id.CurrentPassword);
        _newPassword = findViewById(R.id.NewPassword);
        _confirmPassword = findViewById(R.id.ConfirmPassword);
        _updatePass = findViewById(R.id.UpdatePassword);
    }
    //endregion

    //region MISC
    private Boolean SaveSharedPreferences(String hasedpassword) {
        try {
            SharedPreferences preferences = getApplicationContext().getSharedPreferences(getResources().getString(R.string.ORIGlobal_SharedPreferences), Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = preferences.edit();
            edit.putString("UnityPassword", hasedpassword);
            edit.apply();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    private String CheckForErrors(String currentPassword, String newPassword, String confirmPassword) {
        String result = "";
        String currentHasedPassword = Common.SHA256(currentPassword);
        result += (currentPassword.trim().isEmpty()) ? "Contraseña actual vacia \n" : "";
        result += (!currentHasedPassword.equals(obj.GetSharedPreferencesValue(Settings_ChangePassword.this,"UnityPassword"))) ? "La contraseña actual no coincide con la contraseña de su correo \n" : "";

        result += (newPassword.trim().isEmpty()) ? "Nueva contraseña vacia \n" : "";
        result += (confirmPassword.trim().isEmpty()) ? "Contraseña de confirmación vacia \n" : "";

        result += (!newPassword.equals(confirmPassword)) ? "Ambas contraseñas no coinciden por favor vuelva a escribirlas \n" : "";
        return result;
    }
    //endregion
}
