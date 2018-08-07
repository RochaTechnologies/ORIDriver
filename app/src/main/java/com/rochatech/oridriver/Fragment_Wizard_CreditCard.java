package com.rochatech.oridriver;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import com.rochatech.library.Common;
import com.rochatech.webService.*;
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


public class Fragment_Wizard_CreditCard extends Fragment {

    connectToService _svcConnection;
    Common obj;
    Context context;
    View view;
    EditText txtHolderName, txtCardNumber, txtCvv;
    Spinner SprMonth, SprYear;
    Button btnSaveCardPressed, btnCvvDialogPressed;

    public Fragment_Wizard_CreditCard() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_wizard_creditcard, container, false);
        obj = new Common(context);
        _svcConnection = new connectToService(context, obj.GetSharedPreferencesValue(context, "SessionToken"));
        InitFragControls();
        LoadMonthNYear();
        btnCvvDialogPressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT);
                LayoutInflater inflater = (LayoutInflater) getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.template_dialogcvv,null);
                dialog.setView(dialogView);
                dialog.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
            }
        });
        btnSaveCardPressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obj.ShowLoadingScreen(context,"Guardando los datos de la tarjeta");
                CreateToken();
            }
        });

        //region Hide keyboard
        txtHolderName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Common.HideKeyboard(v, context);
            }
        });
        txtCardNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Common.HideKeyboard(v, context);
            }
        });
        txtCvv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Common.HideKeyboard(v, context);
            }
        });
        //endregion

        return view;
    }

    //region WebService Call
    private void CreateToken() {
        String holderName = txtHolderName.getText().toString();
        String cardNumber = txtCardNumber.getText().toString();
        int expMonth = Integer.parseInt(SprMonth.getSelectedItem().toString());
        int expYear = Integer.parseInt(SprYear.getSelectedItem().toString());
        String cvv2 = txtCvv.getText().toString();
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
                Common.DialogStatusAlert(context, e.toString(), "Ocurrió un error al momento de iniciar el proceso para guardar su tarjeta", "Error");
            }
        } else {
            obj.CloseLoadingScreen();
            Common.DialogStatusAlert(context, Errors, getResources().getString(R.string.ORIGlobal_AnyErrorsTitle), "Error");
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
                Common.DialogStatusAlert(context, e.toString(), "Ocurrió un error de comunicación al momento de procesar su tarjeta", "Error");
            }

            @Override
            public void onSuccess(OperationResult<Token> operationResult) {
                String UID = obj.GetSharedPreferencesValue(context, "UID");
                String GTID = operationResult.getResult().getId();
                String GCID = obj.GetSharedPreferencesValue(context, "GID");
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
                switch (message) {
                    case "Error_InvalidToken":
                        Common.LogoffByInvalidToken(context);
                        break;
                    case "NO_CONNECTION":
                        Common.DialogStatusAlert(context,getResources().getString(R.string.ORI_NoInternetConnection_Msg),getResources().getString(R.string.ORI_NoInternetConnection_Title),"Error");
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
                                /*Actualizar forma disponible de pago y pago preferido*/
                                SharedPreferences pref = context.getSharedPreferences(getResources().getString(R.string.ORIGlobal_SharedPreferences), Context.MODE_PRIVATE);
                                SharedPreferences.Editor edit = pref.edit();
                                edit.putString("settings_AvailablePayment", "3");
                                edit.putString("settings_Payment", "3");
                                edit.apply();
                                /*Actualizar pago preferido en Unity*/
//                                UpdatePreferredPaymentType(UID, "3");
//                                Intent intent = new Intent(context, Wizard_Login.class);
//                                startActivity(intent);
                                Bundle bundle = new Bundle();
                                bundle.putString("DisplayOpt","CreditCardAssigned");
                                Fragment_Wizard_NotificationAlert fragment = new Fragment_Wizard_NotificationAlert();
                                fragment.setArguments(bundle);
                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                transaction.setCustomAnimations(R.animator.anim_slide_inright, R.animator.anim_slide_outleft);
                                obj.CloseLoadingScreen();
                                transaction.replace(R.id.dialogframecontainer,fragment).commit();
                            } catch (Exception e) {
                                obj.CloseLoadingScreen();
                                Common.DialogStatusAlert(context, e.toString(),getResources().getString(R.string.ORIGlobal_webServiceError), "Error");
                            }
                            break;
                    }
                } catch (JSONException e) {
                    obj.CloseLoadingScreen();
                    Common.DialogStatusAlert(context, e.toString(),getResources().getString(R.string.ORIGlobal_webServiceError), "Error");
                }
            }
        });
    }
    //endregion

    //region MISC
    private void InitFragControls() {
        txtHolderName = view.findViewById(R.id.txtHolderName);
        txtCardNumber = view.findViewById(R.id.txtCardNumber);
        txtCvv = view.findViewById(R.id.txtCvv);
        SprMonth = view.findViewById(R.id.SprMonth);
        SprYear = view.findViewById(R.id.SprYear);
        btnCvvDialogPressed = view.findViewById(R.id.btnCvvDialogPressed);
        btnSaveCardPressed = view.findViewById(R.id.btnSaveCardPressed);
    }
    private void LoadMonthNYear() {
        ArrayList<Integer> months = new ArrayList<>();
        for (int j = 1; j <= 12; j++) {
            months.add(j);
        }
        ArrayAdapter<Integer> monthAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        SprMonth.setAdapter(monthAdapter);
        ArrayList<Integer> availableYears = new ArrayList<>();
        int currentYear = Integer.parseInt(Common.getDateComp("YearLastDigits", Common.getNow()));
        for (int i = 0; i <= 10; i++) {
            availableYears.add(currentYear);
            currentYear++;
        }
        ArrayAdapter<Integer> yearAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, availableYears);
        yearAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        SprYear.setAdapter(yearAdapter);
    }
    private String AnyErrors(String holderName, String cardNumber, String cvv2) {
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
        Common.DialogStatusAlert(context, msg, "Ocurrió un error al momento de procesar su tarjeta","Error");
    }
    //endregion

}
