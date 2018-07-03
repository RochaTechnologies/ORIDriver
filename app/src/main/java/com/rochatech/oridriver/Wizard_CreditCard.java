package com.rochatech.oridriver;

import com.rochatech.library.Common;
import com.rochatech.webService.*;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import mx.openpay.android.Openpay;
import mx.openpay.android.OperationCallBack;
import mx.openpay.android.OperationResult;
import mx.openpay.android.exceptions.OpenpayServiceException;
import mx.openpay.android.exceptions.ServiceUnavailableException;
import mx.openpay.android.model.Card;
import mx.openpay.android.model.Token;

public class Wizard_CreditCard extends AppCompatActivity {

    connectToService _svcConnection;
    Common obj;

    AppCompatSpinner availableMonth, availableYear;
    EditText cHolderName, cCardNumber, CVV2;
    Button cvvDialog, CreditCardPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wizard_creditcard_activity);
        obj = new Common(Wizard_CreditCard.this);
        _svcConnection = new connectToService(Wizard_CreditCard.this, obj.GetSharedPreferencesValue(Wizard_CreditCard.this, "SessionToken"));
        InitAppControls();
        LoadSpinner();

        CreditCardPressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obj.ShowLoadingScreen(Wizard_CreditCard.this, "Guardando los datos de la tarjeta");
                CreateToken();
            }
        });

        cvvDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    ImageView cvv = new ImageView(getApplicationContext());
                    cvv.setImageResource(R.drawable.ic_cvv);
                    AlertDialog.Builder builder = new AlertDialog.Builder(Wizard_CreditCard.this);
                    builder.setTitle("¿Dónde se encuentra el código de seguridad de mi tarjeta?");
                    builder.setMessage("Se encuentra al reverso o en la parte delantera de su tarjeta bancaria, consta de 3 o 4 dígitos");
                    builder.setView(cvv);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
            }
        });

        //region Hide keyboard
        cHolderName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Common.HideKeyboard(v, Wizard_CreditCard.this);
            }
        });
        cCardNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Common.HideKeyboard(v, Wizard_CreditCard.this);
            }
        });
        CVV2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Common.HideKeyboard(v, Wizard_CreditCard.this);
            }
        });
        //endregion
    }


    //region WebServiceCall
    private void CreateToken() {
        String holderName = cHolderName.getText().toString();
        String cardNumber = cCardNumber.getText().toString();
        int expMonth = Integer.parseInt(availableMonth.getSelectedItem().toString());
        int expYear = Integer.parseInt(availableYear.getSelectedItem().toString());
        String cvv2 = CVV2.getText().toString();
        String Errors = AnyErrors(holderName, cardNumber, expMonth, expYear, cvv2);
        if (!Errors.trim().isEmpty()) {
            try {
                Card paymentCard = new Card();
                paymentCard.holderName(holderName);
                paymentCard.cardNumber(cardNumber);
                paymentCard.expirationMonth(expMonth);
                paymentCard.expirationYear(expYear);
                paymentCard.cvv2(cvv2);
                Openpay openpay = obj.getOpenPay();
                String deviceSessionId = openpay.getDeviceCollectorDefaultImpl().setup(Wizard_CreditCard.this);
                StartCreatingToken(openpay, paymentCard, deviceSessionId, cvv2);
            } catch (Exception e) {
                obj.CloseLoadingScreen();
                Common.DialogStatusAlert(Wizard_CreditCard.this, e.toString(), "Ocurrió un error al momento de iniciar el proceso para guardar su tarjeta","Error");
            }
        } else {
            obj.CloseLoadingScreen();
            Common.DialogStatusAlert(Wizard_CreditCard.this, Errors, getResources().getString(R.string.ORIGlobal_AnyErrorsTitle),"Error");
        }
    }

    private void StartCreatingToken(Openpay openpay, Card paymentCard, final String deviceSessionId, final String cvv2) {
        openpay.createToken(paymentCard, new OperationCallBack<Token>() {
            @Override
            public void onError(OpenpayServiceException e) {
                int openpayError = e.getErrorCode();
                openpayOnError(openpayError);
            }

            @Override
            public void onCommunicationError(ServiceUnavailableException e) {
                obj.CloseLoadingScreen();
                Common.DialogStatusAlert(Wizard_CreditCard.this, e.toString(), "Ocurrió un error de comunicación al momento de procesar su tarjeta" ,"Error");
            }

            @Override
            public void onSuccess(OperationResult<Token> operationResult) {
                String UID = obj.GetSharedPreferencesValue(Wizard_CreditCard.this, "UID");
                String GTID = operationResult.getResult().getId();
                String GCID = obj.GetSharedPreferencesValue(Wizard_CreditCard.this, "GID");
                String securityCode = Common.SHA256(cvv2 + GCID);
                AssignCardToCostumer(UID, GTID, GCID, deviceSessionId, securityCode);
            }
        });
    }

    private void AssignCardToCostumer(final String UID, String GTID, String GCID, String deviceSessionId, String securityCode) {
        _svcConnection.AssignCardToCustomerInGateway(Integer.parseInt(UID), GTID, GCID, deviceSessionId, securityCode, new WSResponseListener() {
            @Override
            public void onError(String message) {
                obj.CloseLoadingScreen();
                Common.DialogStatusAlert(Wizard_CreditCard.this, message, getResources().getString(R.string.ORIGlobal_webServiceError),"Error");
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                try {
                    JSONObject response = jsonResponse.getJSONObject(0);
                    String status = response.getString("Status");
                    switch (status) {
                        case "OK":
                            try {
                                /*Actualizar forma disponible de pago y pago preferido*/
                                SharedPreferences pref = getApplicationContext().getSharedPreferences(getResources().getString(R.string.ORIGlobal_SharedPreferences), Context.MODE_PRIVATE);
                                SharedPreferences.Editor edit = pref.edit();
                                edit.putString("settings_AvailablePayment", "3");
                                edit.putString("settings_Payment", "3");
                                edit.apply();
                                /*Actualizar pago preferido en Unity*/
                                UpdatePreferredPaymentType(UID, "3");
                            } catch (Exception e) {
                                obj.CloseLoadingScreen();
                                Common.DialogStatusAlert(Wizard_CreditCard.this, e.toString(),getResources().getString(R.string.ORIGlobal_webServiceError),"Error");
                            }
                            break;
                    }
                } catch (JSONException e) {
                    obj.CloseLoadingScreen();
                    Common.DialogStatusAlert(Wizard_CreditCard.this, e.toString(),getResources().getString(R.string.ORIGlobal_webServiceError),"Error");
                }
            }
        });
    }

    private void UpdatePreferredPaymentType(String UID, String payment) {
        _svcConnection.UpdatePreferredPaymentTypeId(Integer.parseInt(UID), payment, new WSResponseListener() {
            @Override
            public void onError(String message) {
                switch (message) {
                    case "Error_InvalidToken":
                        LogoffUser(obj);
                        break;
                }
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                try {
                    JSONObject response = jsonResponse.getJSONObject(0);
                    String result = response.getString("Agregado");
                    switch (result) {
                        case "0":
                            obj.CloseLoadingScreen();
                            response = jsonResponse.getJSONObject(0);
                            Intent intent = new Intent(Wizard_CreditCard.this, Settings_BankAccount.class);
                            intent.putExtra("resultMsg","¡Tarjeta agregada!");
                            startActivity(intent);
                            break;
                    }
                } catch (JSONException e) {
                    obj.CloseLoadingScreen();
                    Common.DialogStatusAlert(Wizard_CreditCard.this,e.toString(),getResources().getString(R.string.ORIGlobal_webServiceError),"Error");
                }
            }
        });
    }


    //emdregion


    //region MISC
    private void InitAppControls() {
        availableMonth = findViewById(R.id.SprMonth);
        availableYear = findViewById(R.id.AvailableYear);
        cHolderName = findViewById(R.id.HolderName);
        cCardNumber = findViewById(R.id.CardNumber);
        CVV2 = findViewById(R.id.CVV);
        CreditCardPressed = findViewById(R.id.AssignCreditCardPressed);
        cvvDialog = findViewById(R.id.cvvDialog);
    }
    private void LoadSpinner() {
        ArrayList<Integer> months = new ArrayList<>();
        for (int j = 1; j <= 12; j++) {
            months.add(j);
        }
        ArrayAdapter<Integer> monthAdapter = new ArrayAdapter<>(Wizard_CreditCard.this, android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        availableMonth.setAdapter(monthAdapter);
        ArrayList<Integer> availableYears = new ArrayList<>();
        int currentYear = Integer.parseInt(Common.getDateComp("YearLastDigits", Common.getNow()));
        for (int i = 0; i <= 10; i++) {
            availableYears.add(currentYear);
            currentYear++;
        }
        ArrayAdapter<Integer> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, availableYears);
        yearAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        availableYear.setAdapter(yearAdapter);
    }
    private String AnyErrors(String holderName, String cardNumber, int expoMonth, int expYear, String cvv2) {
        String result = "";
        result += (holderName.trim().isEmpty()) ? getResources().getString(R.string.WizardCreditCard_CardHolderNameEmpty) + "\n" : "";
        result += (holderName.matches(".*\\d+.*")) ? getResources().getString(R.string.WizardCreditCard_CardHolderNameLettersOnly) + "\n" : "";
        result += (holderName.matches("[a-zA-Z0-9 ]*")) ? getResources().getString(R.string.WizardCreditCard_CardHolderNameLettersOnly) + "\n" : "";

        result += (cardNumber.trim().isEmpty()) ? getResources().getString(R.string.WizardCreditCard_CardNumberEmpty) + "\n" : "";
        result += (!cardNumber.matches("^[0-9]*$")) ? getResources().getString(R.string.WizardCreditCard_CardNumberNumbersOnly) + "\n" : "";
        result += (cardNumber.trim().length() != 16) ? getResources().getString(R.string.WizardCreditCard_CardNumberInvalid) : "";

        result += (cvv2.trim().isEmpty()) ? getResources().getString(R.string.WizardCreditCard_CVVEmpty) + "\n" : "";
        result += (!cvv2.matches("^[0-9]*$")) ? getResources().getString(R.string.WizardCreditCard_CVVNumbersOnly) + "\n" : "";
        return result;
    }
    private void openpayOnError(int openpayError) {
        String msg = "";
        switch (openpayError) {
            //region E1000
            case 1000:
                msg = getResources().getString(R.string.Openpay1000);
                break;
            case 1001:
                msg = getResources().getString(R.string.Openpay1001);
                break;
            case 1002:
                msg = getResources().getString(R.string.Openpay1002);
                break;
            case 1003:
                msg = getResources().getString(R.string.Openpay1003);
                break;
            case 1004:
                msg = getResources().getString(R.string.Openpay1004);
                break;
            case 1005:
                msg = getResources().getString(R.string.Openpay1005);
                break;
            case 1006:
                msg = getResources().getString(R.string.Openpay1006);
                break;
            case 1007:
                msg = getResources().getString(R.string.Openpay1007);
                break;
            case 1008:
                msg = getResources().getString(R.string.Openpay1008);
                break;
            case 1009:
                msg = getResources().getString(R.string.Openpay1009);
                break;
            case 1010:
                msg = getResources().getString(R.string.Openpay1010);
                break;
            //endregion
            //region E2000
            case 2001:
                msg = getResources().getString(R.string.Openpay2001);
                break;
            case 2002:
                msg = getResources().getString(R.string.Openpay2002);
                break;
            case 2003:
                msg = getResources().getString(R.string.Openpay2003);
                break;
            case 2004:
                msg = getResources().getString(R.string.Openpay2004);
                break;
            case 2005:
                msg = getResources().getString(R.string.Openpay2005);
                break;
            case 2006:
                msg = getResources().getString(R.string.Openpay2006);
                break;
            case 2007:
                msg = getResources().getString(R.string.Openpay2007);
                break;
            case 2008:
                msg = getResources().getString(R.string.Openpay2008);
                break;
            case 2009:
                msg = getResources().getString(R.string.Openpay2009);
                break;
            //endregion
            //region E3000
            case 3001:
                msg = getResources().getString(R.string.Openpay3001);
                break;
            case 3002:
                msg = getResources().getString(R.string.Openpay3002);
                break;
            case 3003:
                msg = getResources().getString(R.string.Openpay3003);
                break;
            case 3004:
                msg = getResources().getString(R.string.Openpay3004);
                break;
            case 3005:
                msg = getResources().getString(R.string.Openpay3005);
                break;
            case 3006:
                msg = getResources().getString(R.string.Openpay3006);
                break;
            case 3008:
                msg = getResources().getString(R.string.Openpay3008);
                break;
            case 3009:
                msg = getResources().getString(R.string.Openpay3009);
                break;
            case 3010:
                msg = getResources().getString(R.string.Openpay3010);
                break;
            case 3011:
                msg = getResources().getString(R.string.Openpay3011);
                break;
            case 3012:
                msg = getResources().getString(R.string.Openpay3012);
                break;
            //endregion
            //region E4000
            case 4001:
                msg = getResources().getString(R.string.Openpay4001);
                break;
            //endregion
        }
        obj.CloseLoadingScreen();
        Common.DialogStatusAlert(Wizard_CreditCard.this, msg, "Ocurrió un error al momento de procesar su tarjeta" , "Error");
    }
    //endregion

    private void LogoffUser(Common obj) {
        int UID = Integer.parseInt(obj.GetSharedPreferencesValue(Wizard_CreditCard.this, "UID"));
        String sessionToken = obj.GetSharedPreferencesValue(Wizard_CreditCard.this, "SessionToken");
        _svcConnection.LogOffUser(UID);
        Common.DeleteAllSharedPreferences(Wizard_CreditCard.this);
        final Intent intent = new Intent(Wizard_CreditCard.this, Wizard_Login.class);
        AlertDialog.Builder dialog = new AlertDialog.Builder(Wizard_CreditCard.this);
        dialog.setTitle("Tu sesión ha expirado");
        dialog.setMessage("Cada sesión esta programada para expirar cada 7 días desde la ultima vez que abres el app o cuando se inicia desde otro dispositivo, si no es tu caso, es recomendable cambiar tu contraseña inmediatamente");
        dialog.setIcon(R.drawable.ic_logoff);
        dialog.setCancelable(false);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(intent);
            }
        });
        obj.CloseLoadingScreen();
        dialog.show();
    }

}
