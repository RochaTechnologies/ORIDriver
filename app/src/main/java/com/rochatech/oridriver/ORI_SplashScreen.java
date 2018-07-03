package com.rochatech.oridriver;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.rochatech.library.Common;
import com.rochatech.webService.WSResponseListener;
import com.rochatech.webService.connectToService;

public class ORI_SplashScreen extends AppCompatActivity {

    connectToService _svcConnection;
    Common obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_orisplashscreen);
        obj = new Common(ORI_SplashScreen.this);
        _svcConnection = new connectToService(ORI_SplashScreen.this, obj.GetSharedPreferencesValue(ORI_SplashScreen.this, "SessionToken"));
        AutoLogin();
    }

    //region WebService Calls
    private void StartLogin(final String uEmail, final String hashedPassword, final Context context) {
        _svcConnection.ValidateCredentials(uEmail, hashedPassword, new WSResponseListener() {
            @Override
            public void onError(String message) {
//                obj.GetProgressDialogLoadinScreen().dismiss();
                goToLogin(ORI_SplashScreen.this,"Lo sentimos, pero ocurrió un error al momento de iniciar sesión.","Ocurrió un error al tratar de iniciar sesión");
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
                            result = SaveOnSharedPeferences(uEmail, hashedPassword, UID, gatewayId, sessionTokenId, lastName, givenName, nickName, gender, mobile);
                            if (result) {
                                SetAvailablePaymentOnSharedPreferences(context, UID, sessionTokenId);
                            } else {
                                goToLogin(context, getResources().getString(R.string.ORIGlobal_SharedPreferencesFailed_Msg), getResources().getString(R.string.ORIGlobal_SharedPreferencesFailed_Title));
                            }
                            break;
                        case "Error_InvalidCredentials":
                            goToLogin(context,"Ocurrió un problema al momento de validar sus credenciales, de click en OK para ir al inicio de de sesión.","Credenciales incorrectas");
                            break;
                        case "Error_InactiveAccount":
                            goToLogin(context,"Su cuenta se encuentra inactiva, revise su correo electrónico para activar su cuenta.","Cuenta inactiva");
                            break;
                        case "Error_MembershipSuspended":
                            goToLogin(context,"Su membresía se encuentra suspendida.","Hemos suspendido su membresía");
                            break;
                        case "Error_MembershipInactive":
                            goToLogin(context,"Su membresía se encuentra inactiva, revise su correo electrónico para activarla.","Su membresía se encuentra inactiva");
                            break;
                        case "Error_WizardNotCompleted":
                            goToLogin(context, "Por favor termine de manera correcta su registro, revise su correo electrónico para seguir los pasos.", "Proceso de registro incompleto");
                            break;
                        case "Error_NotPaymentInformation":
                            //TODO preguntar a Raul si es bueno actualizar desde aqui el tipo de pago preferido, porque como no tengo informacion de pago pero mi opcion de pago preferido es ambos desde el web service
                            /*Primero guarda datos del usuario*/
                            result = SaveOnSharedPeferences(uEmail, hashedPassword, UID, gatewayId, sessionTokenId, lastName, givenName, nickName, gender, mobile);
                            if (result) {
                                /*Guardo en shared valores por default, estos pueden cambiar despues del SetAvailablePaymentOnSharedPreferences*/
                                /*NOTA: debo buscar un nombre mas corto para esa mamada de SetAvailablePaymentOnSharedPreferences*/
                                /*NOTA2: Simon por pendejo lo volvi a escribir :'(*/
                                SharedPreferences pref = getApplicationContext().getSharedPreferences(getResources().getString(R.string.ORIGlobal_SharedPreferences), Context.MODE_PRIVATE);
                                SharedPreferences.Editor edit = pref.edit();
                                edit.putString("settings_AvailablePayment", "3");
                                edit.putString("settings_Gender", "3");
                                edit.apply();
                                SetAvailablePaymentOnSharedPreferences(ORI_SplashScreen.this, UID, sessionTokenId);
                            } else {
                                goToLogin(context, getResources().getString(R.string.ORIGlobal_SharedPreferencesFailed_Msg), getResources().getString(R.string.ORIGlobal_SharedPreferencesFailed_Title));
                            }
                            break;
                        case "Error_MembershipExpired":
                            goToLogin(context, "Por favor contacte nuestro centro de atención a clientes para mas información.", "Su membresía ha expirado");
                            break;
                        default:
                            status = "";
                            goToLogin(context, "Lo sentimos, ocurrió un error inesperado. Favor de ponerse en contacto con nuestro centro de atención a clientes.", "Ocurrió un error inesperado");
                            break;
                    }
                } catch (JSONException e) {
                    goToLogin(context, e.toString(), getResources().getString(R.string.ORIGlobal_webServiceError));
                }
            }
        });

    }
    private void SetAvailablePaymentOnSharedPreferences(final Context context, final String UID, final String sessionTokenId) {
        _svcConnection.GetCardOrAcntForService(Integer.parseInt(UID), 0, "1", new WSResponseListener() {
            @Override
            public void onError(String message) {
                switch (message) {
                    case "Error_InvalidToken":
                        _svcConnection.LogOffUser(Integer.parseInt(obj.GetSharedPreferencesValue(ORI_SplashScreen.this,"UID")));
                        Common.LogoffByInvalidToken(ORI_SplashScreen.this);
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
                    if (bankActId.trim().isEmpty()) {
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
                        _svcConnection.LogOffUser(Integer.parseInt(obj.GetSharedPreferencesValue(ORI_SplashScreen.this,"UID")));
                        Common.LogoffByInvalidToken(ORI_SplashScreen.this);
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
                        goToLogin(ORI_SplashScreen.this,getResources().getString(R.string.ORIGlobal_SharedPreferencesFailed_Msg) + ".",getResources().getString(R.string.ORIGlobal_SharedPreferencesFailed_Title) + ".");
                    }
                } catch (JSONException e) {
                    goToLogin(ORI_SplashScreen.this,e.toString() + ".",getResources().getString(R.string.ORIGlobal_webServiceError) + ".");
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
                        _svcConnection.LogOffUser(Integer.parseInt(obj.GetSharedPreferencesValue(ORI_SplashScreen.this,"UID")));
                        Common.LogoffByInvalidToken(ORI_SplashScreen.this);
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
                        Intent intent = new Intent(ORI_SplashScreen.this, Map_Driver.class);
                        String welcomeMsg = "";
                        String opt = obj.GetSharedPreferencesValue(ORI_SplashScreen.this, "Gender");
                        switch (opt) {
                            case "H":
                                welcomeMsg = "¡Bienvenido " + obj.GetSharedPreferencesValue(ORI_SplashScreen.this, "GivenName") + "!";
                                break;
                            case "M":
                                welcomeMsg = "¡Bienvenida " + obj.GetSharedPreferencesValue(ORI_SplashScreen.this, "GivenName") + "!";
                                break;
                        }
                        intent.putExtra("ORIWelcomeMsg", welcomeMsg);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                        goToLogin(ORI_SplashScreen.this,getResources().getString(R.string.ORIGlobal_SharedPreferencesFailed_Msg) + ".",getResources().getString(R.string.ORIGlobal_SharedPreferencesFailed_Title) + ".");
                    }
                } catch (JSONException e) {
                    goToLogin(ORI_SplashScreen.this,e.toString() + ".",getResources().getString(R.string.ORIGlobal_webServiceError) + ".");
                }
            }
        });
    }
    //endregion

    //region MISC
    private Boolean TryLogin() {
        String UEmail = obj.GetSharedPreferencesValue(ORI_SplashScreen.this, "UnityEmail");
        Boolean result = false;
        try {
            result = (UEmail == null) ? false : true;
            if (result) {
                if (UEmail.trim().isEmpty()) {
                    result = false;
                }
            }
        } catch (Exception e) {
            result = false;
        }
        return result;
    }
    private void AutoLogin() {
        /*Inicio sesion automatico*/
        if (TryLogin()) {
            String UEmail = obj.GetSharedPreferencesValue(ORI_SplashScreen.this, "UnityEmail");
            String hasedPassword = obj.GetSharedPreferencesValue(ORI_SplashScreen.this, "UnityPassword");
            StartLogin(UEmail, hasedPassword, ORI_SplashScreen.this);
        } else {
            Intent intent = new Intent(ORI_SplashScreen.this, Wizard_Login.class);
            startActivity(intent);
            finish();
        }
    }
    private Boolean SaveOnSharedPeferences(String uEmail, String hasedPassword,String UID, String gatewayId, String sessionTokenId, String lastName, String givenName, String nickName, String gender, String mobile) {
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
//            edit.putString("app_askagain","Y");
            edit.apply();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    private void goToLogin(final Context context, String msg, String title){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.template_dialogstatusalert,null);
        TextView dialogTitle = dialogView.findViewById(R.id.alertDialogTitle);
        TextView dialogMsg = dialogView.findViewById(R.id.alertDialogMsg);
        ImageView dialogIcon = dialogView.findViewById(R.id.alertDialogIcon);
        dialogTitle.setText(title);
        dialogMsg.setText(msg);
        dialogIcon.setImageResource(R.drawable.ic_error);
        dialog.setView(dialogView);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(context, Wizard_Login.class);
                context.startActivity(intent);
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }
    //endregion

}
