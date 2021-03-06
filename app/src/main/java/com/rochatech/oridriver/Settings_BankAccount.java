package com.rochatech.oridriver;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.rochatech.library.Common;
import com.rochatech.library.Support_BottomDialog_AddNewCreditCard;
import com.rochatech.webService.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
@SuppressWarnings({"WeakerAccess","unused","FieldCanBeLocal"})
public class Settings_BankAccount extends AppCompatActivity {

    connectToService _svcConnection;
    public Common obj;

    //region Global
    String _bankAcntId, _holderName, _cardType, _cardBrand, _cardNumber, _accountAlias, _clabe, _bank, _gatewayCardId, _serviceId;
    ImageView UserCreditCardImg;
    TextView CreditCardNumber, CreditCardHolder;
    Button CardButton;
    Support_BottomDialog_AddNewCreditCard newCreditCardDialog;
    private final String IS_FOR_DEPOSIT = "0";
    public ProgressDialog savingIngo;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_bankaccount_activity);
        obj = new Common(Settings_BankAccount.this);
        _svcConnection = new connectToService(Settings_BankAccount.this, obj.GetSharedPreferencesValue(Settings_BankAccount.this, "SessionToken"));
        obj.ShowLoadingScreen(Settings_BankAccount.this,getResources().getString(R.string.ORIWaitingMsg));
        InitAppControls();
        String resultMsg = getIntent().getStringExtra("resultMsg");
        if (resultMsg != null) {
            LinearLayoutCompat BankAccountLinear = findViewById(R.id.BankAccountLinear);
            Snackbar snackbar = Snackbar.make(BankAccountLinear,resultMsg,Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
        LoadUserPaymentInfo(obj.GetSharedPreferencesValue(Settings_BankAccount.this,"UID"), IS_FOR_DEPOSIT);
        CardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btnText = CardButton.getText().toString();
                switch (btnText) {
                    case "Agregar tarjeta":
                        newCreditCardDialog = new Support_BottomDialog_AddNewCreditCard();
                        newCreditCardDialog.show(getSupportFragmentManager(), "ori_addnewcreditcard");
//                        Intent intent = new Intent(Settings_BankAccount.this, Wizard_CreditCard.class);
//                        intent.putExtra("FromActivity", "SettingsBankAccount");
//                        startActivity(intent);
                        break;
                    case "Eliminar tarjeta":
                        AlertDialog.Builder dialog = new AlertDialog.Builder(Settings_BankAccount.this);
                        LinearLayoutCompat nullParent = findViewById(R.id.BankAccountLinear);
                        View dialogView = getLayoutInflater().inflate(R.layout.template_dialogstatusalert,nullParent,false);
                        TextView dialogTitle = dialogView.findViewById(R.id.alertDialogTitle);
                        TextView dialogMsg = dialogView.findViewById(R.id.alertDialogMsg);
                        ImageView dialogIcon = dialogView.findViewById(R.id.alertDialogIcon);
                        dialogTitle.setText(getResources().getString(R.string.WizardCreditCard_DeleteCard_Title));
                        dialogMsg.setText(getResources().getString(R.string.WizardCreditCard_DeleteCard_Msg));
                        dialogIcon.setImageResource(R.drawable.ic_error);
                        dialog.setView(dialogView);
                        dialog.setPositiveButton(getResources().getString(R.string.ORIEliminateString), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                obj.ShowLoadingScreen(Settings_BankAccount.this,getResources().getString(R.string.WizardCreditCard_DeletingCard));
                                int UID = Integer.parseInt(obj.GetSharedPreferencesValue(Settings_BankAccount.this, "UID"));
                                DeleteUserPaymentInfo(UID, Integer.parseInt(_bankAcntId), Integer.parseInt(_serviceId), _gatewayCardId);
                            }
                        });
                        dialog.show();
                        break;
                }
            }
        });
    }


    //region WebService Call
    public void LoadUserPaymentInfo(String UID, String IsForDeposit) {
        _svcConnection.GetCardOrAcntForService(Integer.parseInt(UID), 0, IsForDeposit, new WSResponseListener() {
            @Override
            public void onError(String message) {
                switch (message) {
                    case "Error_InvalidToken":
                        Common.LogoffByInvalidToken(Settings_BankAccount.this);
                        break;
                    case "NO_CONNECTION":
                        obj.CloseLoadingScreen();
                        Common.DialogStatusAlert(Settings_BankAccount.this,getResources().getString(R.string.ORI_NoInternetConnection_Msg),getResources().getString(R.string.ORI_NoInternetConnection_Title),"Error");
                        break;
                }
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                String Status;
                try {
                    JSONObject response = jsonResponse.getJSONObject(0);
                    _bankAcntId = response.getString("Id");
                    _holderName = response.getString("HolderName");
                    _cardType = response.getString("CardType");
                    _cardBrand = response.getString("CardBrand");
                    _cardNumber = response.getString("CardNumber");
                    _accountAlias = response.getString("AccountAlias");
                    _clabe = response.getString("Clabe");
                    _bank = response.getString("Bank");
                    _gatewayCardId = response.getString("GatewayCardId");
                    _serviceId = response.getString("ServiceId");
                    Status = "OK";
                } catch (JSONException e) {
                    Status = "EMPTY";
                }
                SetUserPaymentInfo(Status);
            }
        });
    }
    private void SetUserPaymentInfo(String Status) {
        CardButton.setEnabled(true);
        switch (Status) {
            case "OK":
                CardButton.setText(getResources().getString(R.string.WizardCreditCard_DeleteCard));
                CardButton.setBackground(getResources().getDrawable(R.drawable.template_redbutton));
                CreditCardHolder.setText(_holderName);
                //region Format card number
                List<String> strings = new ArrayList<>();
                int index = 0;
                while (index < _cardNumber.length()) {
                    strings.add(_cardNumber.substring(index, Math.min(index + 4,_cardNumber.length())));
                    index += 4;
                }
                StringBuilder newCardNumber = new StringBuilder();
                for (int i = 0; i < strings.size(); i++) {
                    newCardNumber.append(strings.get(i)).append("-");
//                    newCardNumber += strings.get(i) + "-";
                }
                _cardNumber = newCardNumber.substring(0, newCardNumber.length() - 1);
                //endregion
                CreditCardNumber.setText(_cardNumber);
                switch (_cardBrand) {
                    case "visa":
                        UserCreditCardImg.setImageResource(R.drawable.ic_visacard);
                        break;
                    case "mastercard":
                        UserCreditCardImg.setImageResource(R.drawable.ic_mastercard);
                        break;
                }
                obj.CloseLoadingScreen();
                if (savingIngo != null) {
                    savingIngo.dismiss();
                }
                break;
            case "EMPTY":
                obj.CloseLoadingScreen();
                CardButton.setText(getResources().getString(R.string.WizardCreditCard_AddCard));
                CardButton.setBackground(getResources().getDrawable(R.drawable.template_bluebutton));
                CreditCardNumber.setText(getResources().getString(R.string.WizardCreditCard_DefaultCardValue));
                break;
        }
    }
    private void DeleteUserPaymentInfo(int UnityId, int BankAccountId, int ServiceId, String GatewayCardId) {
        _svcConnection.RemoveCardService(UnityId, BankAccountId, ServiceId, GatewayCardId, new WSResponseListener() {
            @Override
            public void onError(String message) {
                switch (message) {
                    case "Error_InvalidToken":
                        Common.LogoffByInvalidToken(Settings_BankAccount.this);
                        break;
                    case "NO_CONNECTION":
                        obj.CloseLoadingScreen();
                        Common.DialogStatusAlert(Settings_BankAccount.this,getResources().getString(R.string.ORI_NoInternetConnection_Msg),getResources().getString(R.string.ORI_NoInternetConnection_Title),getResources().getString(R.string.ORIDialog_Error_IconName));
                        break;
                }
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                //region Clean
                _bankAcntId = "";
                _holderName = "";
                _cardType = "";
                _cardBrand = "";
                _cardNumber = "";
                _accountAlias = "";
                _clabe = "";
                _bank = "";
                _gatewayCardId = "";
                _serviceId = "";
                //endregion

                //Actualizar en shared preferences a solo efectivo
                SharedPreferences pref = getApplicationContext().getSharedPreferences(getResources().getString(R.string.ORIGlobal_SharedPreferences), Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = pref.edit();
                edit.putString("settings_AvailablePayment","1");
                edit.putString("settings_Payment", "1");
                edit.apply();


                //region Clean Card Img info
                CardButton.setText(getResources().getString(R.string.WizardCreditCard_AddCard));
                CardButton.setBackgroundResource(R.drawable.template_bluebutton);
                CreditCardHolder.setText(getResources().getString(R.string.WizardCreditCard_DefaultCardHolder));
                CreditCardNumber.setText(getResources().getString(R.string.WizardCreditCard_DefaultCardValue));
                //endregion

                LinearLayoutCompat BankAccountLinear = findViewById(R.id.BankAccountLinear);
                obj.CloseLoadingScreen();
                Snackbar snackbar = Snackbar.make(BankAccountLinear,getResources().getString(R.string.WizardCreditCard_DeletingCardSuccess),Snackbar.LENGTH_SHORT);
                snackbar.show();

                UpdatePreferredPaymentType();
            }
        });
    }
    //endregion

    //region MISC
    private void InitAppControls() {
        UserCreditCardImg = findViewById(R.id.UserCreditCardImg);
        CreditCardNumber = findViewById(R.id.CreditCardNumber);
        CreditCardHolder = findViewById(R.id.CreditCardHolder);
        CardButton = findViewById(R.id.CardButton);
    }
    private void UpdatePreferredPaymentType() {
        int UID = Integer.parseInt(obj.GetSharedPreferencesValue(Settings_BankAccount.this,"UID"));
        String paymentType = obj.GetSharedPreferencesValue(Settings_BankAccount.this,"settings_Payment");
        _svcConnection.UpdatePreferredPaymentTypeId(UID, paymentType, new WSResponseListener() {
            @Override
            public void onError(String message) {
                switch (message) {
                    case "Error_InvalidToken":
                        Common.LogoffByInvalidToken(Settings_BankAccount.this);
                        break;
                    case "NO_CONNECTION":
                        obj.CloseLoadingScreen();
                        Common.DialogStatusAlert(Settings_BankAccount.this,getResources().getString(R.string.ORI_NoInternetConnection_Msg),getResources().getString(R.string.ORI_NoInternetConnection_Title),getResources().getString(R.string.ORIDialog_Error_IconName));
                        break;
                }
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {

            }
        });
    }
    public void CloseNewCardDialog() {
        if (newCreditCardDialog != null) {
            newCreditCardDialog.dismiss();
        }
    }
    //endregion

}
