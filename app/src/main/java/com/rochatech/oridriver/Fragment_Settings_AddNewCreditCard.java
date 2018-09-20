package com.rochatech.oridriver;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.rochatech.library.Common;
import com.rochatech.webService.WSResponseListener;
import com.rochatech.webService.connectToService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import mx.openpay.android.Openpay;
import mx.openpay.android.OperationCallBack;
import mx.openpay.android.OperationResult;
import mx.openpay.android.exceptions.OpenpayServiceException;
import mx.openpay.android.exceptions.ServiceUnavailableException;
import mx.openpay.android.model.Card;
import mx.openpay.android.model.Token;

@SuppressWarnings({"WeakerAccess","unused","FieldCanBeLocal"})
public class Fragment_Settings_AddNewCreditCard extends Fragment {

    Context context;
    connectToService _svcConnection;
    Common obj;
    Spinner SprNewMonth, SprNewYear;
    TextView newCard_HolderName, newCard_CardNumber, newCard_CVV2;
    Button cvvDialogPressed, saveNewCardPressed;
    String unity;

    public Fragment_Settings_AddNewCreditCard() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings_addnewcreditcard, container, false);
        obj = new Common(context);
        _svcConnection = new connectToService(context, obj.GetSharedPreferencesValue(context, "SessionToken"));
        unity = obj.GetSharedPreferencesValue(context, "UID");
        newCard_HolderName = view.findViewById(R.id.newCard_HolderName);
        newCard_CardNumber = view.findViewById(R.id.newCard_CardNumber);
        SprNewMonth = view.findViewById(R.id.SprNewCardMonth);
        SprNewYear = view.findViewById(R.id.SprNewCardYear);
        newCard_CVV2 = view.findViewById(R.id.newCard_CVV2);
        cvvDialogPressed = view.findViewById(R.id.btnNewCvvDialogPressed);
        saveNewCardPressed = view.findViewById(R.id.btnSaveNewCardPressed);
        saveNewCardPressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obj.ShowLoadingScreen(context,getResources().getString(R.string.WizardCreditCard_SavingCard));
                CreateToken();
            }
        });
        cvvDialogPressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT);
                View dialogView = LayoutInflater.from(context).inflate(R.layout.template_dialogcvv,null);
                dialog.setView(dialogView);
                dialog.setPositiveButton(getResources().getString(R.string.ORIUnderstoodString), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
            }
        });
        //region Hide keyboard
        newCard_HolderName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Common.HideKeyboard(v, context);
            }
        });
        newCard_CardNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Common.HideKeyboard(v, context);
            }
        });
        newCard_CVV2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Common.HideKeyboard(v, context);
            }
        });
        //endregion
        LoadMonthNYear();
        return view;
    }

    //region WebServiceCall
    private void CreateToken() {
        String holderName = newCard_HolderName.getText().toString();
        String cardNumber = newCard_CardNumber.getText().toString();
        int expMonth = Integer.parseInt(SprNewMonth.getSelectedItem().toString());
        int expYear = Integer.parseInt(SprNewYear.getSelectedItem().toString());
        String cvv2 = newCard_CVV2.getText().toString();
        String Errors = AnyErrors(holderName, cardNumber, cvv2);
        if (!Errors.trim().isEmpty()) {
            try {
                Card paymentCard = new Card();
                paymentCard.holderName(holderName);
                paymentCard.cardNumber(cardNumber);
                paymentCard.expirationMonth(expMonth);
                paymentCard.expirationYear(expYear);
                paymentCard.cvv2(cvv2);
                Openpay openpay = obj.getOpenPay();
                String deviceSessionId = openpay.getDeviceCollectorDefaultImpl().setup(getActivity());
                StartCreatingToken(openpay, paymentCard, deviceSessionId, cvv2);
            } catch (Exception e) {
                obj.CloseLoadingScreen();
                Common.DialogStatusAlert(context, e.toString(), getResources().getString(R.string.WizardCreditCard_StartSavingCardError), getResources().getString(R.string.ORIDialog_Error_IconName));
            }
        } else {
            obj.CloseLoadingScreen();
            Common.DialogStatusAlert(context, Errors, getResources().getString(R.string.ORIGlobal_AnyErrorsTitle), getResources().getString(R.string.ORIDialog_Error_IconName));
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
                Common.DialogStatusAlert(context, e.toString(), getResources().getString(R.string.Openpay_ComError), getResources().getString(R.string.ORIDialog_Error_IconName));
            }

            @Override
            public void onSuccess(OperationResult<Token> operationResult) {
                String GTID = operationResult.getResult().getId();
                String GCID = obj.GetSharedPreferencesValue(context, "GID");
                String securityCode = Common.SHA256(cvv2 + GCID);
                AssignCardToCostumer(unity, GTID, GCID, deviceSessionId, securityCode);
            }
        });
    }
    private void AssignCardToCostumer(String UID, String GTID, String GCID, String deviceSessionId, String securityCode) {
        _svcConnection.AssignCardToCustomerInGateway(Integer.parseInt(UID), GTID, GCID, deviceSessionId, securityCode, new WSResponseListener() {
            @Override
            public void onError(String message) {
                obj.CloseLoadingScreen();
                switch (message) {
                    case "Error_InvalidToken":
                        Common.LogoffByInvalidToken(context);
                        break;
                    case "NO_CONNECTION":
                        Common.DialogStatusAlert(context,getResources().getString(R.string.ORI_NoInternetConnection_Msg),getResources().getString(R.string.ORI_NoInternetConnection_Title), getResources().getString(R.string.ORIDialog_Error_IconName));
                        break;
                }
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                try {
                    JSONObject response = jsonResponse.getJSONObject(0);
                    String status = response.getString("Status");
                    String errorDesc = response.getString("ErroDesc");
                    switch (status) {
                        case "OK":
                            try {
                                /*Actualizar forma disponible de pago y pago preferido*/
                                SharedPreferences pref = context.getSharedPreferences(getResources().getString(R.string.ORIGlobal_SharedPreferences), Context.MODE_PRIVATE);
                                SharedPreferences.Editor edit = pref.edit();
                                edit.putString("settings_AvailablePayment", "3").apply();
                                edit.putString("settings_Payment", "3").apply();
                                edit.apply();
                                UpdatePreferredPaymentType(Integer.parseInt(unity));
                            } catch (Exception e) {
                                obj.CloseLoadingScreen();
                                Common.DialogStatusAlert(context, e.toString(),getResources().getString(R.string.ORIGlobal_webServiceError), getResources().getString(R.string.ORIDialog_Error_IconName));
                            }
                            break;
                        case "Error":
                            obj.CloseLoadingScreen();
                            Common.DialogStatusAlert(context,errorDesc,getResources().getString(R.string.ORIGlobal_webServiceError),getResources().getString(R.string.ORIDialog_Error_IconName));
                            break;
                    }
                } catch (JSONException e) {
                    obj.CloseLoadingScreen();
                    Common.DialogStatusAlert(context, e.toString(),getResources().getString(R.string.ORIGlobal_webServiceError), getResources().getString(R.string.ORIDialog_Error_IconName));
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
                        Common.LogoffByInvalidToken(context);
                        break;
                    case "NO_CONNECTION":
                        obj.CloseLoadingScreen();
                        Common.DialogStatusAlert(context,getResources().getString(R.string.ORI_NoInternetConnection_Msg),getResources().getString(R.string.ORI_NoInternetConnection_Title),"Error");
                        break;
                }
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                if (getActivity() != null) {
                    ((Settings_BankAccount)getActivity()).savingIngo = new ProgressDialog(context);
                    ((Settings_BankAccount)getActivity()).savingIngo.setCancelable(false);
                    ((Settings_BankAccount)getActivity()).savingIngo.setMessage(context.getResources().getString(R.string.ORIWaitingMsg));
                    ((Settings_BankAccount)getActivity()).savingIngo.show();
                    ((Settings_BankAccount)getActivity()).LoadUserPaymentInfo(unity,"0");
                    ((Settings_BankAccount)getActivity()).CloseNewCardDialog();
                    obj.CloseLoadingScreen();

                }
            }
        });
    }
    //endregion


    //region MISC
    private void LoadMonthNYear() {
        ArrayList<Integer> months = new ArrayList<>();
        for (int j = 1; j <= 12; j++) {
            months.add(j);
        }
        ArrayAdapter<Integer> monthAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        SprNewMonth.setAdapter(monthAdapter);
        ArrayList<Integer> availableYears = new ArrayList<>();
        int currentYear = Integer.parseInt(Common.getDateComp("YearLastDigits", Common.getNow()));
        for (int i = 0; i <= 10; i++) {
            availableYears.add(currentYear);
            currentYear++;
        }
        ArrayAdapter<Integer> yearAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, availableYears);
        yearAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        SprNewYear.setAdapter(yearAdapter);
    }
    private String AnyErrors(String holderName, String cardNumber, String cvv2) {
        String result = "";
        result += (holderName.trim().isEmpty()) ? getResources().getString(R.string.WizardCreditCard_CardHolderNameEmpty) + "\n" : "";
        result += (holderName.matches(".*\\d+.*")) ? getResources().getString(R.string.WizardCreditCard_CardHolderNameLettersOnly) + "\n" : "";
        result += (holderName.matches("^[ A-Za-zéáíóúñÑüÁÉÍÓÚÜ]+$")) ? getResources().getString(R.string.WizardCreditCard_CardHolderNameLettersOnly) + "\n" : "";

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
        Common.DialogStatusAlert(context, msg, getResources().getString(R.string.Openpay_ExeCardError), getResources().getString(R.string.ORIDialog_Error_IconName));
    }
    //endregion
}
