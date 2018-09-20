package com.rochatech.oridriver;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.rochatech.library.Support_BottomDialog;
import com.rochatech.webService.*;
import com.rochatech.library.Common;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings({"WeakerAccess","unused","FieldCanBeLocal"})
public class Wizard_Login extends AppCompatActivity {

    Boolean hasLocPermissions;
    String userEmail;
    EditText UnityEmail, UnityPassword;
    Button LoginPressed, ForgotPasswordPressed, CreateAccountPressed;
    connectToService _svcConnection;
    Common obj;
    String deviceToken;
    Support_BottomDialog dialog;
    CoordinatorLayout parent;
    Integer _UID;
    String fullStack;
    Support_BottomDialog forgotPasswordDialog, createAccountDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wizard_login_activity);
        obj = new Common(Wizard_Login.this);
        _svcConnection = new connectToService(Wizard_Login.this, obj.GetSharedPreferencesValue(Wizard_Login.this, "SessionToken"));
        try {
            parent = findViewById(R.id.LoginMainWrapper);
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    deviceToken = instanceIdResult.getToken();
                    SharedPreferences pref = getApplicationContext().getSharedPreferences(getResources().getString(R.string.ORIGlobal_SharedPreferences), Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString("settings_FCMTokenId", deviceToken).apply();
                }
            });
            hasLocPermissions = (ContextCompat.checkSelfPermission(Wizard_Login.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
            if (obj.isInternetConnectionActive(Wizard_Login.this)) {
                RequestEnvironment();
            } else {
                Common.DialogStatusAlert(Wizard_Login.this,getResources().getString(R.string.ORI_NoInternetConnection_Msg),getResources().getString(R.string.ORI_NoInternetConnection_Title),"Error");
            }
        } catch (Exception e) {
            SendLogDeviceError(e,"protected void onCreate", true);
        }
    }

    //region WebService Call
    private void RequestEnvironment() {
        _svcConnection.RequestEnvironment(new WSResponseListener() {
            @Override
            public void onError(String message) {
                try {
                    switch (message) {
                        case "Error_InvalidToken":
                            Common.LogoffByInvalidToken(Wizard_Login.this);
                            break;
                        case "NO_CONNECTION":
                            Common.DialogStatusAlert(Wizard_Login.this,getResources().getString(R.string.ORI_NoInternetConnection_Msg),getResources().getString(R.string.ORI_NoInternetConnection_Title),"Error");
                            break;
                    }
                } catch (Exception e) {
                    SendLogDeviceError(e,"private void RequestEnvironment", true);
                }
            }

            @Override
            public void onResponseObject(JSONArray jsonArray) {
                try {
                    String prefKeyName = "";
                    String keyValue;
                    SharedPreferences pref = getApplicationContext().getSharedPreferences(getResources().getString(R.string.ORIGlobal_SharedPreferences), Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = pref.edit();
                    JSONObject jsonObject;
                    try {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonObject = jsonArray.getJSONObject(i);
                            if (jsonObject.getString("SettingKey").contains("Env")){
                                prefKeyName = "reqenv_environment";
                            }
                            if (jsonObject.getString("SettingKey").contains("MainSvcURL")){
                                prefKeyName = "reqenv_mainsvcurl";
                            }
                            if (jsonObject.getString("SettingKey").contains("ReleaseDate")){
                                prefKeyName = "reqenv_releasedate";
                            }
                            if (jsonObject.getString("SettingKey").contains("Message")){
                                prefKeyName = "reqenv_message";
                            }
                            if (jsonObject.getString("SettingKey").contains("ProfilePicURL")){
                                prefKeyName = "reqenv_profilepictureurl";
                            }
                            if (jsonObject.getString("SettingKey").contains("ProfilePicUpdateURL")){
                                prefKeyName = "reqenv_profilepicupdateurl";
                            }
                            keyValue = jsonObject.getString("SettingValue");
                            edit.putString(prefKeyName,keyValue).apply();
                        }
                        _svcConnection = new connectToService(Wizard_Login.this, obj.GetSharedPreferencesValue(Wizard_Login.this, "SessionToken"));
                        InitControls();
                        LoginPressed.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    InputMethodManager imm = (InputMethodManager) Wizard_Login.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                                    View view = Wizard_Login.this.getCurrentFocus();
                                    if (view == null) {
                                        view = new View(Wizard_Login.this);
                                    }
                                    if (imm != null) {
                                        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                                    }

                                    if (hasLocPermissions) {
                                        String UEmail = UnityEmail.getText().toString();
                                        String UPassword = UnityPassword.getText().toString();
                                        String Errors = AnyErrors(UEmail,UPassword);
                                        if (Errors.trim().isEmpty()) {
                                            obj.ShowLoadingScreen(Wizard_Login.this,"Iniciando sesión...");
                                            StartLogin(UEmail,Common.SHA256(UPassword),Wizard_Login.this);
                                        } else {
                                            Common.DialogStatusAlert(Wizard_Login.this, Errors, getResources().getString(R.string.ORIGlobal_AnyErrorsTitle),"Error");
                                        }
                                    } else {
                                        Common.DialogStatusAlert(Wizard_Login.this, getResources().getString(R.string.ORINoLocationPermissionsGrantedMsg), getResources().getString(R.string.ORINoLocationPermissionsGrantedTitle),getResources().getString(R.string.ORIDialog_Error_IconName));
                                    }
                                } catch (Exception e) {
                                    SendLogDeviceError(e,"LoginPressed.SetOnClickListener", true);
                                }
                            }
                        });
                        ForgotPasswordPressed.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                forgotPasswordDialog = new Support_BottomDialog();
                                forgotPasswordDialog.Support_BottomDialog_SetFragmentName("PasswordRecover");
                                forgotPasswordDialog.show(getSupportFragmentManager(), "ori_recoverpassword");
                            }
                        });
                        CreateAccountPressed.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                createAccountDialog = new Support_BottomDialog();
                                createAccountDialog.Support_BottomDialog_SetFragmentName("LoadingView");
                                createAccountDialog.show(getSupportFragmentManager(), "ori_loadingview");
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

                        String tmp = pref.getString("reqenv_message_old","").trim();
                        if (!pref.getString("reqenv_message_old","").trim().isEmpty()) {
                            if (!pref.getString("reqenv_message_old","").contains(pref.getString("reqenv_message",""))) {
                                String oldMsg = pref.getString("reqenv_message","");
                                edit.putString("reqenv_message_old",oldMsg).apply();
                                Show_ReqEnvironment_WelcomeDialog();
                            }
                        } else {
                            String oldMsg = pref.getString("reqenv_message","");
                            edit.putString("reqenv_message_old",oldMsg).apply();
                            Show_ReqEnvironment_WelcomeDialog();
                        }
                        userEmail = obj.GetSharedPreferencesValue(Wizard_Login.this,"UnityEmail");
                        if (userEmail != null) {
                            if (!userEmail.trim().isEmpty()) {
                                UnityEmail.setText(userEmail);
                            }
                        }
                        /*Ask for location permissions*/
                        if (!hasLocPermissions) {
                            Show_HasLocPermissions();
                        } else {
                            if (!pref.getString("reqenv_message_old","").trim().isEmpty()) {
                                if (!pref.getString("reqenv_message_old","").contains(pref.getString("reqenv_message",""))) {
                                    String oldMsg = pref.getString("reqenv_message","");
                                    edit.putString("reqenv_message_old",oldMsg).apply();
                                    Show_ReqEnvironment_WelcomeDialog();
                                }
                            } else {
                                String oldMsg = pref.getString("reqenv_message","");
                                edit.putString("reqenv_message_old",oldMsg).apply();
                                Show_ReqEnvironment_WelcomeDialog();
                            }
                        }
                        /**/
                    } catch (JSONException e) {
                        SendLogDeviceError(e,"private void RequestEnvironment", false);
                        Common.DialogStatusAlert(Wizard_Login.this,e.toString(),getResources().getString(R.string.ORIGlobal_webServiceError),"Error");
                    }
                } catch (Exception e) {
                    SendLogDeviceError(e,"private void RequestEnvironment", false);
                    Common.DialogStatusAlert(Wizard_Login.this,getResources().getString(R.string.ORIUnexpectedErrorNSendLogToServer),getResources().getString(R.string.ORIUnexpectedError),"Error");
                }
            }
        });
    }
    private void StartLogin(final String uEmail, final String hashedPassword, final Context context) {
        _svcConnection.ValidateCredentials(uEmail, hashedPassword, new WSResponseListener() {
            @Override
            public void onError(String message) {
                try {
                    obj.CloseLoadingScreen();
                    if (message.contains("NO_CONNECTION")) {
                        Common.DialogStatusAlert(Wizard_Login.this,getResources().getString(R.string.ORI_NoInternetConnection_Msg),getResources().getString(R.string.ORI_NoInternetConnection_Title),"Error");
                    } else if (message.contains("Error_InvalidToken")){
                        _svcConnection.LogOffUser(Integer.parseInt(obj.GetSharedPreferencesValue(Wizard_Login.this,"UID")));
                        Common.LogoffByInvalidToken(Wizard_Login.this);
                    } else {
                        Common.DialogStatusAlert(context, message, getResources().getString(R.string.ORIGlobal_webServiceError),"Error");
                    }
                } catch (Exception e) {
                    SendLogDeviceError(e,"private void StartLogin", false);
                    Common.DialogStatusAlert(Wizard_Login.this,getResources().getString(R.string.ORIUnexpectedErrorNSendLogToServer),getResources().getString(R.string.ORIUnexpectedError),"Error");
                }
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                try {
                    JSONObject response = jsonResponse.getJSONObject(0);
                    String status = response.getString("Status");
                    Boolean result;
                    String UID, gatewayId, sessionTokenId, lastName, givenName, nickName, gender, mobile, isVerified;
//                    UID = "";
//                    gatewayId = "";
//                    sessionTokenId = "";
//                    lastName = "";
//                    givenName = "";
//                    nickName = "";
//                    gender = "";
//                    mobile = "";
//                    isVerified = "";
                    switch (status) {
                        case "IsValid":
                            UID = response.getString("UID");
                            _UID = Integer.parseInt(UID);
                            gatewayId = response.getString("GID");
                            sessionTokenId = response.getString("TokenSessionId");
                            lastName = response.getString("LastName");
                            givenName = response.getString("GivenName");
                            nickName = response.getString("NickName");
                            gender = response.getString("Gender");
                            mobile = response.getString("CellNumber");
                            isVerified = response.getString("IsVerified");
                            result = SaveOnSharedPreferences(uEmail, hashedPassword, UID, gatewayId, sessionTokenId, lastName, givenName, nickName, gender, mobile, isVerified);
                            if (result) {
                                UpdateFCMId();
                                SetAvailablePaymentOnSharedPreferences(context, UID);
                            } else {
                                obj.CloseLoadingScreen();
                                Common.DialogStatusAlert(context, getResources().getString(R.string.ORIGlobal_SharedPreferencesFailed_Msg), getResources().getString(R.string.ORIGlobal_SharedPreferencesFailed_Title),"Error");
                            }
                            break;
                        case "Error_AccountLocked":
                            obj.CloseLoadingScreen();
                            Common.DialogStatusAlert(context, getResources().getString(R.string.WizardLogin_Error_ActLockedMsg), getResources().getString(R.string.WizardLogin_Error_ActLockedTitle),"Error");
                            break;
                        case "Error_InvalidCredentials":
                            obj.CloseLoadingScreen();
                            Common.DialogStatusAlert(context, "Su correo electrónico / contraseña son incorrectos, recuerde que después de cuatro intentos su cuenta se bloqueará por motivos de seguridad", "Credenciales incorrectas","Error");
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
                            obj.CloseLoadingScreen();
                            Common.DialogStatusAlert(context, "Pagale", "Pagale","Error");
                            break;
//                            /*Primero guarda datos del usuario*/
//                            result = SaveOnSharedPreferences(uEmail, hashedPassword, UID, gatewayId, sessionTokenId, lastName, givenName, nickName, gender, mobile, isVerified);
//                            if (result) {
//                                SharedPreferences pref = getApplicationContext().getSharedPreferences(getResources().getString(R.string.ORIGlobal_SharedPreferences), Context.MODE_PRIVATE);
//                                SharedPreferences.Editor edit = pref.edit();
//                                edit.putString("settings_AvailablePayment", "3").apply();
//                                edit.putString("settings_Gender", "3").apply();
//                                SetAvailablePaymentOnSharedPreferences(Wizard_Login.this, UID);
//                            } else {
//                                obj.GetProgressDialogLoadinScreen().dismiss();
//                                Common.DialogStatusAlert(context, getResources().getString(R.string.ORIGlobal_SharedPreferencesFailed_Msg), getResources().getString(R.string.ORIGlobal_SharedPreferencesFailed_Title),"Error");
//                            }
//                            break;
                        case "Error_MembershipExpired":
                            obj.CloseLoadingScreen();
                            Common.DialogStatusAlert(context, "Por favor contacte nuestro centro de atención a clientes para mas información", "Su membresía ha expirado","Error");
                            break;
                        default:
                            obj.CloseLoadingScreen();
                            Common.DialogStatusAlert(context, "Lo sentimos, ocurrió un error inesperado. Favor de ponerse en contacto con nuestro centro de atención a clientes", "Ocurrió un error inesperado","Error");
                            break;
                    }
                } catch (JSONException e) {
                    obj.CloseLoadingScreen();
                    SendLogDeviceError(e,"private void StartLogin", false);
                    Common.DialogStatusAlert(context, e.toString(), getResources().getString(R.string.ORIGlobal_webServiceError),"Error");
                }
            }
        });

    }
    private void SetAvailablePaymentOnSharedPreferences(final Context context, final String UID) {
        _svcConnection.GetCardOrAcntForService(Integer.parseInt(UID), 0,"0", new WSResponseListener() {
            @Override
            public void onError(String message) {
                try {
                    switch (message) {
                        case "Error_InvalidToken":
                            obj.CloseLoadingScreen();
                            Common.LogoffByInvalidToken(Wizard_Login.this);
                            break;
                        case "NO_CONNECTION":
                            obj.CloseLoadingScreen();
                            Common.DialogStatusAlert(context,getResources().getString(R.string.ORI_NoInternetConnection_Msg),getResources().getString(R.string.ORI_NoInternetConnection_Title),"Error");
                            break;
                    }
                } catch (Exception e) {
                    obj.CloseLoadingScreen();
                    SendLogDeviceError(e,"private void SetAvailablePaymentOnSharedPreferences",true);
                }
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                try {
                    JSONObject response;
                    SharedPreferences pref = getApplicationContext().getSharedPreferences(getResources().getString(R.string.ORIGlobal_SharedPreferences), Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = pref.edit();
                    try {
                        response = jsonResponse.getJSONObject(0);
                        String value = response.getString("Id");
                        String bankActId = (value == null) ? "" : value;
                        if (bankActId.trim().isEmpty()){
                            edit.putString("settings_AvailablePayment", "1").apply();
                        } else {
                            edit.putString("settings_AvailablePayment", "3").apply();
                        }
                    } catch (JSONException e) {
                        edit.putString("settings_AvailablePayment", "1").apply();
                    }
                    UpdatePreferredPaymentType(context);
                } catch (Exception e) {
                    obj.CloseLoadingScreen();
                    SendLogDeviceError(e,"private void SetAvailablePaymentOnSharedPreferences",true);
                }
            }
        });
    }
    private void SetPreferredDriverGenderId(final Context context, final String UID) {
        _svcConnection.GetPreferredDriverGenderTypeId(Integer.parseInt(UID), new WSResponseListener() {
            @Override
            public void onError(String message) {
                try {
                    switch (message) {
                        case "Error_InvalidToken":
                            Common.LogoffByInvalidToken(Wizard_Login.this);
                            break;
                        case "NO_CONNECTION":
                            obj.CloseLoadingScreen();
                            Common.DialogStatusAlert(context,getResources().getString(R.string.ORI_NoInternetConnection_Msg),getResources().getString(R.string.ORI_NoInternetConnection_Title),"Error");
                            break;
                    }
                } catch (Exception e) {
                    obj.CloseLoadingScreen();
                    SendLogDeviceError(e,"private void SetPreferredDriverGenderId",true);
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
                            edit.putString("settings_Gender", "3").apply();
                        } else {
                            edit.putString("settings_Gender", genderId).apply();
                        }
                        SetPreferredPaymentTypeId(context, UID);
                    } catch (Exception e) {
                        obj.CloseLoadingScreen();
                        SendLogDeviceError(e,"private void SetPreferredDriverGenderId",false);
                        Common.DialogStatusAlert(Wizard_Login.this, getResources().getString(R.string.ORIGlobal_SharedPreferencesFailed_Msg), getResources().getString(R.string.ORIGlobal_SharedPreferencesFailed_Title),"Error");
                    }
                } catch (Exception e) {
                    obj.CloseLoadingScreen();
                    SendLogDeviceError(e,"private void SetPreferredDriverGenderId",true);
                }
            }
        });
    }
    private void SetPreferredPaymentTypeId(final Context context, final String UID) {
        _svcConnection.GetPreferredPaymentTypeId(Integer.parseInt(UID), new WSResponseListener() {
            @Override
            public void onError(String message) {
                try {
                    switch (message) {
                        case "Error_InvalidToken":
                            Common.LogoffByInvalidToken(Wizard_Login.this);
                            break;
                        case "NO_CONNECTION":
                            obj.CloseLoadingScreen();
                            Common.DialogStatusAlert(context,getResources().getString(R.string.ORI_NoInternetConnection_Msg),getResources().getString(R.string.ORI_NoInternetConnection_Title),"Error");
                            break;
                    }
                } catch (Exception e) {
                    obj.CloseLoadingScreen();
                    SendLogDeviceError(e,"private void SetPreferredPaymentTypeId",true);
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
                            edit.putString("settings_Payment", "3").apply();
                        } else {
                            edit.putString("settings_Payment", paymentId).apply();
                        }
                        Intent goMap = new Intent(Wizard_Login.this,Map_Driver.class);
//                        obj.CloseLoadingScreen();
                        startActivity(goMap);
                        finish();
                    } catch (Exception e) {
                        obj.CloseLoadingScreen();
                        SendLogDeviceError(e,"private void SetPreferredPaymentTypeId",false);
                        Common.DialogStatusAlert(Wizard_Login.this, getResources().getString(R.string.ORIGlobal_SharedPreferencesFailed_Msg), getResources().getString(R.string.ORIGlobal_SharedPreferencesFailed_Title),"Error");
                    }
                } catch (Exception e) {
                    obj.CloseLoadingScreen();
                    SendLogDeviceError(e,"private void SetPreferredPaymentTypeId",true);
                }
            }
        });
    }
    private void UpdateFCMId() {
        try {
            int UID = Integer.parseInt(obj.GetSharedPreferencesValue(Wizard_Login.this,"UID"));
            _svcConnection.UpdateDeviceFCMId(UID);
        } catch (Exception e) {
            SendLogDeviceError(e,"private void UpdateFCMId",true);
        }
    }
    private void UpdatePreferredPaymentType(final Context context) {
        try {
            final int UID = Integer.parseInt(obj.GetSharedPreferencesValue(Wizard_Login.this,"UID"));
            String paymentType = obj.GetSharedPreferencesValue(Wizard_Login.this,"settings_AvailablePayment");
            _svcConnection.UpdatePreferredPaymentTypeId(UID, paymentType, new WSResponseListener() {
                @Override
                public void onError(String message) {
                    try {
                        switch (message) {
                            case "Error_InvalidToken":
                                Common.LogoffByInvalidToken(Wizard_Login.this);
                                break;
                            case "NO_CONNECTION":
                                obj.CloseLoadingScreen();
                                Common.DialogStatusAlert(Wizard_Login.this,getResources().getString(R.string.ORI_NoInternetConnection_Msg),getResources().getString(R.string.ORI_NoInternetConnection_Title),getResources().getString(R.string.ORIDialog_Error_IconName));
                                break;
                        }
                    } catch (Exception e) {
                        obj.CloseLoadingScreen();
                        SendLogDeviceError(e,"private void UpdatePreferredPaymentType",true);
                    }
                }

                @Override
                public void onResponseObject(JSONArray jsonResponse) {
                    SetPreferredDriverGenderId(context, obj.GetSharedPreferencesValue(Wizard_Login.this,"UID"));
                }
            });
        } catch (Exception e) {
            obj.CloseLoadingScreen();
            SendLogDeviceError(e,"private void UpdatePreferredPaymentType",true);
        }
    }
    //endregion

    //region Close Bottom Dialog
    public void ForgotPassword_CloseWizardDialog() {
        try {
            forgotPasswordDialog.dismiss();
        } catch (Exception e) {
            SendLogDeviceError(e,"private void ForgotPassword_CloseWizardDialog",true);
        }
    }
    public void CreateAccount_CloseWizardDialog() {
        try {
            createAccountDialog.dismiss();
        } catch (Exception e) {
            SendLogDeviceError(e,"private void CreateAccount_CloseWizardDialog",true);
        }
    }
    //endregion

    //region On Permission Result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        try {
            hasLocPermissions = (ContextCompat.checkSelfPermission(Wizard_Login.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
            if (hasLocPermissions) {
                SharedPreferences pref = getApplicationContext().getSharedPreferences(getResources().getString(R.string.ORIGlobal_SharedPreferences), Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = pref.edit();
                if (!pref.getString("reqenv_message_old","").trim().isEmpty()) {
                    if (!pref.getString("reqenv_message_old","").contains(pref.getString("reqenv_message",""))) {
                        String oldMsg = pref.getString("reqenv_message","");
                        edit.putString("reqenv_message_old",oldMsg).apply();
                        Show_ReqEnvironment_WelcomeDialog();
                    }
                } else {
                    String oldMsg = pref.getString("reqenv_message","");
                    edit.putString("reqenv_message_old",oldMsg).apply();
                    Show_ReqEnvironment_WelcomeDialog();
                }
            } else {
                Common.DialogStatusAlert(Wizard_Login.this, getResources().getString(R.string.ORINoLocationPermissionsGrantedMsg), getResources().getString(R.string.ORINoLocationPermissionsGrantedTitle),getResources().getString(R.string.ORIDialog_Error_IconName));
            }
        } catch (Exception e) {
            SendLogDeviceError(e,"public void OnRequestPermissionsResult",true);
        }
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
    private void Show_ReqEnvironment_WelcomeDialog() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(getResources().getString(R.string.ORIGlobal_SharedPreferences), Context.MODE_PRIVATE);
        AlertDialog.Builder welcomeDialog = new AlertDialog.Builder(Wizard_Login.this);
        LayoutInflater inflater = getLayoutInflater();
        CoordinatorLayout nullParent = findViewById(R.id.LoginMainWrapper);
        View dialogView = inflater.inflate(R.layout.template_dialogstatusalert, nullParent,false);
        TextView dialogTitle = dialogView.findViewById(R.id.alertDialogTitle);
        TextView dialogMsg = dialogView.findViewById(R.id.alertDialogMsg);
        ImageView dialogIcon = dialogView.findViewById(R.id.alertDialogIcon);
        dialogTitle.setText(getResources().getString(R.string.WelcomeToOriString));
        dialogMsg.setText(pref.getString("reqenv_message",""));
        dialogIcon.setImageResource(R.drawable.ic_error);
        welcomeDialog.setView(dialogView);
        welcomeDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        welcomeDialog.setCancelable(false);
        welcomeDialog.show();
    }
    private void Show_HasLocPermissions(){
        AlertDialog.Builder permissionsDialog = new AlertDialog.Builder(Wizard_Login.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.template_dialogstatusalert, parent, false);
        TextView dialogTitle = dialogView.findViewById(R.id.alertDialogTitle);
        TextView dialogMsg = dialogView.findViewById(R.id.alertDialogMsg);
        ImageView dialogIcon = dialogView.findViewById(R.id.alertDialogIcon);
        dialogTitle.setText(getResources().getString(R.string.ORIAskingPermissions_Title));
        dialogMsg.setText(getResources().getString(R.string.ORIAskingPermissions_Msg));
        dialogIcon.setImageResource(R.drawable.ic_error);
        permissionsDialog.setView(dialogView);
        permissionsDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                            }, 100);
                        }
                    }
                } catch (Exception e) {
                    SendLogDeviceError(e,"permissionsDialog.setPositiveButton",true);
                }
            }
        });
        permissionsDialog.setCancelable(false);
        permissionsDialog.show();
    }
    private String AnyErrors(String uEmail, String uPassword) {
        String result = "";
        result += (uEmail.isEmpty()) ? getResources().getString(R.string.WizardLogin_Error_EmptyUnityEmail) + "\n" : "";
        result += (!Common.IsEmail(uEmail)) ? getResources().getString(R.string.WizardLogin_Error_InvalidUnityEmail) + "\n" : "";
        result += (uPassword.isEmpty()) ? getResources().getString(R.string.WizardLogin_Error_EmptyUnityPassword) : "";
        return result;
    }
    private Boolean SaveOnSharedPreferences(String uEmail, String hasedPassword,String UID, String gatewayId, String sessionTokenId, String lastName, String givenName, String nickName, String gender, String mobile, String isVerified) {
        try {
            SharedPreferences pref = getApplicationContext().getSharedPreferences(getResources().getString(R.string.ORIGlobal_SharedPreferences), Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = pref.edit();
            edit.putString("user_OnlineStatus","A");
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
            edit.putString("IsVerified",isVerified);
            edit.apply();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
//    private void goToMapActivity() {
//        Intent intent = new Intent(Wizard_Login.this, Map_Driver.class);
//        String welcomeMsg = "";
//        String opt = obj.GetSharedPreferencesValue(Wizard_Login.this, "Gender");
//        switch (opt) {
//            case "H":
//                welcomeMsg = "¡Bienvenido " + obj.GetSharedPreferencesValue(Wizard_Login.this, "GivenName") + "!";
//                break;
//            case "M":
//                welcomeMsg = "¡Bienvenida " + obj.GetSharedPreferencesValue(Wizard_Login.this, "GivenName") + "!";
//                break;
//        }
//        intent.putExtra("ORIWelcomeMsg", welcomeMsg);
//        obj.CloseLoadingScreen();
//        startActivity(intent);
//    }
    //endregion

    //region Log device Error
    private void SendLogDeviceError(Exception error, String method, boolean showDialog) {
        fullStack = obj.LogDeviceError(error);
        _svcConnection.LogDeviceError(getResources().getString(R.string.appName), BuildConfig.VERSION_NAME, method, "", "Wizard_Login", Build.MODEL, Build.MANUFACTURER, Integer.toString(Build.VERSION.SDK_INT), Build.VERSION.RELEASE, "", _UID, fullStack, error.getMessage(), Integer.toString(error.hashCode()));
        if (showDialog) {
            Common.DialogStatusAlert(Wizard_Login.this,getResources().getString(R.string.ORIUnexpectedErrorNSendLogToServer),getResources().getString(R.string.ORIUnexpectedError),"Error");
        }
    }
    //endregion


    @Override
    protected void onDestroy() {
        super.onDestroy();
        obj.CloseLoadingScreen();
    }
}
