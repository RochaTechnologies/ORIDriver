package com.rochatech.oridriver;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.widget.TextView;
import com.rochatech.library.Common;
import com.rochatech.webService.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Settings_Membership extends AppCompatActivity {

    connectToService _svcConnection;
    Common obj;

    SwitchCompat Membershipstatus;
    TextView Servicetype, Membersince, Membershiplast, Vehiclebrand, Vehiclemodel, Vehiclecolor, Vehicleyear, Vehicleplates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_membership_status);
        obj = new Common(Settings_Membership.this);
        _svcConnection = new connectToService(Settings_Membership.this, obj.GetSharedPreferencesValue(Settings_Membership.this, "SessionToken"));
        InitAppControls();
        LoadMembershipCurrentStatus(Integer.parseInt(obj.GetSharedPreferencesValue(Settings_Membership.this, "UID")));
    }

    //region WebService Call
    private void LoadMembershipCurrentStatus(int UID) {
        obj.ShowLoadingScreen(Settings_Membership.this,"Estamos cargando su información, por favor espere...");
        _svcConnection.GetCurrentMembershipStatus(UID, new WSResponseListener() {
            @Override
            public void onError(String message) {
                switch (message) {
                    case "Error_InvalidToken":
                        obj.CloseLoadingScreen();
                        Common.LogoffByInvalidToken(Settings_Membership.this);
                        break;
                    case "NO_CONNECTION":
                        obj.CloseLoadingScreen();
                        Common.DialogStatusAlert(Settings_Membership.this,getResources().getString(R.string.ORI_NoInternetConnection_Msg),getResources().getString(R.string.ORI_NoInternetConnection_Title),"Error");
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
                            //regresa un IsValid
                            String membershipStatus = response.getString("MembershipStatus");
                            String serviceType = response.getString("ServiceType");
                            String memberSince = response.getString("MemberSince");
                            String nextPaymentDate = response.getString("NextPaymentDate");
                            String vehicleBrand = response.getString("VehicleBrand");
                            String vehicleModel = response.getString("VehicleModel");
                            String vehicleYear = response.getString("VehicleYear");
                            String vehicleColor = response.getString("VehicleColor");
                            String vehiclePlates = response.getString("VehicleLicensePlate");
                            StartSettingInfoOnActivity(membershipStatus, serviceType, memberSince, nextPaymentDate, vehicleBrand, vehicleModel, vehicleYear, vehicleColor, vehiclePlates);
                            break;
                    }
                } catch (JSONException e) {
                    Common.DialogStatusAlert(Settings_Membership.this, e.toString(), getResources().getString(R.string.ORIGlobal_webServiceError),"Error");
                }
            }
        });
    }

    //endregion

    //region MISC
    private void InitAppControls() {
        Membershipstatus = findViewById(R.id.IsMembershipActive);
        Servicetype = findViewById(R.id.ServiceType);
        Membersince = findViewById(R.id.MemberSince);
        Membershiplast = findViewById(R.id.MembershipLast);
        Vehiclebrand = findViewById(R.id.VehicleBrand);
        Vehiclemodel = findViewById(R.id.VehicleModel);
        Vehiclecolor = findViewById(R.id.VehicleColor);
        Vehicleyear = findViewById(R.id.VehicleYear);
        Vehicleplates = findViewById(R.id.VehiclePlates);
    }

    private void StartSettingInfoOnActivity(String membershipStatus, String serviceType, String memberSince, String nextPaymentDate, String vehicleBrand, String vehicleModel, String vehicleYear, String vehicleColor, String vehiclePlates) {
        if (membershipStatus.trim().equals("IsValid")) {
            Membershipstatus.setChecked(true);
        } else {
            Membershipstatus.setChecked(false);
        }
        Servicetype.setText(serviceType);
        Membersince.setText(memberSince);
        Membershiplast.setText(nextPaymentDate);
        Vehiclebrand.setText(vehicleBrand);
        Vehiclemodel.setText(vehicleModel);
        Vehiclecolor.setText(vehicleColor);
        Vehicleyear.setText(vehicleYear);
        Vehicleplates.setText(vehiclePlates);
        obj.CloseLoadingScreen();
    }

    private void LogoffUser(Common obj) {
        int UID = Integer.parseInt(obj.GetSharedPreferencesValue(Settings_Membership.this, "UID"));
        String sessionToken = obj.GetSharedPreferencesValue(Settings_Membership.this, "SessionToken");
        _svcConnection.LogOffUser(UID);
        Common.DeleteAllSharedPreferences(Settings_Membership.this);
        final Intent intent = new Intent(Settings_Membership.this, Wizard_Login.class);
        AlertDialog.Builder dialog = new AlertDialog.Builder(Settings_Membership.this);
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
