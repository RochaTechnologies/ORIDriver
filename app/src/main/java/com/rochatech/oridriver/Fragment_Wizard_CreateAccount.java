package com.rochatech.oridriver;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SwitchCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import com.rochatech.Model.AvailableCities;
import com.rochatech.library.Common;
import com.rochatech.webService.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;


public class Fragment_Wizard_CreateAccount extends Fragment {

    Context context;
    connectToService _svcConnection;
    Common obj;
    View view;
    Double _screenSize;
    DisplayMetrics dm;

    Spinner sprAvailableCities;
    EditText txtMobile, txtMail, txtPassword, txtConfirmPassword;
    Button btnTermsInfo, btnCreateAccountPressed;
    SwitchCompat swTermsAcceptedChecked;
    String CountryName = "";
    ArrayList<Integer> CID = new ArrayList<>();
    Bitmap profilePicture;

    public Fragment_Wizard_CreateAccount() {
        // Required empty public constructor
    }

    public void SetProfilePicture(Bitmap picture) {
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        profilePicture = picture;
//        profilePicture.compress(Bitmap.CompressFormat.PNG,100, outputStream);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_wizard_createaccount, container, false);
        obj = new Common(context);
        _svcConnection = new connectToService(context, obj.GetSharedPreferencesValue(context, "SessionToken"));
        obj.ShowLoadingScreen(context,"Cargando información...");
        InitFragControls();
        LoadAvailableCities();
        /*Terminos y condiciones*/
        btnTermsInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.template_dialogtermsinfo,null);
                dialog.setView(dialogView);
                dialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        swTermsAcceptedChecked.setChecked(true);
                    }
                });
                dialog.setCancelable(false);
                dialog.show();
            }
        });
        btnCreateAccountPressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obj.ShowLoadingScreen(context,"Creando cuenta");
                PreCreateAccount();
            }
        });

        //region Hide keyboard
        txtMobile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Common.HideKeyboard(v, context);
            }
        });
        txtMail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Common.HideKeyboard(v, context);
            }
        });
        txtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Common.HideKeyboard(v, context);
            }
        });
        txtConfirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Common.HideKeyboard(v, context);
            }
        });
        //endregion

        return view;
    }

    //region WebService Call
    private void LoadAvailableCities() {
        _svcConnection.GetAvailableCities(new WSResponseListener() {
            @Override
            public void onError(String message) {
                obj.CloseLoadingScreen();
                if (message.contains("NO_CONNECTION")) {
                    Common.DialogStatusAlert(context,getResources().getString(R.string.ORI_NoInternetConnection_Msg),getResources().getString(R.string.ORI_NoInternetConnection_Title),"Error");
                } else {
                    Common.DialogStatusAlert(context, message, getResources().getString(R.string.WizardCreateAccount_LoadCitiesFailed), "Error");
                }
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
                ArrayAdapter<String> adapter = new ArrayAdapter<>(context,android.R.layout.simple_spinner_item,list);
                adapter.setDropDownViewResource(android.R.layout.select_dialog_item);
                sprAvailableCities.setAdapter(adapter);
                SetPaddingToSpinner();
                obj.CloseLoadingScreen();
            }
        });
    }
    private void PreCreateAccount() {
        int city = CID.get(sprAvailableCities.getSelectedItemPosition());
        String mobile = txtMobile.getText().toString();
        String email = txtMail.getText().toString();
        String password = txtPassword.getText().toString();
        String confirmPassword = txtConfirmPassword.getText().toString();
        boolean terms = swTermsAcceptedChecked.isChecked();
        String Errors = AnyErrors(city, mobile, email, password, confirmPassword, terms);
        if (Errors.trim().isEmpty()) {
            Bundle bundle = getArguments();
            String givenname = bundle.getString("ori_givenname");
            String gender = bundle.getString("ori_genderselected");
            Fragment fragment;
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            fragment = new Fragment_Wizard_NotificationAlert();
            bundle = new Bundle();
            bundle.putString("DisplayOpt","AccountCreated");
            bundle.putString("ori_createdname", givenname);
            bundle.putString("ori_createdemail", email);
            bundle.putString("ori_createdgender", gender);
            fragment.setArguments(bundle);
            transaction.setCustomAnimations(R.animator.anim_slide_inright, R.animator.anim_slide_outleft);
            obj.CloseLoadingScreen();
            transaction.replace(R.id.dialogframecontainer, fragment, "ori_notificationalert").commit();
//            StartCreatingAccount(city, email, mobile, Common.SHA256(password));
        } else {
            obj.CloseLoadingScreen();
            Common.DialogStatusAlert(context, Errors, getResources().getString(R.string.ORIGlobal_AnyErrorsTitle), "Error");
        }
    }
    private void StartCreatingAccount(int city, final String email, String mobile, String password) {
        Bundle bundle = getArguments();
        final String givenname = bundle.getString("ori_givenname");
        String lastname = bundle.getString("ori_lastname");
        String nickname = bundle.getString("ori_nickname");
        final String gender = bundle.getString("ori_genderselected");
        _svcConnection.CreateNewAccount(email, password, mobile, city, nickname, lastname, givenname, gender, profilePicture, new WSResponseListener() {
            @Override
            public void onError(String message) {
                obj.CloseLoadingScreen();
                Common.DialogStatusAlert(context, message, getResources().getString(R.string.ORIGlobal_webServiceError), "Error");
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                try {
                    JSONObject response = jsonResponse.getJSONObject(0);
                    String status = response.getString("Status");
                    String UID = "";
                    String GID = "";
                    SharedPreferences pref = context.getSharedPreferences(getResources().getString(R.string.ORIGlobal_SharedPreferences), Context.MODE_PRIVATE);
                    Fragment fragment;
                    Bundle bundle;
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    switch (status) {
                        case "UserAdded":
                            /*Recoger el UnityId y el GatewayId*/
                            UID = response.getString("UID");
                            GID = response.getString("GID");
                            /*Guardarlos en las preferencias*/
                            SharedPreferences.Editor edit = pref.edit();
                            edit.putString("UID", UID);
                            edit.putString("GID", GID);
                            edit.apply();
                            fragment = new Fragment_Wizard_NotificationAlert();
                            bundle = new Bundle();
                            bundle.putString("DisplayOpt","AccountCreated");
                            bundle.putString("ori_createdname", givenname);
                            bundle.putString("ori_createdemail", email);
                            bundle.putString("ori_createdgender", gender);
                            fragment.setArguments(bundle);
                            transaction.setCustomAnimations(R.animator.anim_slide_inright, R.animator.anim_slide_outleft);
                            obj.CloseLoadingScreen();
                            transaction.replace(R.id.dialogframecontainer, fragment, "ori_notificationalert").commit();
                            break;
                        case "Error_InactiveAccount":
                            obj.CloseLoadingScreen();
                            Common.DialogStatusAlert(context, getResources().getString(R.string.WizardCreateAccount_InactiveAccountMsg), getResources().getString(R.string.WizardCreateAccount_InactiveAccountTitle), "Error");
                            break;
                        case "EXIST":
                            obj.CloseLoadingScreen();
                            Common.DialogStatusAlert(context, getResources().getString(R.string.WizardCreateAccount_ExistMsg), getResources().getString(R.string.WizardCreateAccount_ExistTitle), "Error");
                            break;
                        case "ERROR":
                            obj.CloseLoadingScreen();
                            Common.DialogStatusAlert(context,"Lo sentimos, pero ocurrió un error inesperado.","ORI Driver Services", "Error");
                            break;
                        case "ERROR_EmailTemplateNotFound":
                            obj.CloseLoadingScreen();
                            Common.DialogStatusAlert(context,"Lo sentimos, pero ocurrió un error inesperado.","ORI Driver Services", "Error");
                            break;

                        default:
                            if (status.contains("EXIST_")){
                                String results[] = status.split("_");
                                /*Recoger el UnityId y el GatewayId*/
                                UID = response.getString("UID");
                                GID = response.getString("GID");
                                /*Guardarlos en las preferencias*/
                                SharedPreferences.Editor editExist = pref.edit();
                                editExist.putString("UID", UID);
                                editExist.putString("GID", GID);
                                editExist.apply();
                                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                View dialogView = inflater.inflate(R.layout.template_dialogstatusalert,null);
                                TextView dialogTitle = dialogView.findViewById(R.id.alertDialogTitle);
                                TextView dialogMsg = dialogView.findViewById(R.id.alertDialogMsg);
                                ImageView dialogIcon = dialogView.findViewById(R.id.alertDialogIcon);
                                dialogTitle.setText("¡Este usuario ya existe en alguno de nuestros servicios!");
                                dialogMsg.setText("Utiliza la cuenta blabla preguntarle a Raul para el mensaje");
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
                            break;
                    }
                } catch (JSONException e) {
                    obj.CloseLoadingScreen();
                    Common.DialogStatusAlert(context, e.toString(), getResources().getString(R.string.ORIGlobal_webServiceError), "Error");
                }
            }
        });
    }
    //endregion

    //region MISC
    private void InitFragControls() {
        sprAvailableCities = view.findViewById(R.id.sprAvailableCities);
        txtMobile = view.findViewById(R.id.txtMobile);
        txtMail = view.findViewById(R.id.txtMail);
        txtPassword = view.findViewById(R.id.txtPassword);
        txtConfirmPassword = view.findViewById(R.id.txtConfirmPassword);
        btnTermsInfo = view.findViewById(R.id.btnTermsInfo);
        swTermsAcceptedChecked = view.findViewById(R.id.swTermsAcceptedChecked);
        btnCreateAccountPressed = view.findViewById(R.id.btnCreateAccountPressed);
        GetScreenInches();
        SetBtnHeightByScreenInches();
    }
    private String AnyErrors(int city, String mobile, String email, String password, String confirmPassword, boolean terms) {
        String result = "";
//        result += (mobile.trim().isEmpty()) ? getResources().getString(R.string.WizardCreateAccount_MobileEmpty) + "\n" : "";
//        result += (mobile.length() < 10) ? getResources().getString(R.string.WizardCreateAccount_MobileNotValid) + "\n"  : "";
//        result += (city < 1) ? getResources().getString(R.string.WizardCreateAccount_NoCitySelected) + "\n"  : "";
//        result += (email.trim().isEmpty()) ? getResources().getString(R.string.WizardCreateAccount_EmailEmpty) + "\n"  : "";
//        result += (!Common.IsEmail(email)) ? getResources().getString(R.string.WizardCreateAccount_EmailNotValid) + "\n"  : "";
//        result += (password.trim().isEmpty()) ? getResources().getString(R.string.WizardCreateAccount_PasswordEmpty) + "\n"  : "";
//        result += (!password.equals(confirmPassword)) ? getResources().getString(R.string.WizardCreateAccount_PasswordNotSame) + "\n"  : "";
//        result += (!terms) ? getResources().getString(R.string.WizardCreateAccount_TermsNotAccepted) : "";
        return result;
    }
    private void GetScreenInches() {
        dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        Double x = Math.pow(dm.widthPixels/dm.xdpi,2);
        Double y = Math.pow(dm.heightPixels/dm.xdpi,2);
        Double inches = Math.sqrt(x+y);
        _screenSize = inches;
    }
    private void SetBtnHeightByScreenInches() {
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int size = 0;
        if (width == 480 && height == 800) {
            size = 6;
        } else if (width < 720 && height < 1184) {
            size = 8;
        } else if (width == 720 && height == 1184) {
            size = 15;
        } else if ((width > 720 && height > 1184) && (width < 1081 && height < 1920)) {
            size = 20;
        }
        if (size != 0) {
            txtConfirmPassword.setPadding(size,size,size,size);
            txtPassword.setPadding(size,size,size,size);
            txtMail.setPadding(size,size,size,size);
            txtMobile.setPadding(size,size,size,size);
        }
    }
    private void SetPaddingToSpinner() {
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int size = 0;
        if (width == 480 && height == 800) {
            size = 5;
        } else if (width < 720 && height < 1184) {
            size = 8;
        } else if (width == 720 && height == 1184) {
            size = 15;
        } else if ((width > 720 && height > 1184) && (width < 1081 && height < 1920)) {
            size = 15;
        }
        if (size != 0) {
            sprAvailableCities.setPadding(size,size,size,size);
        }
    }
    //endregion
}
