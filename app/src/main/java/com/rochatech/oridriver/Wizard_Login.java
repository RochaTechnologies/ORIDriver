package com.rochatech.oridriver;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.rochatech.library.Support_BottomDialog;
import com.rochatech.webService.*;
import com.rochatech.library.Common;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Wizard_Login extends AppCompatActivity {

    EditText UnityEmail, UnityPassword;
    Button LoginPressed, ForgotPasswordPressed, CreateAccountPressed;
    connectToService _svcConnection;
    Common obj;
    String deviceToken;
    Support_BottomDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wizard_login_activity);
        obj = new Common(Wizard_Login.this);
        _svcConnection = new connectToService(Wizard_Login.this, obj.GetSharedPreferencesValue(Wizard_Login.this, "SessionToken"));
        InitControls();

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                deviceToken = instanceIdResult.getToken();
                SharedPreferences pref = getApplicationContext().getSharedPreferences(getResources().getString(R.string.ORIGlobal_SharedPreferences), Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = pref.edit();
                edit.putString("settings_FCMTokenId", deviceToken);
                edit.apply();
            }
        });

        LoginPressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) Wizard_Login.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                View view = Wizard_Login.this.getCurrentFocus();
                if (view == null) {
                    view = new View(Wizard_Login.this);
                }
                imm.hideSoftInputFromWindow(view.getWindowToken(),0);

                String UEmail = UnityEmail.getText().toString();
                String UPassword = UnityPassword.getText().toString();
                String Errors = AnyErrors(UEmail, UPassword);
                if (Errors.trim().isEmpty()) {
                    obj.ShowLoadingScreen(Wizard_Login.this,"Iniciando sesión...");
                    StartLogin(UEmail, Common.SHA256(UPassword), Wizard_Login.this);
                } else {
                    Common.DialogStatusAlert(Wizard_Login.this, Errors, getResources().getString(R.string.ORIGlobal_AnyErrorsTitle),"Error");
                }
            }
        });
        ForgotPasswordPressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Support_BottomDialog();
                dialog.Support_BottomDialog("PasswordRecover");
                dialog.show(getSupportFragmentManager(), "ori_recoverpassword");
            }
        });
        CreateAccountPressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Support_BottomDialog();
                dialog.Support_BottomDialog("LoadingView");
                dialog.show(getSupportFragmentManager(), "ori_loadingview");
            }
        });

        //region Hide keyboard event
        UnityEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Common.HideKeyboard(v, Wizard_Login.this);
            }
        });
        UnityPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Common.HideKeyboard(v, Wizard_Login.this);
            }
        });
        //endregion
        //endregion
    }

    //region WebService Call
    private void StartLogin(final String uEmail, final String hashedPassword, final Context context) {
        _svcConnection.ValidateCredentials(uEmail, hashedPassword, new WSResponseListener() {
            @Override
            public void onError(String message) {
                obj.GetProgressDialogLoadinScreen().dismiss();
                if (message.contains("NO_CONNECTION")) {
                    Common.DialogStatusAlert(Wizard_Login.this,getResources().getString(R.string.ORI_NoInternetConnection_Msg),getResources().getString(R.string.ORI_NoInternetConnection_Title),"Error");
                } else if (message.contains("Error_InvalidToken")){
                    _svcConnection.LogOffUser(Integer.parseInt(obj.GetSharedPreferencesValue(Wizard_Login.this,"UID")));
                    Common.LogoffByInvalidToken(Wizard_Login.this);
                } else {
                    Common.DialogStatusAlert(context, message, getResources().getString(R.string.ORIGlobal_webServiceError),"Error");
                }
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                try {
                    JSONObject response = jsonResponse.getJSONObject(0);
                    String status = response.getString("Status");
                    String UID = response.getString("UID");
                    String gatewayId = response.getString("GID");
                    String sessionTokenId = response.getString("TokenSessionId");
                    String lastName = response.getString("LastName");
                    String givenName = response.getString("GivenName");
                    String nickName = response.getString("NickName");
                    String gender = response.getString("Gender");
                    String mobile = response.getString("CellNumber");
                    Boolean result = false;
                    switch (status) {
                        case "IsValid":
                            result = SaveOnSharedPreferences(uEmail, hashedPassword, UID, gatewayId, sessionTokenId, lastName, givenName, nickName, gender, mobile);
                            if (result) {
                                UpdateFCMId(deviceToken);
                                SetAvailablePaymentOnSharedPreferences(context, UID, sessionTokenId);
                            } else {
                                obj.CloseLoadingScreen();
                                Common.DialogStatusAlert(context, getResources().getString(R.string.ORIGlobal_SharedPreferencesFailed_Msg), getResources().getString(R.string.ORIGlobal_SharedPreferencesFailed_Title),"Error");
                            }
                            break;
                        case "Error_AccountLocked":
                            obj.CloseLoadingScreen();
                            Common.DialogStatusAlert(context, "La cuenta ha sido bloqueada por motivos de seguridad", "Cuenta bloqueada","Error");
                            break;
                        case "Error_InvalidCredentials":
                            obj.CloseLoadingScreen();
                            Common.DialogStatusAlert(context, "Su correo electrónico / contraseña son incorrectos, recuerde que después de tres intentos su cuenta se bloqueara por motivos de seguridad", "Credenciales incorrectas","Error");
                            break;
                        case "Error_InactiveAccount":
                            obj.CloseLoadingScreen();
                            Common.DialogStatusAlert(context, "Su cuenta se encuentra inactiva, por favor revise su correo electrónico para seguir los pasos para activarla", "Cuenta inactiva","Error");
                            break;
                        case "Error_MembershipSuspended":
                            obj.CloseLoadingScreen();
                            Common.DialogStatusAlert(context, "Su membresía se encuentra suspendida.", "Hemos suspendido su membresía","Error");
                            break;
                        case "Error_MembershipInactive":
                            obj.CloseLoadingScreen();
                            Common.DialogStatusAlert(context, "Su membresía se encuentra inactiva, revise su correo electrónico para activarla", "Su membresía se encuentra inactiva","Error");
                            break;
                        case "Error_WizardNotCompleted":
                            obj.CloseLoadingScreen();
                            Common.DialogStatusAlert(context, "Por favor termine de manera correcta su registro, revise su correo electrónico para seguir los pasos", "Proceso de registro incompleto","Error");
                            break;
                        case "Error_NotPaymentInformation":
                            //TODO preguntar a Raul si es bueno actualizar desde aqui el tipo de pago preferido, porque como no tengo informacion de pago pero mi opcion de pago preferido es ambos desde el web service
                            /*Primero guarda datos del usuario*/
                            result = SaveOnSharedPreferences(uEmail, hashedPassword, UID, gatewayId, sessionTokenId, lastName, givenName, nickName, gender, mobile);
                            if (result) {
                                /*Guardo en shared valores por default, estos pueden cambiar despues del SetAvailablePaymentOnSharedPreferences*/
                                /*NOTA: debo buscar un nombre mas corto para esa mamada de SetAvailablePaymentOnSharedPreferences*/
                                /*NOTA2: Simon por pendejo lo volvi a escribir :'(*/
                                SharedPreferences pref = getApplicationContext().getSharedPreferences(getResources().getString(R.string.ORIGlobal_SharedPreferences), Context.MODE_PRIVATE);
                                SharedPreferences.Editor edit = pref.edit();
                                edit.putString("settings_AvailablePayment", "3");
                                edit.putString("settings_Gender", "3");
                                edit.apply();
                                SetAvailablePaymentOnSharedPreferences(Wizard_Login.this, UID, sessionTokenId);
                            } else {
                                obj.GetProgressDialogLoadinScreen().dismiss();
                                Common.DialogStatusAlert(context, getResources().getString(R.string.ORIGlobal_SharedPreferencesFailed_Msg), getResources().getString(R.string.ORIGlobal_SharedPreferencesFailed_Title),"Error");
                            }
                            break;
                        case "Error_MembershipExpired":
                            obj.CloseLoadingScreen();
                            Common.DialogStatusAlert(context, "Por favor contacte nuestro centro de atención a clientes para mas información", "Su membresía ha expirado","Error");
                            break;
                        default: status = "";
                            obj.CloseLoadingScreen();
                            Common.DialogStatusAlert(context, "Lo sentimos, ocurrió un error inesperado. Favor de ponerse en contacto con nuestro centro de atención a clientes", "Ocurrió un error inesperado","Error");
                            break;
                    }
                } catch (JSONException e) {
                    obj.CloseLoadingScreen();
                    Common.DialogStatusAlert(context, e.toString(), getResources().getString(R.string.ORIGlobal_webServiceError),"Error");
                }
            }
        });

    }
    private void SetAvailablePaymentOnSharedPreferences(final Context context, final String UID, final String sessionTokenId) {
        _svcConnection.GetCardOrAcntForService(Integer.parseInt(UID), 0,"0", new WSResponseListener() {
            @Override
            public void onError(String message) {
                switch (message) {
                    case "Error_InvalidToken":
                        Common.LogoffByInvalidToken(Wizard_Login.this);
                        break;
                    case "NO_CONNECTION":
                        obj.CloseLoadingScreen();
                        Common.DialogStatusAlert(context,getResources().getString(R.string.ORI_NoInternetConnection_Msg),getResources().getString(R.string.ORI_NoInternetConnection_Title),"Error");
                        break;
                }
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                JSONObject response = null;
                SharedPreferences pref = getApplicationContext().getSharedPreferences(getResources().getString(R.string.ORIGlobal_SharedPreferences), Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = pref.edit();
                try {
                    response = jsonResponse.getJSONObject(0);
                    String value = response.getString("Id");
                    String bankActId = (value == null) ? "" : value;
                    if (bankActId.trim().isEmpty()){
                        edit.putString("settings_AvailablePayment", "1");
                    } else {
                        edit.putString("settings_AvailablePayment", "3");
                    }
                    edit.apply();
                    SetPreferredDriverGenderId(context, UID, sessionTokenId);
                } catch (JSONException e) {
                    edit.putString("settings_AvailablePayment", "1").apply();
                    SetPreferredDriverGenderId(context, UID, sessionTokenId);
                }
            }
        });
    }
    private void SetPreferredDriverGenderId(final Context context, final String UID, final String sessionTokenId) {
        _svcConnection.GetPreferredDriverGenderTypeId(Integer.parseInt(UID), new WSResponseListener() {
            @Override
            public void onError(String message) {
                switch (message) {
                    case "Error_InvalidToken":
                        Common.LogoffByInvalidToken(Wizard_Login.this);
                        break;
                    case "NO_CONNECTION":
                        obj.CloseLoadingScreen();
                        Common.DialogStatusAlert(context,getResources().getString(R.string.ORI_NoInternetConnection_Msg),getResources().getString(R.string.ORI_NoInternetConnection_Title),"Error");
                        break;
                }
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                try {
                    String genderId = jsonResponse.getString(0);
                    try {
                        SharedPreferences preferences = getApplicationContext().getSharedPreferences(getResources().getString(R.string.ORIGlobal_SharedPreferences), Context.MODE_PRIVATE);
                        SharedPreferences.Editor edit = preferences.edit();
                        if (genderId.trim().isEmpty()) {
                            edit.putString("settings_Gender", "3");
                        } else {
                            edit.putString("settings_Gender", genderId);
                        }
                        edit.apply();
                        SetPreferredPaymentTypeId(context, UID, sessionTokenId);
                    } catch (Exception e) {
                        obj.CloseLoadingScreen();
                        Common.DialogStatusAlert(Wizard_Login.this, getResources().getString(R.string.ORIGlobal_SharedPreferencesFailed_Msg), getResources().getString(R.string.ORIGlobal_SharedPreferencesFailed_Title),"Error");
                    }
                } catch (JSONException e) {
                    obj.CloseLoadingScreen();
                    Common.DialogStatusAlert(Wizard_Login.this, e.toString(), getResources().getString(R.string.ORIGlobal_webServiceError),"Error");
                }
            }
        });
    }
    private void SetPreferredPaymentTypeId(final Context context, final String UID, final String sessionTokenId) {
        _svcConnection.GetPreferredPaymentTypeId(Integer.parseInt(UID), new WSResponseListener() {
            @Override
            public void onError(String message) {
                switch (message) {
                    case "Error_InvalidToken":
                        Common.LogoffByInvalidToken(Wizard_Login.this);
                        break;
                    case "NO_CONNECTION":
                        obj.CloseLoadingScreen();
                        Common.DialogStatusAlert(context,getResources().getString(R.string.ORI_NoInternetConnection_Msg),getResources().getString(R.string.ORI_NoInternetConnection_Title),"Error");
                        break;
                }
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                try {
                    String paymentId = jsonResponse.getString(0);
                    try {
                        SharedPreferences preferences = getApplicationContext().getSharedPreferences(getResources().getString(R.string.ORIGlobal_SharedPreferences), Context.MODE_PRIVATE);
                        SharedPreferences.Editor edit = preferences.edit();
                        if (paymentId.trim().isEmpty()) {
                            edit.putString("settings_Payment", "3");
                        } else {
                            edit.putString("settings_Payment", paymentId);
                        }
                        edit.apply();
                        Intent intent = new Intent(Wizard_Login.this, Map_Driver.class);
                        String welcomeMsg = "";
                        String opt = obj.GetSharedPreferencesValue(Wizard_Login.this, "Gender");
                        switch (opt) {
                            case "H":
                                welcomeMsg = "¡Bienvenido " + obj.GetSharedPreferencesValue(Wizard_Login.this, "GivenName") + "!";
                                break;
                            case "M":
                                welcomeMsg = "¡Bienvenida " + obj.GetSharedPreferencesValue(Wizard_Login.this, "GivenName") + "!";
                                break;
                        }
                        intent.putExtra("ORIWelcomeMsg", welcomeMsg);
                        obj.CloseLoadingScreen();
                        startActivity(intent);
                    } catch (Exception e) {
                        obj.CloseLoadingScreen();
                        Common.DialogStatusAlert(Wizard_Login.this, getResources().getString(R.string.ORIGlobal_SharedPreferencesFailed_Msg), getResources().getString(R.string.ORIGlobal_SharedPreferencesFailed_Title),"Error");
                    }
                } catch (JSONException e) {
                    obj.CloseLoadingScreen();
                    Common.DialogStatusAlert(Wizard_Login.this, e.toString(), getResources().getString(R.string.ORIGlobal_webServiceError),"Error");
                }
            }
        });
    }
    private void UpdateFCMId(String token) {
        int UID = Integer.parseInt(obj.GetSharedPreferencesValue(Wizard_Login.this,"UID"));
        _svcConnection.UpdateDeviceFCMId(UID);
    }
    //endregion

    //region Misc
    private void InitControls() {
        UnityEmail = findViewById(R.id.txtUnityEmail);
        UnityPassword = findViewById(R.id.txtUnityPassword);
        LoginPressed = findViewById(R.id.btnLoginPressed);
        ForgotPasswordPressed = findViewById(R.id.btnPasswordPressed);
        CreateAccountPressed = findViewById(R.id.btnAccountPressed);
    }
    private String AnyErrors(String uEmail, String uPassword) {
        String result = "";
        result += (uEmail.isEmpty()) ? getResources().getString(R.string.WizardLogin_Error_EmptyUnityEmail) + "\n" : "";
        result += (!Common.IsEmail(uEmail)) ? getResources().getString(R.string.WizardLogin_Error_InvalidUnityEmail) + "\n" : "";
        result += (uPassword.isEmpty()) ? getResources().getString(R.string.WizardLogin_Error_EmptyUnityPassword) : "";
        return result;
    }
    private Boolean SaveOnSharedPreferences(String uEmail, String hasedPassword,String UID, String gatewayId, String sessionTokenId, String lastName, String givenName, String nickName, String gender, String mobile) {
        try {
            SharedPreferences pref = getApplicationContext().getSharedPreferences(getResources().getString(R.string.ORIGlobal_SharedPreferences), Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = pref.edit();
            edit.putString("A","user_OnlineStatus");
            edit.putString("UID", UID);
            edit.putString("GID", gatewayId);
            edit.putString("SessionToken", sessionTokenId);
            /*Update session token value*/
            _svcConnection.SetSessionTokenId(sessionTokenId);
            edit.putString("LastName", lastName);
            edit.putString("GivenName", givenName);
            edit.putString("NickName", nickName);
            edit.putString("Gender", gender);
            edit.putString("Mobile", mobile);
            edit.putString("UnityEmail", uEmail);
            edit.putString("UnityPassword", hasedPassword);
            edit.apply();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public void CloseDialogFragment() {
        dialog.dismiss();
    }
    //endregion

}
