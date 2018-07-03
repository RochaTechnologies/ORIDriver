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
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Settings_DriverNPayment extends AppCompatActivity {

    LinearLayoutCompat maleDriver, femaleDriver, bothDriver, creditPayment, cashPayment, bothPayment, SettingsLinear;
    ImageView maleIcon, femaleIcon, creditCardIcon, cashIcon, bothPaymentIcon;
    ImageView malecheck, femalecheck, bothdrivercheck, cashCheck, creditCheck, bothPaymentCheck;
    TextView maleTitle, maleDescription, femaleTitle, femaleDescription, creditCardTitle, cashTitle, bothPaymentTitle;
    connectToService _svcConnection;
    Common obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_drivernpayment_activity);
        obj = new Common(Settings_DriverNPayment.this);
        _svcConnection = new connectToService(Settings_DriverNPayment.this, obj.GetSharedPreferencesValue(Settings_DriverNPayment.this, "SessionToken"));
        obj.ShowLoadingScreen(Settings_DriverNPayment.this,"Estamos cargando tus preferencias...");
        SettingsLinear = findViewById(R.id.SettingsLinear);
        InitAppControls();
        LockDriverByUserGender();
        SetPreferredDriverGender();
        LockAvailablePayment();
        SetPreferredPaymentType();
        //endregion
    }

    //region Driver click Events
    public void MaleDriver_Click(View v) {
        if (malecheck.getVisibility() != View.VISIBLE) {
            int UID = Integer.parseInt(obj.GetSharedPreferencesValue(Settings_DriverNPayment.this,"UID"));
            obj.ShowLoadingScreen(Settings_DriverNPayment.this,"Actualizando a solo hombres...");
            MaleDriverSelected();
            UpdatePreferredDriverGender(UID, "1");
        }
    }
    public void FemaleDriver_Click(View v) {
        if (femalecheck.getVisibility() != View.VISIBLE) {
            int UID = Integer.parseInt(obj.GetSharedPreferencesValue(Settings_DriverNPayment.this,"UID"));
            obj.ShowLoadingScreen(Settings_DriverNPayment.this,"Actualizando a solo mujeres...");
            FemaleDriverSelected();
            UpdatePreferredDriverGender(UID, "2");
        }
    }
    public void BothDriver_Click(View v) {
        if (bothdrivercheck.getVisibility() != View.VISIBLE) {
            int UID = Integer.parseInt(obj.GetSharedPreferencesValue(Settings_DriverNPayment.this,"UID"));
            obj.ShowLoadingScreen(Settings_DriverNPayment.this,"Actualizando a ambos...");
            BothDriverSelected();
            UpdatePreferredDriverGender(UID, "3");
        }
    }
    //endregion

    //region Payment click Events
    public void CreditPayment_Click(View v) {
        if (creditCheck.getVisibility() != View.VISIBLE) {
            int UID = Integer.parseInt(obj.GetSharedPreferencesValue(Settings_DriverNPayment.this,"UID"));
            obj.ShowLoadingScreen(Settings_DriverNPayment.this,"Actualizando a tarjeta de crédito...");
            CreditPaymentSelected();
            UpdatePreferredPayment(UID, "2");
        }
    }
    public void CashPayment_Click(View v) {
        if (cashCheck.getVisibility() != View.VISIBLE) {
            int UID = Integer.parseInt(obj.GetSharedPreferencesValue(Settings_DriverNPayment.this,"UID"));
            obj.ShowLoadingScreen(Settings_DriverNPayment.this,"Actualizando a efectivo...");
            CashPaymentSelected();
            UpdatePreferredPayment(UID, "1");
        }
    }
    public void BothPayment_Click(View v) {
        if (bothPaymentCheck.getVisibility() != View.VISIBLE) {
            int UID = Integer.parseInt(obj.GetSharedPreferencesValue(Settings_DriverNPayment.this,"UID"));
            obj.ShowLoadingScreen(Settings_DriverNPayment.this,"Actualizando a ambos...");
            BothPaymentSelected();
            UpdatePreferredPayment(UID, "3");
        }
    }
    //endregion

    //region WebService Call
    private void UpdatePreferredDriverGender(int UnityId, final String Gender) {
        _svcConnection.UpdatePreferredDriverGenderTypeId(UnityId, Gender, new WSResponseListener() {
            @Override
            public void onError(String message) {
                switch (message) {
                    case "Error_InvalidToken":
                        obj.CloseLoadingScreen();
                        Common.LogoffByInvalidToken(Settings_DriverNPayment.this);
                        break;
                }
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                try {
                    //response "[{"Agregado":0}]"
                    JSONObject response = jsonResponse.getJSONObject(0);
                    String result = response.getString("Agregado");
                    if (Driver_SaveSharedPreferences(Gender)) {
                        switch (Gender) {
                            case "1":
                                malecheck.setVisibility(View.VISIBLE);
                                femalecheck.setVisibility(View.INVISIBLE);
                                bothdrivercheck.setVisibility(View.INVISIBLE);
                                break;
                            case "2":
                                malecheck.setVisibility(View.INVISIBLE);
                                femalecheck.setVisibility(View.VISIBLE);
                                bothdrivercheck.setVisibility(View.INVISIBLE);
                                break;
                            case "3":
                                malecheck.setVisibility(View.INVISIBLE);
                                femalecheck.setVisibility(View.INVISIBLE);
                                bothdrivercheck.setVisibility(View.VISIBLE);
                                break;
                        }
                        obj.CloseLoadingScreen();
                        Snackbar snackbar = Snackbar.make(SettingsLinear,"¡Género de conductor preferido actualizado!",Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    } else {
                        obj.CloseLoadingScreen();
                        Common.DialogStatusAlert(Settings_DriverNPayment.this,getResources().getString(R.string.ORIGlobal_SharedPreferencesFailed_Msg),getResources().getString(R.string.ORIGlobal_SharedPreferencesFailed_Title),"Error");
                    }

                } catch (JSONException e) {
                    obj.CloseLoadingScreen();
                    Common.DialogStatusAlert(Settings_DriverNPayment.this,e.toString(),getResources().getString(R.string.ORIGlobal_webServiceError),"Error");
                }
            }
        });
    }
    private void UpdatePreferredPayment(int UnityId, final String Payment) {
        _svcConnection.UpdatePreferredPaymentTypeId(UnityId, Payment, new WSResponseListener() {
            @Override
            public void onError(String message) {
                switch (message) {
                    case "Error_InvalidToken":
                        obj.CloseLoadingScreen();
                        Common.LogoffByInvalidToken(Settings_DriverNPayment.this);
                        break;
                }
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                try {
                    JSONObject response = jsonResponse.getJSONObject(0);
                    //result contiene "0"
                    String result = response.getString("Agregado");
                    if (Payment_SaveSharedPreferences(Payment)) {
                        switch (Payment) {
                            case "1":
                                CashPaymentSelected();
                                break;
                            case "2":
                                CreditPaymentSelected();
                                break;
                            case "3":
                                BothPaymentSelected();
                                break;
                        }
                        obj.CloseLoadingScreen();
                        Snackbar snackbar = Snackbar.make(SettingsLinear,"¡Tipo de pago preferido actualizado!",Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    } else {
                        obj.CloseLoadingScreen();
                        Common.DialogStatusAlert(Settings_DriverNPayment.this,getResources().getString(R.string.ORIGlobal_SharedPreferencesFailed_Msg),getResources().getString(R.string.ORIGlobal_SharedPreferencesFailed_Title),"Error");
                    }
                } catch (JSONException e) {
                    obj.CloseLoadingScreen();
                    Common.DialogStatusAlert(Settings_DriverNPayment.this,e.toString(),getResources().getString(R.string.ORIGlobal_webServiceError),"Error");
                }
            }
        });
    }
    //endregion

    //region Set up by option selected
    private void MaleDriverSelected() {
        malecheck.setVisibility(View.VISIBLE);
        femalecheck.setVisibility(View.INVISIBLE);
        bothdrivercheck.setVisibility(View.INVISIBLE);
    }
    private void FemaleDriverSelected() {
        malecheck.setVisibility(View.INVISIBLE);
        femalecheck.setVisibility(View.VISIBLE);
        bothdrivercheck.setVisibility(View.INVISIBLE);
    }
    private void BothDriverSelected() {
        malecheck.setVisibility(View.INVISIBLE);
        femalecheck.setVisibility(View.INVISIBLE);
        bothdrivercheck.setVisibility(View.VISIBLE);
    }
    private void CreditPaymentSelected() {
        creditCheck.setVisibility(View.VISIBLE);
        cashCheck.setVisibility(View.INVISIBLE);
        bothPaymentCheck.setVisibility(View.INVISIBLE);
    }
    private void CashPaymentSelected() {
        cashCheck.setVisibility(View.VISIBLE);
        creditCheck.setVisibility(View.INVISIBLE);
        bothPaymentCheck.setVisibility(View.INVISIBLE);
    }
    private void BothPaymentSelected() {
        bothPaymentCheck.setVisibility(View.VISIBLE);
        cashCheck.setVisibility(View.INVISIBLE);
        creditCheck.setVisibility(View.INVISIBLE);
    }
    //endregion

    //region Setup
    private void LockDriverByUserGender() {
        String userGender = obj.GetSharedPreferencesValue(Settings_DriverNPayment.this,"Gender");
        switch (userGender) {
            case "H":
                femaleDriver.setEnabled(false);
                femaleIcon.setColorFilter(getResources().getColor(R.color.ORILightGray));
                femaleTitle.setTextColor(getResources().getColor(R.color.ORILightGray));
                femaleDescription.setTextColor(getResources().getColor(R.color.ORILightGray));
                break;
            case "M":
                maleDriver.setEnabled(false);
                maleIcon.setColorFilter(getResources().getColor(R.color.ORILightGray));
                maleTitle.setTextColor(getResources().getColor(R.color.ORILightGray));
                maleDescription.setTextColor(getResources().getColor(R.color.ORILightGray));
                break;
        }
    }
    private void SetPreferredDriverGender() {
        String prefDriver = obj.GetSharedPreferencesValue(Settings_DriverNPayment.this,"settings_Gender");
        switch (prefDriver){
            case "1":
                malecheck.setVisibility(View.VISIBLE);
                break;
            case "2":
                femalecheck.setVisibility(View.VISIBLE);
                break;
            case "3":
                bothdrivercheck.setVisibility(View.VISIBLE);
                break;
        }
    }
    private void LockAvailablePayment() {
        String availablePayment = obj.GetSharedPreferencesValue(Settings_DriverNPayment.this,"settings_AvailablePayment");
        switch (availablePayment) {
            case "0":
                //No tiene ninguna tarjeta puesta, quiere decir que solo puede pagar en efectivo
                creditPayment.setEnabled(false);
                bothPayment.setEnabled(false);
                creditCardIcon.setColorFilter(getResources().getColor(R.color.ORILightGray));
                creditCardTitle.setTextColor(getResources().getColor(R.color.ORILightGray));
                bothPaymentIcon.setColorFilter(getResources().getColor(R.color.ORILightGray));
                bothPaymentTitle.setTextColor(getResources().getColor(R.color.ORILightGray));
                break;
            case "1":
                //Es efectivo, por lo tanto no tiene tarjeta dada de alta y no puede seleccionar "Ambos" o "Tarjeta de credito" como opcion preferida
                creditPayment.setEnabled(false);
                bothPayment.setEnabled(false);
                creditCardIcon.setColorFilter(getResources().getColor(R.color.ORILightGray));
                creditCardTitle.setTextColor(getResources().getColor(R.color.ORILightGray));
                bothPaymentIcon.setColorFilter(getResources().getColor(R.color.ORILightGray));
                bothPaymentTitle.setTextColor(getResources().getColor(R.color.ORILightGray));
                break;
            case "3":
                //Es ambas, por lo tanto tiene una tarjeta agregada
                creditPayment.setEnabled(true);
                bothPayment.setEnabled(true);
                creditCardIcon.setImageResource(R.drawable.ic_menu_creditcard);
                creditCardTitle.setTextColor(getResources().getColor(R.color.ORIBlack));
                bothPaymentIcon.setImageResource(R.drawable.ic_cash);
                bothPaymentTitle.setTextColor(getResources().getColor(R.color.ORIBlack));
                break;
        }
    }
    private void SetPreferredPaymentType() {
        String prefPayment = obj.GetSharedPreferencesValue(Settings_DriverNPayment.this,"settings_Payment");
        String availablePayment = obj.GetSharedPreferencesValue(Settings_DriverNPayment.this,"settings_AvailablePayment");
        int UID = Integer.parseInt(obj.GetSharedPreferencesValue(Settings_DriverNPayment.this,"UID"));
        if (!prefPayment.trim().isEmpty()) {
            switch (prefPayment) {
                case "1":
                    /*  Efectivo, no tiene una tarjeta dada de alta
                        Actualizar el tipo de pago preferido en los sharedpreferences   */
                    Payment_SaveSharedPreferences("1");
                    cashCheck.setVisibility(View.VISIBLE);
                    break;
                case "2":
                    /*  Tarjeta, tiene una tarjeta dada de alta
                        Actualizar el tipo de pago preferido en los sharedpreferences   */
                    Payment_SaveSharedPreferences("2");
                    creditCheck.setVisibility(View.VISIBLE);
                    break;
                case "3":
                    /*  Ambos, osea que tiene una tarjeta dada de alta
                        Actualizar el tipo de pago preferido en los sharedpreferences   */
                    Payment_SaveSharedPreferences("3");
                    bothPaymentCheck.setVisibility(View.VISIBLE);
                    break;
            }
            obj.CloseLoadingScreen();
        } else {

        }
    }
    //endregion

    //region MISC
    private void InitAppControls() {
        maleDriver = findViewById(R.id.maleDriverLayout);
        maleIcon = findViewById(R.id.maleDriverIcon);
        maleTitle = findViewById(R.id.maleDriverTitle);
        maleDescription = findViewById(R.id.maleDriverDescription);
        femaleDriver = findViewById(R.id.femaleDriverLayout);
        femaleIcon = findViewById(R.id.femaleDriverIcon);
        femaleTitle = findViewById(R.id.femaleDriverTitle);
        femaleDescription = findViewById(R.id.femaleDriverDescription);
        bothDriver = findViewById(R.id.bothDriverLayout);

        malecheck = findViewById(R.id.maledrivercheck);
        femalecheck = findViewById(R.id.femaledrivercheck);
        bothdrivercheck = findViewById(R.id.bothdrivercheck);

        cashPayment = findViewById(R.id.cashPaymentLayout);
        creditPayment = findViewById(R.id.creditPaymentLayout);
        bothPayment = findViewById(R.id.bothPaymentLayout);
        cashCheck = findViewById(R.id.cashpaymentcheck);
        creditCheck = findViewById(R.id.creditpaymentcheck);
        bothPaymentCheck = findViewById(R.id.bothpaymentcheck);
        creditCardIcon = findViewById(R.id.paymentCreditcardIcon);
        cashIcon = findViewById(R.id.paymentCashIcon);
        bothPaymentIcon = findViewById(R.id.bothPaymentIcon);
        creditCardTitle = findViewById(R.id.creditCardTitle);
        cashTitle = findViewById(R.id.cashTitle);
        bothPaymentTitle = findViewById(R.id.bothPaymentTitle);
    }
    private Boolean Driver_SaveSharedPreferences(String driverGender) {
        try {
            SharedPreferences preferences = getApplicationContext().getSharedPreferences(getResources().getString(R.string.ORIGlobal_SharedPreferences), Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = preferences.edit();
            edit.putString("settings_Gender", driverGender);
            edit.apply();
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
    private Boolean Payment_SaveSharedPreferences(String paymentType) {
        try {
            SharedPreferences preferences = getApplicationContext().getSharedPreferences(getResources().getString(R.string.ORIGlobal_SharedPreferences), Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = preferences.edit();
            edit.putString("settings_Payment", paymentType);
            edit.apply();
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
    //endregion
}
