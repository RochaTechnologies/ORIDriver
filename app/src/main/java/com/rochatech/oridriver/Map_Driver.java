/*
 * Created by Adrian Joshet Moreno Fabian on 5/6/18 1:21 PM .
 * Rocha Technologies de Mexico SA de CV
 * soporte@rochatech.com
 */

package com.rochatech.oridriver;

//region rest
import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.rochatech.library.Support_BottomDialog;
import com.rochatech.webService.*;
import com.rochatech.library.Common;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
//endregion

//region Mapbox
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.services.android.ui.geocoder.GeocoderAutoCompleteView;
import com.mapbox.services.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.services.commons.models.Position;
//endregion

public class Map_Driver extends AppCompatActivity implements LocationListener {

    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mToogle;
    NavigationView NavView;
    connectToService _svcConnection;
    Common obj;

    Integer driverUID;
    Integer _cityId;

    TextView lastUpdate;
    TextView lastUpdateReqStatus;

    //region Mapbox variables
    private final String MAPBOX_ACCESSTOKEN = "pk.eyJ1Ijoicm9jaGF0ZWNoIiwiYSI6ImNqZjAwbXZndTBnbDkzMm9kM3ppdWp6aXUifQ.AiF9e0dfxbDyeBQSxGaZwA";
    private MapView mapView;
    private MapboxMap map;

    Icon _pickupLocationMarkerIcon;
    Icon _dropOffLocationMarkerIcon;
    private Point _pickupLocationPosition;
    private Point _dropoffLocationPosition;
    private DirectionsRoute _currentRoute;
    private static final String TAG = "DirectionsActivity";
    private NavigationMapRoute _navigationMapRoute;
    private Marker _pickupLocationMarker;
    private LatLng _pickupLocationCoord;
    private Marker _dropOffLocationMarker;
    private LatLng _dropoffLocationCoord;

    Icon _driverLocationMarkerIcon;
    private LatLng _driverLocationCoord;
    private Marker _driverLocationMarker;
    private Location _driverCurrentLocation;
    private Location _driverPreviousLocation;

    private double _estimatedFare;
    private double _estimatedDistante;
    private double _estimatedTime;
    //endregion

    //region Progress Dialog
    ProgressDialog _searchingYourLocation;
    Boolean isSearchingYourLocationHidden = false;
    ProgressDialog _loadingTripDetails;
    //endregion

    //region Bottomsheet Views
    View _botSheet_AvailableTripRequest;
    View _botSheet_DriverOnTheWay;
    View _botSheet_DriverRateDriver;
    //endregion

    //region Bottomsheet Behavior
    BottomSheetBehavior _botBehave_AvailableTripRequest;
    BottomSheetBehavior _botBehave_DriverOnTheWay;
    BottomSheetBehavior _botBehave_DriverRateDriver;
    //endregion

    //region Passenger Profile Picture
    Bitmap _reqByPassengerProfilePic;
    //endregion

    /**/
    String cardBrand, cardNumber;
    int BankAccountId;

    /*Preguntar por permisos*/
    private LocationManager locationManager;
    private LocationListener locationListener = this;
    private long LOCATION_TIME = 0;
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
        lastUpdate = findViewById(R.id.lastUpdate);
        Map_InitMap(savedInstanceState);








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

    //region Mapbox Map
    public void Map_InitMap(Bundle savedInstanceState) {
        if (obj.isInternetConnectionActive(Map_Driver.this)) {
            _searchingYourLocation = new ProgressDialog(Map_Driver.this);
            _searchingYourLocation.setMessage("Buscando tu ubicación");
            _searchingYourLocation.setCancelable(false);
            _searchingYourLocation.show();
            _pickupLocationMarkerIcon = IconFactory.getInstance(Map_Driver.this).fromResource(R.drawable.ic_menu_startmark40);
            _dropOffLocationMarkerIcon = IconFactory.getInstance(Map_Driver.this).fromResource(R.drawable.ic_menu_endmark100);

            Mapbox.getInstance(this, MAPBOX_ACCESSTOKEN);
            mapView = findViewById(R.id.mapView);
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(MapboxMap mapboxMap) {
                    Map_Driver.this.map = mapboxMap;
                    /*  Delete compass  */
                    map.getUiSettings().setCompassEnabled(false);
                    Map_ConfigureMap();
                }
            });

        } else {
            Common.DialogStatusAlert(Map_Driver.this,getResources().getString(R.string.ORI_NoInternetConnection_Msg),getResources().getString(R.string.ORI_NoInternetConnection_Title),"Error");
        }
    }
    public void Map_ConfigureMap() {
        LOCATION_TIME = 1000;
        StartUpdatingLocation(LOCATION_TIME,0);
    }

    private void SetNMarkDriverLocation() {
        if (_searchingYourLocation != null && !isSearchingYourLocationHidden) {
            isSearchingYourLocationHidden = true;
            _searchingYourLocation.dismiss();
        }
        if (_driverLocationMarker != null) {
            _driverLocationMarker.remove();
        }
        _driverCurrentLocation = GetDriverBestLastKnowLocation();
        _driverLocationCoord = new LatLng(_driverCurrentLocation.getLatitude(),_driverCurrentLocation.getLongitude());
        _driverLocationMarker = map.addMarker(new MarkerOptions()
                .position(_driverLocationCoord)
                .setIcon(_pickupLocationMarkerIcon)
        );
        setCameraPosition(_driverCurrentLocation);
    }
    private void setCameraPosition(Location location) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 15));
    }
    protected Address getLocationData(LatLng loc) {
        Geocoder geocoder =  new Geocoder(getApplicationContext(), Locale.getDefault());
        // Get the current location from the input parameter list
        //Location loc = params[0];
        // Create a list to contain the result address
        List<Address> addresses = null;
        try {
            /*
             * Return 1 address.
             */
            addresses = geocoder.getFromLocation(loc.getLatitude(),
                    loc.getLongitude(), 1);
        } catch (IOException e1) {
            Log.e("LocationSampleActivity",
                    "IO Exception in getFromLocation()");
            e1.printStackTrace();
            return null;// ("IO Exception trying to get address");
        } catch (IllegalArgumentException e2) {
            // Error message to post in the log
            String errorString = "Illegal arguments " +
                    Double.toString(loc.getLatitude()) +
                    " , " +
                    Double.toString(loc.getLongitude()) +
                    " passed to address service";
            Log.e("LocationSampleActivity", errorString);
            e2.printStackTrace();
            return null;// errorString;
        }
        // If the reverse geocode returned an address
        if (addresses != null && addresses.size() > 0) {
            // Get the first address
            Address address = addresses.get(0);
            /*
             * Format the first line of address (if available),
             * city, and country name.
             */
            // Return the text
            return address;//addressText;
        } else {
            return null;//"No address found";
        }
    }
    private void getCityIdFromName(String CityName, String CountryName){
        _svcConnection.GetCityIdFromName(CityName, CountryName, new WSResponseListener() {
            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                try{
                    _cityId = Integer.parseInt(jsonResponse.getString(0));
                }catch (JSONException ex){

                }
            }
        });
    }
    private void getRoute(Point origin, Point destination) {
        NavigationRoute.builder()
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        // You can get the generic HTTP info about the response
                        Log.d(TAG, "Response code: " + response.code());
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Log.e(TAG, "No routes found");
                            return;
                        }

                        _currentRoute = response.body().routes().get(0);
                        _estimatedDistante = (int) Math.ceil(_currentRoute.distance() / 1000);
                        _estimatedTime = (int) Math.ceil(_currentRoute.duration() / 60);

                        // Draw the route on the map
                        if (_navigationMapRoute != null) {
                            _navigationMapRoute.removeRoute();
                        } else {
                            _navigationMapRoute = new NavigationMapRoute(null, mapView, map, R.style.NavigationMapRoute);
                        }
                        _navigationMapRoute.addRoute(_currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(TAG, "Error: " + throwable.getMessage());
                    }
                });
    }
    //endregion










    //region Get Driver Best Location
    private Location GetDriverBestLastKnowLocation() {
        Location currentLocation = null;
        List<String> providers = locationManager.getProviders(true);
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            Location location = locationManager.getLastKnownLocation(provider);
            if (location == null) {
                continue;
            }
            if (currentLocation == null || location.getAccuracy() < currentLocation.getAccuracy()) {
                currentLocation = location;
            }
        }
        return currentLocation;
    }
    //endregion


    //region Location Init

    //endregion


    //region Location Listerner / Searching for Travel Request
    private void StartUpdatingLocation(long location_time, float location_distance) {
        locationManager.removeUpdates(locationListener);
        /*Habilitar lo de best provider*/
        if (ActivityCompat.checkSelfPermission(Map_Driver.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            SetLocationPermissions();
            /*Revisar que pasa si no se dan los permisos*/
//          return;
        }
        locationManager.requestLocationUpdates("gps", LOCATION_TIME, 0, locationListener);
    }
    private void StopUpdatingLocation() {
        locationManager.removeUpdates(locationListener);
        locationManager = null;
    }
    //endregion










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


    //region onBackPressed
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
    //endregion


    //region onOptionItemSelected
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (mToogle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //endregion















    @Override
    public void onLocationChanged(Location location) {
        lastUpdate.setText("Ultima actualizacion: " + Common.getAppStringFullDateFromFullDate(Common.getNow()));
        SetNMarkDriverLocation();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
