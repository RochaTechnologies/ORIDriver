package com.rochatech.oridriver;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.rochatech.library.Common;
import com.rochatech.webService.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Settings_DepositAccount extends AppCompatActivity {

    connectToService _svcConnection;
    Common obj;
    EditText ActHolderName, ActCLABE, ActNickName;
    Button ActionBtnPressed;
    LinearLayoutCompat DepositLinear;
    String UID = "";
    String GID = "";
    String bankActId = "";
    String GCID = "";
    Integer _UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_depositaccount_activity);
        obj = new Common(Settings_DepositAccount.this);
        _svcConnection = new connectToService(Settings_DepositAccount.this, obj.GetSharedPreferencesValue(Settings_DepositAccount.this, "SessionToken"));
        UID = obj.GetSharedPreferencesValue(Settings_DepositAccount.this, "UID");
        _UID = Integer.parseInt(UID);
        GID = obj.GetSharedPreferencesValue(Settings_DepositAccount.this, "GID");
        InitAppControls();
        LoadUserPaymentInfo(UID, "1");
        ActionBtnPressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String actionbtntext = ActionBtnPressed.getText().toString();
                switch (actionbtntext) {
                    case "Guardar información":
                        obj.ShowLoadingScreen(Settings_DepositAccount.this,"Realizando cambios, por favor espere...");
                        String holder = ActHolderName.getText().toString();
                        String alias = ActNickName.getText().toString();
                        String CLABE = ActCLABE.getText().toString();
                        String Errors = AnyErrors(holder, alias, CLABE);
                        if(Errors.trim().isEmpty()) {
                            SaveActInfo(CLABE, alias, holder);
                        } else {
                            obj.CloseLoadingScreen();
                            Common.DialogStatusAlert(Settings_DepositAccount.this, Errors, getResources().getString(R.string.ORIGlobal_AnyErrorsTitle),"Error");
                        }
                        break;
                    case "Eliminar información":
                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(Settings_DepositAccount.this);
                        LayoutInflater inflater = getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.template_dialogstatusalert, null);
                        TextView dialogTitle = dialogView.findViewById(R.id.alertDialogTitle);
                        TextView dialogMsg = dialogView.findViewById(R.id.alertDialogMsg);
                        ImageView dialogIcon = dialogView.findViewById(R.id.alertDialogIcon);
                        dialogTitle.setText("¿Desea eliminar este cuenta de depósitos?");
                        dialogMsg.setVisibility(View.GONE);
                        dialogIcon.setImageResource(R.drawable.ic_error);
                        dialog.setView(dialogView);
                        dialog.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                obj.ShowLoadingScreen(Settings_DepositAccount.this,"Realizando cambios, por favor espere...");
                                DeleteActInfo(Integer.parseInt(UID), Integer.parseInt(bankActId), GCID);
                            }
                        });
                        dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        dialog.show();
                        break;
                }
            }
        });

        //region Hide softkeyboard
        ActHolderName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Common.HideKeyboard(v, Settings_DepositAccount.this);
            }
        });
        ActCLABE.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Common.HideKeyboard(v, Settings_DepositAccount.this);
            }
        });
        ActNickName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Common.HideKeyboard(v, Settings_DepositAccount.this);
            }
        });
        //endregion
    }


    //region WebService Call
    private void LoadUserPaymentInfo(String UID, String IsForDeposit) {
        obj.ShowLoadingScreen(Settings_DepositAccount.this,"Estamos cargando su información");
        _svcConnection.GetCardOrAcntForService(Integer.parseInt(UID), 0, IsForDeposit, new WSResponseListener() {
            @Override
            public void onError(String message) {
                switch (message) {
                    case "Error_InvalidToken":
                        Common.LogoffByInvalidToken(Settings_DepositAccount.this);
                        break;
                    case "NO_CONNECTION":
                        obj.CloseLoadingScreen();
                        Common.DialogStatusAlert(Settings_DepositAccount.this,getResources().getString(R.string.ORI_NoInternetConnection_Msg),getResources().getString(R.string.ORI_NoInternetConnection_Title),"Error");
                        break;
                }
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                String holdername = "";
                String CLABE = "";
                String actnickname = "";
                String status = "";
                String IsForDeposit = "";
                try {
                    JSONObject response = jsonResponse.getJSONObject(0);
                    holdername = response.getString("HolderName");
                    actnickname = response.getString("AccountAlias");
                    IsForDeposit = response.getString("IsForDeposit");
                    CLABE = response.getString("Clabe");
                    bankActId = response.getString("Id");
                    GCID = response.getString("GatewayCardId");
                    status = "OK";
                } catch (JSONException e) {
                    status = "EMPTY";
                }
                SetUserPaymentInfo(status, IsForDeposit, holdername, actnickname, CLABE);
            }
        });
    }
    private void SetUserPaymentInfo(String status, String IsForDeposit, String holdername, String actnickname, String CLABE) {
        ActionBtnPressed.setEnabled(true);
            switch (status) {
                case "OK":
                    ActHolderName.setText(holdername);
                    ActHolderName.setEnabled(false);
                    ActCLABE.setText(CLABE);
                    ActCLABE.setEnabled(false);
                    ActNickName.setText(actnickname);
                    ActNickName.setEnabled(false);
                    ActionBtnPressed.setText("Eliminar información");
                    ActionBtnPressed.setBackground(getResources().getDrawable(R.drawable.template_redbutton));
                    obj.CloseLoadingScreen();
                    break;
                case "EMPTY":
                    ActionBtnPressed.setText("Guardar información");
                    ActionBtnPressed.setBackground(getResources().getDrawable(R.drawable.template_bluebutton));
                    obj.CloseLoadingScreen();
                    break;
            }
    }
    private void SaveActInfo(String CLABE, String alias, String holder) {
        _svcConnection.AssignAccountToCostumerInGateway(Integer.parseInt(UID), CLABE, alias, holder, GID, new WSResponseListener() {
            @Override
            public void onError(String message) {
                switch (message) {
                    case "Error_InvalidToken":
                        Common.LogoffByInvalidToken(Settings_DepositAccount.this);
                        break;
                    case "NO_CONNECTION":
                        obj.CloseLoadingScreen();
                        Common.DialogStatusAlert(Settings_DepositAccount.this,getResources().getString(R.string.ORI_NoInternetConnection_Msg),getResources().getString(R.string.ORI_NoInternetConnection_Title),"Error");
                        break;
                }
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                try {
                    JSONObject response = jsonResponse.getJSONObject(0);
                    String status = response.getString("Status");
                    switch (status) {
                        case "OK":
                            try {
                                obj.CloseLoadingScreen();
                                Snackbar snackbar = Snackbar.make(DepositLinear,"¡Cuenta de depósito agregada!",Snackbar.LENGTH_SHORT);
                                snackbar.show();
                                LoadUserPaymentInfo(UID,"1");
                            } catch (Exception e) {
                                obj.CloseLoadingScreen();
                                Common.DialogStatusAlert(Settings_DepositAccount.this, e.toString(),getResources().getString(R.string.ORIGlobal_webServiceError),"Error");
                            }
                            break;
                    }
                    obj.CloseLoadingScreen();
                } catch (JSONException e) {
                    obj.CloseLoadingScreen();
                    Common.DialogStatusAlert(Settings_DepositAccount.this, e.toString(), getResources().getString(R.string.ORIGlobal_webServiceError),"Error");
                }
            }
        });
    }
    private void DeleteActInfo(int UID, int bankActId, String GCID) {
        _svcConnection.RemoveAccountFromService(UID, bankActId, GCID, new WSResponseListener() {
            @Override
            public void onError(String message) {
                switch (message) {
                    case "Error_InvalidToken":
                        Common.LogoffByInvalidToken(Settings_DepositAccount.this);
                        break;
                    case "NO_CONNECTION":
                        obj.CloseLoadingScreen();
                        Common.DialogStatusAlert(Settings_DepositAccount.this,getResources().getString(R.string.ORI_NoInternetConnection_Msg),getResources().getString(R.string.ORI_NoInternetConnection_Title),"Error");
                        break;
                }
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                ActHolderName.setText("");
                ActCLABE.setText("");
                ActNickName.setText("");
                ActionBtnPressed.setText("Guardar información");
                ActionBtnPressed.setBackground(getResources().getDrawable(R.drawable.template_bluebutton));
                obj.CloseLoadingScreen();
                Snackbar snackbar = Snackbar.make(DepositLinear,"Cuenta eliminada",Snackbar.LENGTH_SHORT);
                snackbar.show();

                obj.CloseLoadingScreen();
            }
        });
    }
    //endregion


    //region MISC
    private void InitAppControls() {
        ActHolderName = findViewById(R.id.AccountHolderName);
        ActCLABE = findViewById(R.id.AccountCLABE);
        ActNickName = findViewById(R.id.AccountNickName);
        ActionBtnPressed = findViewById(R.id.UpdateDepositInfo);
        DepositLinear = findViewById(R.id.DepositActLinear);
    }
    private String AnyErrors(String holder, String alias, String clabe) {
        String result = "";
        result += (holder.trim().isEmpty()) ? getResources().getString(R.string.WizardCreditCard_CardHolderNameEmpty) + "\n" : "";
        result += (holder.trim().matches(".*\\d+.*")) ? getResources().getString(R.string.WizardCreditCard_CardHolderNameLettersOnly) + "\n" : "";
        result += (!holder.trim().matches("^[ A-Za-zéáíóúñÑüÁÉÍÓÚÜ]+$")) ? getResources().getString(R.string.WizardCreditCard_CardHolderNameLettersOnly) + "\n" : "";
        result += (alias.trim().isEmpty()) ? getResources().getString(R.string.WizardCreditCard_CardNickNameEmpty) + "\n" : "";
        result += (clabe.trim().isEmpty()) ? getResources().getString(R.string.WizardCreditCard_CLABEEmpty) + "\n" : "";
        result += (clabe.trim().length() != 18) ? getResources().getString(R.string.WizardCreditCard_CLABEInvalid) + "\n" : "";
        return result;
    }
    //endregion
}
