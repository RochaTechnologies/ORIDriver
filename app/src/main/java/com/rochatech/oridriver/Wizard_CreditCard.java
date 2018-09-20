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
import android.view.LayoutInflater;
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

    //region Global
    EditText txtHolderName, txtCardNumber, txtCvv;
    Button CVVInfo, SaveCreditCard;
    Spinner SprMonth, SprYear;
    connectToService _svcConnection;
    Common obj;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wizard_creditcard_activity);
        obj = new Common(Wizard_CreditCard.this);
        _svcConnection = new connectToService(Wizard_CreditCard.this, obj.GetSharedPreferencesValue(Wizard_CreditCard.this, "SessionToken"));
        obj.ShowLoadingScreen(Wizard_CreditCard.this,"Por favor espere...");
        InitControls();
        LoadSpinners();
        CVVInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CVVDialogInfo();
            }
        });
        SaveCreditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obj.ShowLoadingScreen(Wizard_CreditCard.this,"Agregando tarjeta...");
                CreateToken();
            }
        });

        //region Hide keyboard
        txtHolderName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Common.HideKeyboard(v, Wizard_CreditCard.this);
            }
        });
        txtCardNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Common.HideKeyboard(v, Wizard_CreditCard.this);
            }
        });
        txtCvv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Common.HideKeyboard(v, Wizard_CreditCard.this);
            }
        });
        //endregion
    }


    //region WebService Call
    private void CreateToken() {
//        String Errors = CheckForErrors(_userCardName.getText().toString(), _userCardNumber.getText().toString(), _cardMonth.getText().toString(), _cardYear.getText().toString(), _cardCVV.getText().toString());
        String holderName = txtHolderName.getText().toString();
        String cardNumber = txtCardNumber.getText().toString();
        int expMonth = Integer.parseInt(SprMonth.getSelectedItem().toString());
        int expYear = Integer.parseInt(SprYear.getSelectedItem().toString());
        final String cvv2 = txtCvv.getText().toString();
        String Errors = AnyErrors(holderName,cardNumber,cvv2);
        if (Errors.trim().isEmpty()) {
            Card paymentCard = new Card();
            paymentCard.holderName(holderName);
            paymentCard.cardNumber(cardNumber);
            paymentCard.expirationMonth(expMonth);
            paymentCard.expirationYear(expYear);
            paymentCard.cvv2(cvv2);
            Openpay openpay = obj.getOpenPay();
            final String deviceSessionId = openpay.getDeviceCollectorDefaultImpl().setup(Wizard_CreditCard.this);
            openpay.createToken(paymentCard, new OperationCallBack<Token>() {
                @Override
                public void onError(OpenpayServiceException e) {
                    int openpayError = e.getErrorCode();
                    openpayOnError(openpayError);
                }

                @Override
                public void onCommunicationError(ServiceUnavailableException e) {
                    obj.CloseLoadingScreen();
                    Common.DialogStatusAlert(Wizard_CreditCard.this, e.toString(), "Ocurrió un error de comunicación al momento de procesar su tarjeta","Error");
                }

                @Override
                public void onSuccess(OperationResult<Token> operationResult) {
                    int UID = Integer.parseInt(obj.GetSharedPreferencesValue(Wizard_CreditCard.this,"UID"));
                    String GTID = operationResult.getResult().getId();
                    String GCID = obj.GetSharedPreferencesValue(Wizard_CreditCard.this,"GID");
                    String secCode = cvv2 + GCID;
                    String SecurityCode = Common.SHA256(secCode);
                    AssignCardToCostumer(UID, GTID, GCID, deviceSessionId, SecurityCode);
                }
            });
        } else {
            obj.CloseLoadingScreen();
            Common.DialogStatusAlert(Wizard_CreditCard.this, Errors,getResources().getString(R.string.ORIGlobal_AnyErrorsTitle),"Error");
        }
    }
    private void AssignCardToCostumer(final int UID, String GTID, String GCID, String deviceSession, String securityCode) {
        _svcConnection.AssignCardToCustomerInGateway(UID, GTID, GCID, deviceSession, securityCode, new WSResponseListener() {
            @Override
            public void onError(String message) {
                if (message.contains("NO_CONNECTION")) {
                    obj.CloseLoadingScreen();
                    Common.DialogStatusAlert(Wizard_CreditCard.this,getResources().getString(R.string.ORI_NoInternetConnection_Msg),getResources().getString(R.string.ORI_NoInternetConnection_Title),"Error");
                } else {
                    obj.CloseLoadingScreen();
                    Common.DialogStatusAlert(Wizard_CreditCard.this,message,getResources().getString(R.string.ORIGlobal_webServiceError),"Error");
                }
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                try {
                    JSONObject msgFromJsonResponse = jsonResponse.getJSONObject(0);
                    String status = msgFromJsonResponse.getString("Status");
                    String errorDesc = msgFromJsonResponse.getString("ErroDesc");
                    switch (status) {
                        case "OK":
                            //Actualizar forma disponible de pago y pago preferido en Shared
                            SharedPreferences pref = getApplicationContext().getSharedPreferences(getResources().getString(R.string.ORIGlobal_SharedPreferences), Context.MODE_PRIVATE);
                            SharedPreferences.Editor edit = pref.edit();
                            edit.putString("settings_AvailablePayment", "3");
                            edit.putString("settings_Payment", "3");
                            edit.apply();
                            UpdatePreferredPaymentType(UID);
                            break;
                        default:
                            obj.CloseLoadingScreen();
                            Common.DialogStatusAlert(Wizard_CreditCard.this,status,getResources().getString(R.string.ORIGlobal_webServiceError),"Error");
                            break;
                        case "Error":
                            obj.CloseLoadingScreen();
                            Common.DialogStatusAlert(Wizard_CreditCard.this,errorDesc,getResources().getString(R.string.ORIGlobal_webServiceError),getResources().getString(R.string.ORIDialog_Error_IconName));
                            break;
                    }
                } catch (JSONException e) {
                    obj.CloseLoadingScreen();
                    Common.DialogStatusAlert(Wizard_CreditCard.this,e.toString(),getResources().getString(R.string.ORIGlobal_webServiceError),"Error");
                }
            }
        });
    }
    private void UpdatePreferredPaymentType(int UID) {
        _svcConnection.UpdatePreferredPaymentTypeId(UID, "3", new WSResponseListener() {
            @Override
            public void onError(String message) {
                switch (message) {
                    case "Error_InvalidToken":
                        obj.CloseLoadingScreen();
                        LogoffUser(obj);
                        break;
                    case "NO_CONNECTION":
                        obj.CloseLoadingScreen();
                        Common.DialogStatusAlert(Wizard_CreditCard.this,getResources().getString(R.string.ORI_NoInternetConnection_Msg),getResources().getString(R.string.ORI_NoInternetConnection_Title),"Error");
                        break;
                }
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                Intent intent = new Intent(Wizard_CreditCard.this, Settings_BankAccount.class);
                intent.putExtra("resultMsg","¡Tarjeta agregada!");
                startActivity(intent);
                finish();
            }
        });
    }
    //endregion


    //region MISC
    private void InitControls() {
        txtHolderName = findViewById(R.id.txtCardName);
        txtCardNumber = findViewById(R.id.txtCardNumber);
        SprMonth = findViewById(R.id.AvailableMonths);
        SprYear = findViewById(R.id.AvailableYears);
        txtCvv = findViewById(R.id.txtCVV);
        CVVInfo = findViewById(R.id.btnCVV);
        SaveCreditCard = findViewById(R.id.btnSaveCreditCard);
    }
    private void LoadSpinners() {
        ArrayList<Integer> months = new ArrayList<>();
        for (int j = 1; j <= 12; j++) {
            months.add(j);
        }
        ArrayAdapter<Integer> monthAdapter = new ArrayAdapter<>(Wizard_CreditCard.this, android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        SprMonth.setAdapter(monthAdapter);
        ArrayList<Integer> availableYears = new ArrayList<>();
        int currentYear = Integer.parseInt(Common.getDateComp("YearLastDigits", Common.getNow()));
        for (int i = 0; i <= 10; i++) {
            availableYears.add(currentYear);
            currentYear++;
        }
        ArrayAdapter<Integer> yearAdapter = new ArrayAdapter<>(Wizard_CreditCard.this, android.R.layout.simple_spinner_item, availableYears);
        yearAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        SprYear.setAdapter(yearAdapter);
        obj.CloseLoadingScreen();
    }
    private void CVVDialogInfo() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(Wizard_CreditCard.this,AlertDialog.THEME_HOLO_LIGHT);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.template_dialogcvv,null);
        dialog.setView(dialogView);
        dialog.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    }
    private String AnyErrors(String holderName, String cardNumber, String cvv2){
        String result = "";
        result += (holderName.trim().isEmpty()) ? getResources().getString(R.string.WizardCreditCard_CardHolderNameEmpty) + "\n" : "";
        result += (!holderName.matches("^[ A-Za-z]+$")) ? getResources().getString(R.string.WizardCreditCard_CardHolderNameLettersOnly) + "\n" : "";
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
        Common.DialogStatusAlert(Wizard_CreditCard.this, msg, "Ocurrió un error al momento de procesar su tarjeta","Error");
    }
    private void LogoffUser(Common obj) {
        int UID = Integer.parseInt(obj.GetSharedPreferencesValue(Wizard_CreditCard.this, "UID"));
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
    //endregion

}
