/*
 * Created by Adrian Joshet Moreno Fabian on 5/6/18 1:21 PM .
 * Rocha Technologies de Mexico SA de CV
 * soporte@rochatech.com
 */

package com.rochatech.oridriver;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.rochatech.webService.*;
import com.rochatech.library.Common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Map_Driver extends AppCompatActivity {

    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mToogle;
    NavigationView NavView;
    connectToService _svcConnection;
    Common obj;

    /**/
    String cardBrand, cardNumber;
    int BankAccountId;

    /*Preguntar por permisos*/
    private LocationManager locationManager;
    private static final int PERMISSION_GRANTED = 1;
    private static final int ACTIVATED_GPS = 1;

    /*NavMenu Switch*/
    private SwitchCompat navmenu_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_driver_activity);
        obj = new Common(Map_Driver.this);
        _svcConnection = new connectToService(Map_Driver.this, obj.GetSharedPreferencesValue(Map_Driver.this, "SessionToken"));
        /*Preguntar por permisos para obtener la ubicacion del usuario*/
        if (IsGPSEnabled()){
            /*Preguntar por los permisos*/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    NeedPermissions();
                }
            }
        } else {
            /*Hay que habilitar el GPS*/
            /*Mensaje para decirle al usuario porque necesita el GPS encendido*/
            EnableGPS();
        }
        InitCustomToolbar();
        InitDriverHeader();
        InitDriverStatus();
        InitDriverRate();
//        CheckIfPaymentInfoIsAvailable();
        String welcome = getIntent().getStringExtra("ORIWelcomeMsg");
        if (welcome != null) {
            Snackbar snackbar = Snackbar.make(mDrawerLayout,welcome,Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
        NavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int size = NavView.getMenu().size();
                for (int i = 0; i < size; i++) {
                    NavView.getMenu().getItem(i).setChecked(false);
                }
                switch (item.getItemId()) {
                    case R.id.DriverMap:
                        item.setChecked(true);
                        mDrawerLayout.closeDrawer(Gravity.START);
                        break;
                    case R.id.DriverRequest:
                        Intent request = new Intent(Map_Driver.this, Settings_FavRequest.class);
                        startActivity(request);
                        break;
                    case R.id.DriverHistory:
                        Intent history = new Intent(Map_Driver.this, Settings_TripHistory.class);
                        startActivity(history);
                        break;
                    case R.id.DriverSettings:
                        item.setChecked(true);
                        Intent settings = new Intent(Map_Driver.this, Settings_Main.class);
                        startActivity(settings);
                        break;
                    case R.id.DriverLogoff:
                        item.setChecked(true);
                        AlertDialog.Builder dialog = new AlertDialog.Builder(Map_Driver.this);
                        LayoutInflater inflater = getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.template_dialogstatusalert,null);
                        TextView dialogTitle = dialogView.findViewById(R.id.alertDialogTitle);
                        TextView dialogMsg = dialogView.findViewById(R.id.alertDialogMsg);
                        ImageView dialogIcon = dialogView.findViewById(R.id.alertDialogIcon);
                        dialogTitle.setText("Cerrando sesión");
                        dialogMsg.setText("¿Esta seguro que desea cerrar sesión?");
                        dialogIcon.setImageResource(R.drawable.ic_logoff);
                        dialog.setView(dialogView);
                        dialog.setPositiveButton("Cerrar Sesión", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int UID = Integer.parseInt(obj.GetSharedPreferencesValue(Map_Driver.this, "UID"));
                                /*Recuperar la opcion de "Volver a preguntar" del dialog para el cambio de estado del conductor*/
                                String askAgain = obj.GetSharedPreferencesValue(Map_Driver.this,"app_askagain");
                                Common.DeleteAllSharedPreferences(Map_Driver.this);
                                _svcConnection.LogOffUser(UID);
                                /*Volver a poner en los shared el valor de "Volver a preguntar"*/
                                SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(getResources().getString(R.string.ORIGlobal_SharedPreferences), Context.MODE_PRIVATE).edit();
                                editor.putString("app_askagain",askAgain);
                                editor.apply();
                                Intent intent = new Intent(Map_Driver.this, Wizard_Login.class);
                                startActivity(intent);
                            }
                        });
                        dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mDrawerLayout.closeDrawer(Gravity.START);
                            }
                        });
                        dialog.setCancelable(false);
                        dialog.show();
                        break;
                }
                return false;
            }
        });
    }

    //region Initialize Toolbar
    private void InitCustomToolbar() {
        NavView = findViewById(R.id.MapNavMenu);
        Toolbar mapToolbar = findViewById(R.id.PassengerMapToolbar);
        mapToolbar.setBackgroundColor(Color.TRANSPARENT);
        mapToolbar.setTitle("");
        mDrawerLayout = findViewById(R.id.MapDrawer);
        mToogle = new ActionBarDrawerToggle(Map_Driver.this, mDrawerLayout, R.string.ORIDrawerOpen, R.string.ORIDrawerClose);
        mDrawerLayout.addDrawerListener(mToogle);
        setSupportActionBar(mapToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToogle.syncState();
    }
    //endregion

    //region Location Permissions

    private boolean IsGPSEnabled() {
        Boolean result = false;
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            result = true;
        }
        return result;
    }
    private void SetLocationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                /*No tiene los permisos*/
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, PERMISSION_GRANTED);
            }
        } else {
//            AutoLogin();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_GRANTED:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Common.DialogStatusAlert(Map_Driver.this,"Los permisos necesarios han sido asignados, ahora blabla...","Permisos asignados","Success");
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!IsGPSEnabled()) {
            EnableGPS();
        }
    }

    //endregion

    //region MISC
    private void InitDriverHeader() {
        View view = NavView.getHeaderView(0);
        TextView PassengerName = view.findViewById(R.id.NavMenu_DriverName);
        ImageView PassengerProfilePicture = view.findViewById(R.id.PassengerProfilePic);
        PassengerName.setText(obj.GetSharedPreferencesValue(Map_Driver.this,"GivenName"));
        PassengerProfilePicture.setImageBitmap(_svcConnection.GetProfilePictureFromUID(Integer.parseInt(obj.GetSharedPreferencesValue(Map_Driver.this,"UID"))));
    }
    private void InitDriverStatus() {
//        A=Available, P=OnCourseToPickUp, B=Bussy, X=Offline
        Menu menu = NavView.getMenu();
        MenuItem menuItem = menu.findItem(R.id.DriverStatus);
        View actionView = menuItem.getActionView();
        navmenu_status = actionView.findViewById(R.id.Menu_DriverStatus);
        navmenu_status.setChecked(true);
        navmenu_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int UID = Integer.parseInt(obj.GetSharedPreferencesValue(Map_Driver.this, "UID"));
                if (navmenu_status.isChecked()) {
                    ChangeDriverStatus(UID, "X",false);
                } else {
                    ChangeDriverStatus(UID, "A",true);
                }
            }
        });

    }
    private void InitDriverRate(){
        View view = NavView.getHeaderView(0);
        final RatingBar passengerRate = view.findViewById(R.id.NavMenu_DriverRate);
        int UID = Integer.parseInt(obj.GetSharedPreferencesValue(Map_Driver.this, "UID"));
        _svcConnection.GetDriverRate(UID, new WSResponseListener() {
            @Override
            public void onError(String message) {
                switch (message) {
                    case "Error_InvalidToken":
                        Common.LogoffByInvalidToken(Map_Driver.this);
                        break;
                }
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                try {
                    String rate = jsonResponse.getString(0);
                    float oris;
                    if (!rate.trim().isEmpty()){
                        oris = Float.parseFloat(rate);
                        passengerRate.setRating(oris);
                    } else {
//                        oris = Float.parseFloat("3.5");
//                        passengerRate.setRating(oris);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void EnableGPS() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(Map_Driver.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.template_dialogstatusalert,null);
        TextView dialogTitle = dialogView.findViewById(R.id.alertDialogTitle);
        TextView dialogMsg = dialogView.findViewById(R.id.alertDialogMsg);
        ImageView dialogIcon = dialogView.findViewById(R.id.alertDialogIcon);
        dialogTitle.setText("El GPS no esta encendido");
        dialogMsg.setText("Encontramos que el GPS se encuentra apagado, es necesario que el GPS este encendido para que ORI pueda funcionar de manera correcta. Presione Encender para ayudarle a encenderlo.");
        dialogIcon.setImageResource(R.drawable.ic_error);
        dialog.setView(dialogView);
        dialog.setPositiveButton("Encender", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        dialog.show();
    }
    private void NeedPermissions() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(Map_Driver.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.template_dialogstatusalert,null);
        TextView dialogTitle = dialogView.findViewById(R.id.alertDialogTitle);
        TextView dialogMsg = dialogView.findViewById(R.id.alertDialogMsg);
        ImageView dialogIcon = dialogView.findViewById(R.id.alertDialogIcon);
        dialogTitle.setText("Necesitamos acceder a tu ubicación");
        dialogMsg.setText("Para una mejor experiencia, ORI necesita accesar a tu ubicación. Haga click en Conceder permisos y despues en Permitir.");
        dialogIcon.setImageResource(R.drawable.ic_error);
        dialog.setView(dialogView);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SetLocationPermissions();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }
    private void CheckIfPaymentInfoIsAvailable(){
        int opt = Integer.parseInt(obj.GetSharedPreferencesValue(Map_Driver.this,"settings_Payment"));
        int UID = Integer.parseInt(obj.GetSharedPreferencesValue(Map_Driver.this,"UID"));
        if (opt != 1) {
            GetPaymentInfo(UID);
        }
    }
    private void GetPaymentInfo(int UID){
        _svcConnection.GetCardOrAcntForService(UID, 0, "0", new WSResponseListener() {
            @Override
            public void onError(String message) {
                switch (message) {
                    case "Error_InvalidToken":
                        Common.LogoffByInvalidToken(Map_Driver.this);
                        break;
                }
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                try {
                    JSONObject response = jsonResponse.getJSONObject(0);
                    BankAccountId = Integer.parseInt(response.getString("Id"));
                    cardBrand = response.getString("CardBrand");
                    cardNumber = response.getString("CardNumber");
                } catch (JSONException e) {
                    obj.CloseLoadingScreen();
                    Common.DialogStatusAlert(Map_Driver.this, e.toString(), getResources().getString(R.string.ORIGlobal_webServiceError),"Error");
                }
            }
        });
    }
    private void ChangeDriverStatus(int UID, final String newStatus, final boolean showMsg) {
        _svcConnection.ChangeDriverStatus(UID, newStatus, new WSResponseListener() {
            @Override
            public void onError(String message) {
                switch (message) {
                    case "Error_InvalidToken":
                        Common.LogoffByInvalidToken(Map_Driver.this);
                        break;
                }
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                try {
                    String response = jsonResponse.getString(0);
                    SharedPreferences preferences = Map_Driver.this.getSharedPreferences(getResources().getString(R.string.ORIGlobal_SharedPreferences), Context.MODE_PRIVATE);
                    final SharedPreferences.Editor edit = preferences.edit();
                    edit.putString("user_OnlineStatus", newStatus);
                    String askAgain = obj.GetSharedPreferencesValue(Map_Driver.this,"app_askagain");
                    LayoutInflater inflater = (LayoutInflater) Map_Driver.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View dialogView = inflater.inflate(R.layout.template_dialogdonotaskagain,null);
                    final CheckBox donotaskagain = dialogView.findViewById(R.id.DoNotAskAgain);
                    if (showMsg) {
                        if (askAgain.contains("Y")) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(Map_Driver.this);
                            Typeface font = Typeface.createFromAsset(getResources().getAssets(),"montserrat_regular.ttf");
                            donotaskagain.setTypeface(font);
                            dialog.setView(dialogView);
                            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (donotaskagain.isChecked()) {
                                        edit.putString("app_askagain","N");
                                        edit.apply();
                                    }
                                }
                            });
                            dialog.setCancelable(false);
                            dialog.show();
                        } else {

                        }
                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //endregion

    /*Triggered when pressed back button*/
    @Override
    public void onBackPressed() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(Map_Driver.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.template_dialogstatusalert,null);
        TextView dialogTitle = dialogView.findViewById(R.id.alertDialogTitle);
        TextView dialogMsg = dialogView.findViewById(R.id.alertDialogMsg);
        ImageView dialogIcon = dialogView.findViewById(R.id.alertDialogIcon);
        dialogTitle.setText("¿Estas seguro que deseas salir de la aplicación?");
        dialogMsg.setText("Recuerda que para navegar por nuestra aplicación puedes utilizar nuestro menu y la flecha de navegación en la parte superior derecha.");
        dialogIcon.setImageResource(R.drawable.ic_logoff);
        dialog.setView(dialogView);
        dialog.setPositiveButton("Salir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Map_Driver.super.onBackPressed();
            }
        });
        dialog.setNegativeButton("Cancelar",null);
        dialog.setCancelable(false);
        dialog.show();
    }

    /*Triggered when clicked menu icon*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (mToogle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
