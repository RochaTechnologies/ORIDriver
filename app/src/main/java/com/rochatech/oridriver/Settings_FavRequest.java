package com.rochatech.oridriver;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.rochatech.webService.*;
import com.rochatech.library.Common;
import com.rochatech.Model.FavoriteDrivers;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
//import android.app.Fragment;
//import android.app.FragmentManager;
//import android.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Settings_FavRequest extends AppCompatActivity {

    connectToService _svcConnection;
    Common obj;
    int UID = 0;
    int pendingRequest = 0;
    int totalaccepted = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_favrequest_activity);
        obj = new Common(Settings_FavRequest.this);
        _svcConnection = new connectToService(Settings_FavRequest.this, obj.GetSharedPreferencesValue(Settings_FavRequest.this, "SessionToken"));
        UID = Integer.parseInt(obj.GetSharedPreferencesValue(Settings_FavRequest.this, "UID"));
        GetTotalFavDriverPendingRequest(UID);
    }

    private void GetTotalFavDriverPendingRequest(final int UID) {
        obj.ShowLoadingScreen(Settings_FavRequest.this,"Cargando información, por favor espere...");
        _svcConnection.GetMyFavoriteDriverRequest(UID, new WSResponseListener() {
            @Override
            public void onError(String message) {
                switch (message) {
                    case "Error_InvalidToken":
                        obj.CloseLoadingScreen();
                        Common.LogoffByInvalidToken(Settings_FavRequest.this);
                        break;
                    case "NO_CONNECTION":
                        obj.CloseLoadingScreen();
                        Common.DialogStatusAlert(Settings_FavRequest.this,getResources().getString(R.string.ORI_NoInternetConnection_Msg),getResources().getString(R.string.ORI_NoInternetConnection_Title),"Error");
                        break;
                }
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                try {
                    ArrayList<FavoriteDrivers> favrequest = FavoriteDrivers.fromJson(jsonResponse);
                    pendingRequest = favrequest.size();
                    if (pendingRequest != 0 && pendingRequest > 0) {
                        /*Mostrar fragmento de lista de req pendientes*/
                        SelectNSetFragment(2);
                    } else {
                        /*Mostrar fragmento main*/
                        TotalApprovedFavoriteDriverRequest(UID);
//                        SelectNSetFragment(1);
                    }
                } catch (Exception e) {
                    obj.CloseLoadingScreen();
                    Common.DialogStatusAlert(Settings_FavRequest.this, e.toString(),getResources().getString(R.string.ORIGlobal_webServiceError),"Error");
                }
            }
        });
    }
    private void TotalApprovedFavoriteDriverRequest(int UID) {
        _svcConnection.GetTotalApprovedFavoriteDriverRequest(UID, new WSResponseListener() {
            @Override
            public void onError(String message) {
                switch (message) {
                    case "Error_InvalidToken":
                        obj.CloseLoadingScreen();
                        Common.LogoffByInvalidToken(Settings_FavRequest.this);
                        break;
                    case "NO_CONNECTION":
                        obj.CloseLoadingScreen();
                        Common.DialogStatusAlert(Settings_FavRequest.this,getResources().getString(R.string.ORI_NoInternetConnection_Msg),getResources().getString(R.string.ORI_NoInternetConnection_Title),"Error");
                        break;
                }
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                try {
                    JSONObject response = jsonResponse.getJSONObject(0);
                    String total = response.getString("Total");
                    totalaccepted = Integer.parseInt(total);
                    SelectNSetFragment(1);
                } catch (JSONException e) {
                    obj.CloseLoadingScreen();
                    Common.DialogStatusAlert(Settings_FavRequest.this, e.toString(),getResources().getString(R.string.ORIGlobal_webServiceError),"Error");
                }
            }
        });

    }

    //region MISC
    private void SelectNSetFragment(int option) {
        switch (option) {
            case 1:
                /*Mostrar fragmento main*/
                Fragment main = new Fragment_MainFavContent();
                FragmentManager mainmanager = getSupportFragmentManager();
                FragmentTransaction maintransaction = mainmanager.beginTransaction();
                Bundle arg = new Bundle();
                arg.putInt("totalapproved", totalaccepted);
                main.setArguments(arg);
                obj.CloseLoadingScreen();
                maintransaction.replace(R.id.FragContent, main);
                maintransaction.commit();
                break;
            case 2:
                /*Mostrar fragmento de lista de req pendientes*/
                Fragment pendigfav = new Fragment_PendingFavRequest();
                FragmentManager pendingmanager = getSupportFragmentManager();
                FragmentTransaction pendingtransaction = pendingmanager.beginTransaction();
                obj.CloseLoadingScreen();
                pendingtransaction.replace(R.id.FragContent, pendigfav);
                pendingtransaction.commit();
                break;
        }
    }
    //endregion
}
