package com.rochatech.oridriver;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import com.rochatech.library.Common;
import com.rochatech.webService.*;
import com.rochatech.Model.AvailableCities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Wizard_CreateAccount extends AppCompatActivity {

    connectToService _svcConnection;
    Common obj;

    EditText UserMobile, UserEmail, UserPassword, UserConfirmPassword;
    Spinner Cities;
    SwitchCompat AcceptedTerms;
    Button TermsDialog, StartCreatingAccountPressed;

    String CountryName = "";
    ArrayList<Integer> CID = new ArrayList<>();
    boolean goToCreditCard = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wizard_createaccount_activity);
        obj = new Common(Wizard_CreateAccount.this);
        _svcConnection = new connectToService(Wizard_CreateAccount.this, obj.GetSharedPreferencesValue(Wizard_CreateAccount.this, "SessionToken"));
//        obj.ShowLoadingScreen(Wizard_CreateAccount.this, "Cargando", "Por favor espere...");
        InitAppControls();
        LoadAvailableCities();
        TermsDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        StartCreatingAccountPressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                obj.ShowLoadingScreen(Wizard_CreateAccount.this, "Creando cuenta", "Por favor espere...");
                CreateAccount();
            }
        });


        //region Hide Keyboard
        UserMobile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Common.HideKeyboard(v, Wizard_CreateAccount.this);
            }
        });
        UserEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Common.HideKeyboard(v, Wizard_CreateAccount.this);
            }
        });
        UserPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Common.HideKeyboard(v, Wizard_CreateAccount.this);
            }
        });
        UserConfirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Common.HideKeyboard(v, Wizard_CreateAccount.this);
            }
        });
        //endregion
    }

    private void CreateAccount() {
        int city = CID.get(Cities.getSelectedItemPosition());
        String mobile = UserMobile.getText().toString();
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();
        String confirmPassword = UserConfirmPassword.getText().toString();
        boolean terms = AcceptedTerms.isChecked();
        String Errors = AnyErrors(city, mobile, email, password, confirmPassword, terms);
        if (Errors.trim().isEmpty()) {
            CreateActWebService(city, email, mobile, Common.SHA256(password));
        } else {
            obj.CloseLoadingScreen();
//            Common.DialogStatusAlert(Wizard_CreateAccount.this, Errors, getResources().getString(R.string.ORIGlobal_AnyErrorsTitle), false, "Error");
        }
    }

    private void CreateActWebService(int city, String email, String mobile, String hashedPassword) {
        Bitmap picture = null;
        String nickName = obj.GetSharedPreferencesValue(Wizard_CreateAccount.this, "NickName");
        String gender = obj.GetSharedPreferencesValue(Wizard_CreateAccount.this, "Gender");
        String lastName = obj.GetSharedPreferencesValue(Wizard_CreateAccount.this, "LastName");
        String givenName = obj.GetSharedPreferencesValue(Wizard_CreateAccount.this, "GivenName");
        _svcConnection.CreateNewAccount(email, hashedPassword, mobile, city, nickName, lastName, givenName, gender, picture, new WSResponseListener() {
            @Override
            public void onError(String message) {
                obj.CloseLoadingScreen();
//                Common.DialogStatusAlert(Wizard_CreateAccount.this, message, getResources().getString(R.string.ORIGlobal_webServiceError), false, "Error");
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                try {
                    JSONObject response = jsonResponse.getJSONObject(0);
                    String status = response.getString("Status");
                    switch (status) {
                        case "UserAdded":
                            /*Recoger el UnityId y el GatewayId*/
                            String UID = response.getString("UID");
                            String GID = response.getString("GID");
                            /*Guardarlos en las preferencias*/
                            SharedPreferences pref = Wizard_CreateAccount.this.getSharedPreferences(getResources().getString(R.string.ORIGlobal_SharedPreferences), Context.MODE_PRIVATE);
                            SharedPreferences.Editor edit = pref.edit();
                            edit.putString("UID", UID);
                            edit.putString("GID", GID);
                            edit.apply();
                            AlertDialog.Builder dialog = new AlertDialog.Builder(Wizard_CreateAccount.this);
                            dialog.setTitle(getResources().getString(R.string.WizardCreateAccount_UserAddedTitle));
                            dialog.setTitle(getResources().getString(R.string.WizardCreateUserAccount_UserAddedMsg));
                            dialog.setIcon(R.drawable.ic_success);
                            dialog.setCancelable(false);
                            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    obj.CloseLoadingScreen();
                                    Intent intent = new Intent(Wizard_CreateAccount.this, Wizard_Login.class);
//                                    intent.putExtra("FromActivity", "WizardCreateAccount");
                                    startActivity(intent);
                                }
                            });
                            break;
                        case "Error_InactiveAccount":
                            obj.CloseLoadingScreen();
//                            Common.DialogStatusAlert(Wizard_CreateAccount.this, getResources().getString(R.string.WizardCreateAccount_InactiveAccountMsg), getResources().getString(R.string.WizardCreateAccount_InactiveAccountTitle), false, "Error");
                            break;
                        case "EXIST":
                            obj.CloseLoadingScreen();
//                            Common.DialogStatusAlert(Wizard_CreateAccount.this, getResources().getString(R.string.WizardCreateAccount_ExistMsg), getResources().getString(R.string.WizardCreateAccount_ExistTitle), false, "Error");
                            break;
                        case "EXIST_":
                            obj.CloseLoadingScreen();
//                            Common.DialogStatusAlert(Wizard_CreateAccount.this, getResources().getString(R.string.WizardCreateAccount_ExistMsg), getResources().getString(R.string.WizardCreateAccount_ExistTitle), false, "Error");
                            break;
                    }
                } catch (JSONException e) {
                    obj.CloseLoadingScreen();
//                    Common.DialogStatusAlert(Wizard_CreateAccount.this, e.toString(), getResources().getString(R.string.ORIGlobal_webServiceError), false, "Error");
                }
            }
        });
    }


    //region MISC
    private void InitAppControls() {
        UserMobile = findViewById(R.id.UserMobile);
        UserEmail = findViewById(R.id.UserEmail);
        UserPassword = findViewById(R.id.UserPassword);
        UserConfirmPassword = findViewById(R.id.UserConfirmPassword);
        Cities = findViewById(R.id.AvailableCities);
        AcceptedTerms = findViewById(R.id.AcceptedTermsandConditions);
        TermsDialog = findViewById(R.id.TermsAndConditionsDialog);
        StartCreatingAccountPressed = findViewById(R.id.StartCreatingAccountPressed);
    }

    private void LoadAvailableCities() {
        _svcConnection.GetAvailableCities(new WSResponseListener() {
            @Override
            public void onError(String message) {
//                Common.DialogStatusAlert(Wizard_CreateAccount.this, message, getResources().getString(R.string.WizardCreateAccount_LoadCitiesFailed),false, "Error");
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                ArrayList<AvailableCities> city = AvailableCities.fromJson(jsonResponse);
                ArrayList<String> list = new ArrayList<>();
                int i = 0;
                for (Iterator<AvailableCities> item = city.iterator(); item.hasNext();){
                    try {
                        AvailableCities _tmp = item.next();
                        CountryName = _tmp.getCountryName();
                        String _fullname = _tmp.getCityName() + " ," + _tmp.getStateName();
                        list.add(i, _fullname);
                        CID.add(i, _tmp.getCityId());
                        i++;
                    } catch (Exception e) {
                        Log.d("ERROR", "onResponseObject: " + e.toString());
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(Wizard_CreateAccount.this,android.R.layout.simple_spinner_item,list);
                adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
                Cities.setAdapter(adapter);
                obj.CloseLoadingScreen();
            }
        });
    }

    private String AnyErrors(int cityId, String mobile, String email, String password, String confirmPassword, boolean AcceptedTerms){
        String result = "";
        //TODO agregar validacion por medio de codigo para el numero celular v2.0
        result += (mobile.trim().isEmpty()) ? getResources().getString(R.string.WizardCreateAccount_MobileEmpty) + "\n" : "";
        result += (mobile.length() < 10) ? getResources().getString(R.string.WizardCreateAccount_MobileNotValid) + "\n"  : "";
        result += (cityId < 1) ? getResources().getString(R.string.WizardCreateAccount_NoCitySelected) + "\n"  : "";
        result += (email.trim().isEmpty()) ? getResources().getString(R.string.WizardCreateAccount_EmailEmpty) + "\n"  : "";
        result += (!Common.IsEmail(email)) ? getResources().getString(R.string.WizardCreateAccount_EmailNotValid) + "\n"  : "";
        result += (password.trim().isEmpty()) ? getResources().getString(R.string.WizardCreateAccount_PasswordEmpty) + "\n"  : "";
        result += (!password.equals(confirmPassword)) ? getResources().getString(R.string.WizardCreateAccount_PasswordNotSame) + "\n"  : "";
        result += (!AcceptedTerms) ? getResources().getString(R.string.WizardCreateAccount_TermsNotAccepted) : "";
        return result;
    }
    //endregion
}
