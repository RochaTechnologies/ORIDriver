package com.rochatech.oridriver;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.Button;
import com.rochatech.library.Common;
import com.rochatech.webService.*;

public class Settings_Main extends AppCompatActivity {

    //region Global
    LinearLayoutCompat _personalInfo, _changePass, _paymentNDrivers, _membershipStatus, _depositAct, _WithDrawAct;
    connectToService _svcConnection;
    Common obj;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_main_activity);
        InitAppControls();
        _personalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings_Main.this, Settings_PersonalInfo.class);
                startActivity(intent);
            }
        });
        _changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings_Main.this, Settings_ChangePassword.class);
                startActivity(intent);
            }
        });
        _paymentNDrivers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings_Main.this, Settings_DriverNPayment.class);
                startActivity(intent);
            }
        });
        _membershipStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings_Main.this, Settings_Membership.class);
                startActivity(intent);
            }
        });
        _depositAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings_Main.this, Settings_DepositAccount.class);
                startActivity(intent);
            }
        });
        _WithDrawAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings_Main.this, Settings_BankAccount.class);
                startActivity(intent);
            }
        });
    }


    //region MISC
    private void InitAppControls() {
        _personalInfo = findViewById(R.id.PersonalInfo);
        _changePass = findViewById(R.id.ChangePassword);
        _paymentNDrivers = findViewById(R.id.DriversNPayments);
        _membershipStatus = findViewById(R.id.MembershipStatus);
        _depositAct = findViewById(R.id.DepositAct);
        _WithDrawAct = findViewById(R.id.WithdrawAct);
    }
    //endregion
}
