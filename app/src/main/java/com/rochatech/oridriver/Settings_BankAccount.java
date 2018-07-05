package com.rochatech.oridriver;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.rochatech.library.Common;
import com.rochatech.webService.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class Settings_BankAccount extends AppCompatActivity {

    connectToService _svcConnection;
    Common obj;

    //region Global
    String _bankAcntId, _holderName, _cardType, _cardBrand, _cardNumber, _accountAlias, _clabe, _bank, _gatewayCardId, _serviceId;
    ImageView UserCreditCardImg;
    TextView CreditCardNumber, CreditCardHolder;
    Button CardButton;
    //endregion


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_bankaccount_activity);
        obj = new Common(Settings_BankAccount.this);
        _svcConnection = new connectToService(Settings_BankAccount.this, obj.GetSharedPreferencesValue(Settings_BankAccount.this, "SessionToken"));
        obj.ShowLoadingScreen(Settings_BankAccount.this,"Por favor espere...");
        InitAppControls();
        String resultMsg = getIntent().getStringExtra("resultMsg");
        if (resultMsg != null) {
            LinearLayoutCompat BankAccountLinear = findViewById(R.id.BankAccountLinear);
            Snackbar snackbar = Snackbar.make(BankAccountLinear,resultMsg,Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
        String IsForDeposit = "0";
        LoadUserPaymentInfo(obj.GetSharedPreferencesValue(Settings_BankAccount.this,"UID"), IsForDeposit);
        CardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btnText = CardButton.getText().toString();
                switch (btnText) {
                    case "Agregar cuenta":
                        Intent intent = new Intent(Settings_BankAccount.this, Wizard_CreditCard.class);
                        intent.putExtra("FromActivity", "SettingsBankAccount");
                        startActivity(intent);
                        break;
                    case "Eliminar cuenta":
                        AlertDialog.Builder dialog = new AlertDialog.Builder(Settings_BankAccount.this);
                        LayoutInflater inflater = getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.template_dialogstatusalert,null);
                        TextView dialogTitle = dialogView.findViewById(R.id.alertDialogTitle);
                        TextView dialogMsg = dialogView.findViewById(R.id.alertDialogMsg);
                        ImageView dialogIcon = dialogView.findViewById(R.id.alertDialogIcon);
                        dialogTitle.setText("Eliminar forma de pago");
                        dialogMsg.setText("¿Esta seguro que desea eliminar esta forma de pago?");
                        dialogIcon.setImageResource(R.drawable.ic_error);
                        dialog.setView(dialogView);
                        dialog.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                obj.ShowLoadingScreen(Settings_BankAccount.this,"Eliminando tarjeta");
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
    private void LoadUserPaymentInfo(String UID, String IsForDeposit) {
        _svcConnection.GetCardOrAcntForService(Integer.parseInt(UID), 0, IsForDeposit, new WSResponseListener() {
            @Override
            public void onError(String message) {
                switch (message) {
                    case "Error_InvalidToken":
                        Common.LogoffByInvalidToken(Settings_BankAccount.this);
                        break;
                }
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                String Status = "";
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
                CardButton.setText("Eliminar cuenta");
                CardButton.setBackground(getResources().getDrawable(R.drawable.template_darkpinkbutton));
                CreditCardHolder.setText(_holderName);
                //region Format card number
                List<String> strings = new ArrayList<String>();
                int index = 0;
                while (index < _cardNumber.length()) {
                    strings.add(_cardNumber.substring(index, Math.min(index + 4,_cardNumber.length())));
                    index += 4;
                }
                String newCardNumber = "";
                for (int i = 0; i < strings.size(); i++) {
                    newCardNumber += strings.get(i) + "-";
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
                break;
            case "EMPTY":
                obj.CloseLoadingScreen();
                CardButton.setText("Agregar cuenta");
                CardButton.setBackground(getResources().getDrawable(R.drawable.template_ligthgreenbutton));
                CreditCardNumber.setText("XXXX-XXXX-XXXX-XXXX");
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
                CardButton.setText("Agregar cuenta");
                CreditCardHolder.setText("Titular");
                CreditCardNumber.setText("XXXX-XXXX-XXXX-XXXX");
                //endregion

                LinearLayoutCompat BankAccountLinear = findViewById(R.id.BankAccountLinear);
                obj.CloseLoadingScreen();
                Snackbar snackbar = Snackbar.make(BankAccountLinear,"¡Tarjeta eliminada!",Snackbar.LENGTH_SHORT);
                snackbar.show();
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
    //endregion

}
