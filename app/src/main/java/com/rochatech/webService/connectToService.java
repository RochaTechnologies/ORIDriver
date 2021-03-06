/*
 * Created by Adrian Joshet Moreno Fabian on 5/3/18 9:03 PM .
 * Rocha Technologies de Mexico SA de CV
 * soporte@rochatech.com
 */

package com.rochatech.webService;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.rochatech.library.Common;
import com.rochatech.oridriver.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
@SuppressWarnings({"WeakerAccess","unused","FieldCanBeLocal"})
public class connectToService {

    //region Global
    private Context context;
    private String _urlValidationSvc = "http://testws.rochatech.com/rtunity/ws/v1/aurlval.php";
    private String _mainURLString;
    private String _profilePicURL;
    private String _profilePicUpdateURL;
    private String _loginURL;
    private String _unityURL;
    private String _appURL;
    private String _errorURL;
    private String _serviceName = "RochaTech";
    private String _servicePassword = "Rocha2018";
    private Integer _unityServiceId = 3;
    private String _deviceFCMId;
    private int _accessPointId = 3;
    private String _sessionTokenId;
    private Common obj = new Common();
    //endregion

    public connectToService(Context context, String token) {
        this.context = context;
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.ORIGlobal_SharedPreferences), Context.MODE_PRIVATE);
        _sessionTokenId = preferences.getString("SessionToken", null);
        _deviceFCMId = preferences.getString("settings_FCMTokenId",null);
        _mainURLString = preferences.getString("reqenv_mainsvcurl",null);
        _profilePicURL = preferences.getString("reqenv_profilepictureurl",null);
        _profilePicUpdateURL = preferences.getString("reqenv_profilepicupdateurl",null);
        _loginURL = _mainURLString + "/rtunity/ws/v1/als.php";
        _unityURL = _mainURLString + "/rtunity/ws/v1/aus.php";
        _appURL = _mainURLString + "/rtorids/ws/v1/aos.php";
        _errorURL = _mainURLString + "/rtunity/ws/v1/aes.php";
    }

    //region WS Log Error
    public void LogDeviceError (String AppName, String AppVersion, String AppMethod, String AppMethodsParams, String AppActivity,
                                String DeviceModel, String DeviceBrand, String DeviceAPILevel, String DeviceOS, String DeviceScreenSize,
                                Integer UnityId,
                                String ErrorStackTrace, String ErrorMsg, String ErrorCode) {
        JSONObject paramsObject = new JSONObject();
        try {
            /*  App Info    */
            paramsObject.put("AppName", AppName);
            paramsObject.put("AppVersion", AppVersion);
            paramsObject.put("AppMethod", AppMethod);
            paramsObject.put("AppMethodParams", AppMethodsParams);
            paramsObject.put("AppActivity", AppActivity);
            /*  Device Info */
            paramsObject.put("DeviceModel", DeviceModel);
            paramsObject.put("DeviceBrand", DeviceBrand);
            paramsObject.put("DeviceAPILevel", DeviceAPILevel);
            paramsObject.put("DeviceOS", DeviceOS);
            paramsObject.put("DeviceScreenSize", DeviceScreenSize);
            /*  User Info   */
            paramsObject.put("UnityId", UnityId);
            /*  Error Info  */
            paramsObject.put("ErrorStackTrace", ErrorStackTrace);
            paramsObject.put("ErrorMessage", ErrorMsg);
            paramsObject.put("ErrorCode", ErrorCode);
            /*  Common      */
            paramsObject.put("AccessPointId", _accessPointId);
            paramsObject.put("Request", "Log_DeviceError");
            paramsObject.put("SessionToken", _sessionTokenId);
            paramsObject.put("UnityServiceId", _unityServiceId);
        } catch (JSONException e) {
            //
        }
        WebServiceCall(paramsObject, _errorURL, new WSResponseListener() {
            @Override
            public void onError(String message) {

            }
            @Override
            public void onResponseObject(JSONArray jsonResponse) {

            }

        });
    }
    //endregion


    /*Update SessionToken value*/
    public void SetSessionTokenId(String token) {
        this._sessionTokenId = token;
    }

    /*Get profile picture*/
    public Bitmap GetProfilePictureFromUID(int UnityId){
        profilePictures task = new profilePictures();
        task.SetContext(context);
        Bitmap result = null;
        try {
            String tmp = _profilePicURL.replace("RochaTech:Rocha2018@","");
            result = task.execute(tmp + "?d=" + Integer.toString(UnityId)).get();
        }  catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    /*Request for environment*/
    public void RequestEnvironment(final WSResponseListener listener) {
        //Parametros
        JSONObject paramsObject = new JSONObject();
        try {
            paramsObject.put("AccessPointId", _accessPointId);
            paramsObject.put("Request", "Request_Env");
            paramsObject.put("UnityServiceId", _unityServiceId);
            paramsObject.put("DeviceFCMId", _deviceFCMId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebServiceCall(paramsObject, _urlValidationSvc, new WSResponseListener() {
            @Override
            public void onError(String message) {
                listener.onError(message);
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                listener.onResponseObject(jsonResponse);
            }
        });
    }

    //region Update Information
    public void UpdateNickNameNCell (int UnityId, String NickName, String CellNumber, final WSResponseListener listener) {
        JSONObject paramsObject = new JSONObject();
        try {
            paramsObject.put("AccessPointId", _accessPointId);
            paramsObject.put("Request", "Update_NickNCell");
            paramsObject.put("SessionToken", _sessionTokenId);
            paramsObject.put("UnityId", UnityId);
            paramsObject.put("UnityServiceId", _unityServiceId);
            paramsObject.put("NickName", NickName);
            paramsObject.put("CellNumber", CellNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebServiceCall(paramsObject, _unityURL, new WSResponseListener() {
            @Override
            public void onError(String message) {
                listener.onError(message);
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                listener.onResponseObject(jsonResponse);
            }
        });
    }
    public void UpdatePassword (int UnityId, String Password, final WSResponseListener listener) {
        JSONObject paramsObject = new JSONObject();
        try {
            paramsObject.put("AccessPointId", _accessPointId);
            paramsObject.put("Request", "Update_Password");
            paramsObject.put("SessionToken", _sessionTokenId);
            paramsObject.put("UnityId", UnityId);
            paramsObject.put("UnityServiceId", _unityServiceId);
            paramsObject.put("NewPassword", Password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebServiceCall(paramsObject, _unityURL, new WSResponseListener() {
            @Override
            public void onError(String message) {
                listener.onError(message);
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                listener.onResponseObject(jsonResponse);
            }
        });
    }
    public void UpdateProfilePicture (int UnityId, Bitmap ProfilePicture, final WSResponseListener listener) {
        String base64StringOf_My_Image = "";
        JSONObject paramsObject = new JSONObject();
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ProfilePicture.compress(Bitmap.CompressFormat.PNG, 70, os);
            byte[] byteArray = os.toByteArray();
            base64StringOf_My_Image = Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (Exception e) {
            Log.d("ERROR", "Base64Image: " + e.toString());
        }
        try {
            paramsObject.put("AccessPointId", _accessPointId);
            paramsObject.put("Request", "Update_ProfilePicture");
            paramsObject.put("SessionToken", _sessionTokenId);
            paramsObject.put("UnityId", UnityId);
            paramsObject.put("UnityServiceId", _unityServiceId);
            paramsObject.put("ProfilePic", base64StringOf_My_Image);
        } catch (JSONException e) {
            Log.d("ERROR", "Param: " + e.toString());
        }
        WebServiceCall(paramsObject, _profilePicUpdateURL, new WSResponseListener() {
            @Override
            public void onError(String message) {
                listener.onError(message);
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                listener.onResponseObject(jsonResponse);
            }
        });
    }
    public void UpdatePreferredDriverGenderTypeId (int UnityId, String GenderTypeId, final WSResponseListener listener) {
        JSONObject paramsObject = new JSONObject();
        try {
            paramsObject.put("AccessPointId", _accessPointId);
            paramsObject.put("Request", "Passenger_UpdatePreferredDriverGenderTypeId");
            paramsObject.put("SessionToken", _sessionTokenId);
            paramsObject.put("UnityId", UnityId);
            paramsObject.put("UnityServiceId", _unityServiceId);
            paramsObject.put("GenderTypeId", GenderTypeId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebServiceCall(paramsObject, _appURL, new WSResponseListener() {
            @Override
            public void onError(String message) {
                listener.onError(message);
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                listener.onResponseObject(jsonResponse);
            }
        });
    }
    public void UpdatePreferredPaymentTypeId (int UnityId, String PaymentTypeId, final WSResponseListener listener) {
        JSONObject paramsObject = new JSONObject();
        try {
            paramsObject.put("AccessPointId", _accessPointId);
            paramsObject.put("Request", "Passenger_UpdatePreferredPaymentTypeId");
            paramsObject.put("SessionToken", _sessionTokenId);
            paramsObject.put("UnityId", UnityId);
            paramsObject.put("UnityServiceId", _unityServiceId);
            paramsObject.put("PaymentTypeId", PaymentTypeId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebServiceCall(paramsObject, _appURL, new WSResponseListener() {
            @Override
            public void onError(String message) {
                listener.onError(message);
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                listener.onResponseObject(jsonResponse);
            }
        });
    }
    //endregion

    //region Favorite Drivers
    public void GetFavoriteDrivers(int PassengerUnityId, final WSResponseListener listener) {
        JSONObject paramsObject = new JSONObject();
        try {
            paramsObject.put("AccessPointId", _accessPointId);
            paramsObject.put("Request", "Passenger_GetFavoriteDrivers");
            paramsObject.put("SessionToken", _sessionTokenId);
            paramsObject.put("UnityServiceId", _unityServiceId);
            paramsObject.put("UnityId", PassengerUnityId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebServiceCall(paramsObject, _appURL, new WSResponseListener() {
            @Override
            public void onError(String message) {
                listener.onError(message);
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                listener.onResponseObject(jsonResponse);
            }

        });
    }
    public void GetTotalApprovedFavoriteDriverRequest(int UnityId, final WSResponseListener listener) {
        JSONObject paramsObject = new JSONObject();
        try {
            paramsObject.put("AccessPointId", _accessPointId);
            paramsObject.put("Request", "Driver_GetTotalTimesFavorited");
            paramsObject.put("SessionToken", _sessionTokenId);
            paramsObject.put("UnityServiceId", _unityServiceId);
            paramsObject.put("UnityId", UnityId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebServiceCall(paramsObject, _appURL, new WSResponseListener() {
            @Override
            public void onError(String message) {
                listener.onError(message);
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                listener.onResponseObject(jsonResponse);
            }

        });
    }
    public void GetMyFavoriteDriverRequest(int UnityId, final WSResponseListener listener) {
        JSONObject paramsObject = new JSONObject();
        try {
            paramsObject.put("AccessPointId", _accessPointId);
            paramsObject.put("Request", "Driver_GetMyFavoriteDriverRequests");
            paramsObject.put("SessionToken", _sessionTokenId);
            paramsObject.put("UnityServiceId", _unityServiceId);
            paramsObject.put("UnityId", UnityId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebServiceCall(paramsObject, _appURL, new WSResponseListener() {
            @Override
            public void onError(String message) {
                listener.onError(message);
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                listener.onResponseObject(jsonResponse);
            }

        });
    }
    //endregion

    //region Preferences
    public void GetPreferredPaymentTypeId(int UnityId, final WSResponseListener listener) {
        JSONObject paramsObject = new JSONObject();
        try {
            paramsObject.put("AccessPointId", _accessPointId);
            paramsObject.put("Request", "Passenger_GetPreferredPaymentTypeId");
            paramsObject.put("SessionToken", _sessionTokenId);
            paramsObject.put("UnityServiceId", _unityServiceId);
            paramsObject.put("UnityId", UnityId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebServiceCall(paramsObject, _appURL, new WSResponseListener() {
            @Override
            public void onError(String message) {
                listener.onError(message);
            }
            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                listener.onResponseObject(jsonResponse);
            }
        });
    }
    public void GetPreferredDriverGenderTypeId(int UnityId, final WSResponseListener listener) {
        JSONObject paramsObject = new JSONObject();
        try {
            paramsObject.put("AccessPointId", _accessPointId);
            paramsObject.put("Request", "Passenger_GetPreferredDriverGenderTypeId");
            paramsObject.put("SessionToken", _sessionTokenId);
            paramsObject.put("UnityServiceId", _unityServiceId);
            paramsObject.put("UnityId", UnityId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebServiceCall(paramsObject, _appURL, new WSResponseListener() {
            @Override
            public void onError(String message) {
                listener.onError(message);
            }
            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                listener.onResponseObject(jsonResponse);
            }
        });
    }
    public void GetDriverRate(int UnityId, final WSResponseListener listener) {
        JSONObject paramsObject = new JSONObject();
        try {
            paramsObject.put("AccessPointId", _accessPointId);
            paramsObject.put("Request", "Driver_GetRate");
            paramsObject.put("SessionToken", _sessionTokenId);
            paramsObject.put("UnityServiceId", _unityServiceId);
            paramsObject.put("UnityId", UnityId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebServiceCall(paramsObject, _appURL, new WSResponseListener() {
            @Override
            public void onError(String message) {
                listener.onError(message);
            }
            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                listener.onResponseObject(jsonResponse);
            }
        });
    }
    public void ChangeDriverStatus(int UnityId, String Status, final WSResponseListener listener){
        JSONObject paramsObject = new JSONObject();
        try {
            paramsObject.put("AccessPointId", _accessPointId);
            paramsObject.put("Request", "Driver_ChangeStatus");
            paramsObject.put("SessionToken", _sessionTokenId);
            paramsObject.put("UnityServiceId", _unityServiceId);
            paramsObject.put("UnityId", UnityId);
            paramsObject.put("DriverStatus", Status);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebServiceCall(paramsObject, _appURL, new WSResponseListener() {
            @Override
            public void onError(String message) {
                listener.onError(message);
            }
            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                listener.onResponseObject(jsonResponse);
            }
        });
    }
    //endregion

    //region Payment Gateway
    /*Card*/
    public void AssignCardToCustomerInGateway (int UnityId, String GatewayTokenId, String GatewayCostumerId, String GatewaySessionId, String SecurityCode, final WSResponseListener listener) {
        JSONObject paramsObject = new JSONObject();
        try {
            paramsObject.put("AccessPointId", _accessPointId);
            paramsObject.put("Request", "Gateway_AssignCardToCustomer");
            paramsObject.put("UnityServiceId", _unityServiceId);
            paramsObject.put("UnityId", UnityId);
            paramsObject.put("SClientId", SecurityCode);
            paramsObject.put("GatewayTokenId", GatewayTokenId);
            paramsObject.put("GatewayCustomerId", GatewayCostumerId);
            paramsObject.put("GatewaySessionId", GatewaySessionId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebServiceCall(paramsObject, _unityURL, new WSResponseListener() {
            @Override
            public void onError(String message) {
                listener.onError(message);
            }
            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                listener.onResponseObject(jsonResponse);
            }
        });
    }
    public void RemoveCardService (int UnityId, int BankAccountId, int ServiceId, String GatewayCardId, final WSResponseListener listener) {
        JSONObject paramsObject = new JSONObject();
        try {
            paramsObject.put("AccessPointId", _accessPointId);
            paramsObject.put("Request", "Remove_CardForService");
            paramsObject.put("SessionToken", _sessionTokenId);
            paramsObject.put("UnityServiceId", _unityServiceId);
            paramsObject.put("UnityId", UnityId);
            paramsObject.put("BankAccountId", BankAccountId);
            paramsObject.put("GatewayCardId", GatewayCardId);
            paramsObject.put("ServiceId", ServiceId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebServiceCall(paramsObject, _unityURL, new WSResponseListener() {
            @Override
            public void onError(String message) {
                listener.onError(message);
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                listener.onResponseObject(jsonResponse);
            }
        });
    }
    /*Account*/
    public void AssignAccountToCostumerInGateway (int UnityId, String CLABE, String Alias, String HolderName, String GatewayCostumerId, final WSResponseListener listener){
        JSONObject paramsObject = new JSONObject();
        try {
            paramsObject.put("AccessPointId", _accessPointId);
            paramsObject.put("Request", "Gateway_AssignAccountToCustomer");
            paramsObject.put("SessionToken", _sessionTokenId);
            paramsObject.put("UnityServiceId", _unityServiceId);
            paramsObject.put("UnityId", UnityId);
            paramsObject.put("Clabe", CLABE);
            paramsObject.put("Alias", Alias);
            paramsObject.put("HolderName", HolderName);
            paramsObject.put("GatewayCustomerId", GatewayCostumerId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebServiceCall(paramsObject, _unityURL, new WSResponseListener() {
            @Override
            public void onError(String message) {
                listener.onError(message);
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                listener.onResponseObject(jsonResponse);
            }
        });
    }
    public void RemoveAccountFromService (int UnityId, int BankActId, String GatewayCardId, final WSResponseListener listener){
        JSONObject paramsObject = new JSONObject();
        try {
            paramsObject.put("AccessPointId", _accessPointId);
            paramsObject.put("Request", "Remove_AccountForService");
            paramsObject.put("SessionToken", _sessionTokenId);
            paramsObject.put("UnityServiceId", _unityServiceId);
            paramsObject.put("UnityId", UnityId);
            paramsObject.put("BankAccountId", BankActId);
            paramsObject.put("GatewayCardId", GatewayCardId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebServiceCall(paramsObject, _unityURL, new WSResponseListener() {
            @Override
            public void onError(String message) {
                listener.onError(message);
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                listener.onResponseObject(jsonResponse);
            }
        });
    }
    public void GetCardOrAcntForService(int UnityId, int ServiceId, String IsForDeposit, final WSResponseListener listener) {
        JSONObject paramsObject = new JSONObject();
        try {
            paramsObject.put("AccessPointId", _accessPointId);
            paramsObject.put("Request", "Get_CardForService");
            paramsObject.put("SessionToken", _sessionTokenId);
            paramsObject.put("UnityServiceId", _unityServiceId);
            paramsObject.put("UnityId", UnityId);
            paramsObject.put("IsForDeposit", IsForDeposit);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebServiceCall(paramsObject, _unityURL, new WSResponseListener() {
            @Override
            public void onError(String message) {
                listener.onError(message);
            }
            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                listener.onResponseObject(jsonResponse);
            }
        });
    }
    //endregion

    //region Credentials
    public void ValidateCredentials(String UnityEmail, String UnityPassword, final WSResponseListener listener){
        //Parametros
        JSONObject paramsObject = new JSONObject();
        try {
            paramsObject.put("AccessPointId", _accessPointId);
            paramsObject.put("Request", "Validate_User");
            paramsObject.put("User", UnityEmail);
            paramsObject.put("Password", UnityPassword);
            paramsObject.put("UnityServiceId", _unityServiceId);
            paramsObject.put("DeviceFCMId", _deviceFCMId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServiceCall(paramsObject, _loginURL, new WSResponseListener() {
            @Override
            public void onError(String message) {
                listener.onError(message);
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                listener.onResponseObject(jsonResponse);
            }
        });
    }
    public void LogOffUser (int UnityId) {
        JSONObject paramsObject = new JSONObject();
        try {
            paramsObject.put("AccessPointId", _accessPointId);
            paramsObject.put("Request", "Logoff_User");
            paramsObject.put("SessionToken", _sessionTokenId);
            paramsObject.put("UnityServiceId", _unityServiceId);
            paramsObject.put("UnityId", UnityId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebServiceCall(paramsObject, _loginURL, new WSResponseListener() {
            @Override
            public void onError(String message) {

            }
            @Override
            public void onResponseObject(JSONArray jsonResponse) {

            }

        });
    }
    public void ForgotPassword (String UnityEmail) {
        JSONObject paramsObject = new JSONObject();
        try {
            paramsObject.put("AccessPointId", _accessPointId);
            paramsObject.put("Request", "Recover_PasswordRequest");
            paramsObject.put("UnityServiceId", _unityServiceId);
            paramsObject.put("User", UnityEmail);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebServiceCall(paramsObject, _unityURL, new WSResponseListener() {
            @Override
            public void onError(String message) {

            }
            @Override
            public void onResponseObject(JSONArray jsonResponse) {

            }

        });
    }
    public void CreateNewAccount(String UserEmail, String UserPassword, String UserMobile, int CityId, String UserNickName, String UserLastName, String UserGivenName, String UserGender, Bitmap UserProfilePic, final WSResponseListener listener) {
        String base64StringOf_My_Image = "";
        JSONObject paramsObject = new JSONObject();
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            UserProfilePic.compress(Bitmap.CompressFormat.PNG, 70, os);
            byte[] byteArray = os.toByteArray();
            base64StringOf_My_Image = Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (Exception e) {
            Log.d("ERROR", "Base64Image: " + e.toString());
        }
        try {
            paramsObject.put("AccessPointId", _accessPointId);
            paramsObject.put("Request", "Create_ORIUser");
            paramsObject.put("User", UserEmail);
            paramsObject.put("Password", UserPassword);
            paramsObject.put("Mobile", UserMobile);
            paramsObject.put("CityId", CityId);
            paramsObject.put("UserNickName", UserNickName);
            paramsObject.put("UserLastName", UserLastName);
            paramsObject.put("UserGivenName", UserGivenName);
            paramsObject.put("UserGender", UserGender);
            paramsObject.put("UnityServiceId", _unityServiceId);
            paramsObject.put("UserType", "U");
            paramsObject.put("ProfilePic", base64StringOf_My_Image);

        } catch (JSONException e) {
            Log.d("ERROR", "Param: " + e.toString());
        }
        WebServiceCall(paramsObject, _profilePicUpdateURL, new WSResponseListener() {
            @Override
            public void onError(String message) {
                listener.onError(message);
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                listener.onResponseObject(jsonResponse);
            }
        });
    }
    public void UpdateDeviceFCMId(int UnityId) {
        JSONObject paramsObject = new JSONObject();
        try {
            paramsObject.put("AccessPointId", _accessPointId);
            paramsObject.put("Request", "UpdateDriverDeviceFCMId");
            paramsObject.put("SessionToken", _sessionTokenId);
            paramsObject.put("UnityServiceId", _unityServiceId);
            paramsObject.put("UnityId", UnityId);
            paramsObject.put("DeviceFCMId", _deviceFCMId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebServiceCall(paramsObject, _appURL, new WSResponseListener() {
            @Override
            public void onError(String message) {

            }
            @Override
            public void onResponseObject(JSONArray jsonResponse) {

            }

        });
    }
    //endregion

    //region ORI Settings
    public void GetCityIdFromName (String CityName, String CountryName, final WSResponseListener listener) {
        JSONObject paramsObject = new JSONObject();
        try {
            paramsObject.put("AccessPointId", _accessPointId);
            paramsObject.put("Request", "Get_CityId");
            paramsObject.put("UnityServiceId", _unityServiceId);
            paramsObject.put("CityName", CityName);
            paramsObject.put("CountryName", CountryName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebServiceCall(paramsObject, _unityURL, new WSResponseListener() {
            @Override
            public void onError(String message) {
                listener.onError(message);
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                listener.onResponseObject(jsonResponse);
            }

        });
    }
    public void GetAvailableCities(final WSResponseListener listener){
        //Parametros
        JSONObject paramsObject = new JSONObject();
        try {
            paramsObject.put("AccessPointId", _accessPointId);
            paramsObject.put("Request", "Get_Cities");
            paramsObject.put("UnityServiceId", _unityServiceId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServiceCall(paramsObject, _unityURL, new WSResponseListener() {
            @Override
            public void onError(String message) {
                listener.onError(message);
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                listener.onResponseObject(jsonResponse);
            }

        });
    }
    public void GetPaymentTypes (int UnityId, final WSResponseListener listener) {
        JSONObject paramsObject = new JSONObject();
        try {
            paramsObject.put("AccessPointId", _accessPointId);
            paramsObject.put("Request", "Get_PaymentTypes");
            paramsObject.put("SessionToken", _sessionTokenId);
            paramsObject.put("UnityServiceId", _unityServiceId);
            paramsObject.put("UnityId", UnityId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebServiceCall(paramsObject, _appURL, new WSResponseListener() {
            @Override
            public void onError(String message) {
                listener.onError(message);
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                listener.onResponseObject(jsonResponse);
            }

        });
    }
    public void GetServiceTypes (int UnityId, final  WSResponseListener listener) {
        JSONObject paramsObject = new JSONObject();
        try {
            paramsObject.put("AccessPointId", _accessPointId);
            paramsObject.put("Request", "Get_ServiceTypes");
            paramsObject.put("SessionToken", _sessionTokenId);
            paramsObject.put("UnityServiceId", _unityServiceId);
            paramsObject.put("UnityId", UnityId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebServiceCall(paramsObject, _appURL, new WSResponseListener() {
            @Override
            public void onError(String message) {
                listener.onError(message);
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                listener.onResponseObject(jsonResponse);
            }

        });
    }
    public void GetTripHistoryByDriverId(int UnityId, final WSResponseListener listener) {
        JSONObject paramsObject = new JSONObject();
        try {
            paramsObject.put("AccessPointId", _accessPointId);
            paramsObject.put("Request", "Driver_GetTripHistoryByDriverId");
            paramsObject.put("SessionToken", _sessionTokenId);
            paramsObject.put("UnityServiceId", _unityServiceId);
            paramsObject.put("UnityId", UnityId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebServiceCall(paramsObject, _appURL, new WSResponseListener() {
            @Override
            public void onError(String message) {
                listener.onError(message);
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                listener.onResponseObject(jsonResponse);
            }

        });
    }
    public void GetCurrentPaymentPeriod (int UnityId, final WSResponseListener listener) {
        JSONObject paramsObject = new JSONObject();
        try {
            paramsObject.put("AccessPointId", _accessPointId);
            paramsObject.put("Request", "Driver_GetCurrentPaymentPeriod");
            paramsObject.put("SessionToken", _sessionTokenId);
            paramsObject.put("UnityServiceId", _unityServiceId);
            paramsObject.put("UnityId", UnityId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebServiceCall(paramsObject, _appURL, new WSResponseListener() {
            @Override
            public void onError(String message) {
                listener.onError(message);
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                listener.onResponseObject(jsonResponse);
            }

        });
    }
    public void GetDriversLocation (int DriverUnityId, int UnityId, final WSResponseListener listener) {
        JSONObject paramsObject = new JSONObject();
        try {
            paramsObject.put("AccessPointId", _accessPointId);
            paramsObject.put("UnityId", UnityId);
            paramsObject.put("Request", "Passenger_GetDriversLocation");
            paramsObject.put("SessionToken", _sessionTokenId);
            paramsObject.put("UnityServiceId", _unityServiceId);
            paramsObject.put("DriverUnityId", DriverUnityId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebServiceCall(paramsObject, _appURL, new WSResponseListener() {
            @Override
            public void onError(String message) {
                listener.onError(message);
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                listener.onResponseObject(jsonResponse);
            }

        });
    }
    //endregion


    //region Driver Web Service
    public void UpdateDriverLocation(Integer UnityId, String Latitude, String Longitude, String Status, Integer TravelRequestId, Integer TravelRequestStep, final WSResponseListener listener) {
        JSONObject paramsObject = new JSONObject();
        try {
            paramsObject.put("AccessPointId", _accessPointId);
            paramsObject.put("Request", "Driver_UpdateLocation");
            paramsObject.put("SessionToken", _sessionTokenId);
            paramsObject.put("UnityServiceId", _unityServiceId);
            paramsObject.put("Latitud", Latitude);
            paramsObject.put("Longitud", Longitude);
            paramsObject.put("Status", Status);
            paramsObject.put("UnityId", UnityId);

            if (TravelRequestId != null && Status.contains("B")) {
                paramsObject.put("AccessPointId", _accessPointId);
                paramsObject.put("Request", "Driver_UpdateLocation_TravelActivity");
                paramsObject.put("SessionToken", _sessionTokenId);
                paramsObject.put("UnityServiceId", _unityServiceId);
                paramsObject.put("TravelRequestId", TravelRequestId);
                paramsObject.put("Step", TravelRequestStep);
                paramsObject.put("Latitud", Latitude);
                paramsObject.put("Longitud", Longitude);
                paramsObject.put("Status", Status);
                paramsObject.put("UnityId", UnityId);
            }

            if (TravelRequestId != null && Status.contains("P")) {
                paramsObject.put("AccessPointId", _accessPointId);
                paramsObject.put("Request", "Driver_UpdateLocation");
                paramsObject.put("SessionToken", _sessionTokenId);
                paramsObject.put("UnityServiceId", _unityServiceId);
                paramsObject.put("TravelRequestId", TravelRequestId);
                paramsObject.put("Step", TravelRequestStep);
                paramsObject.put("Latitud", Latitude);
                paramsObject.put("Longitud", Longitude);
                paramsObject.put("Status", Status);
                paramsObject.put("UnityId", UnityId);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebServiceCall(paramsObject, _appURL, new WSResponseListener() {
            @Override
            public void onError(String message) {
                listener.onError(message);
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                listener.onResponseObject(jsonResponse);
            }

        });
    }
    public void StartTravelRequest(Integer UnityId, Integer TravelRequestId, final WSResponseListener listener) {
        JSONObject paramsObject = new JSONObject();
        try {
            paramsObject.put("AccessPointId", _accessPointId);
            paramsObject.put("Request", "Driver_StartTravel");
            paramsObject.put("SessionToken", _sessionTokenId);
            paramsObject.put("UnityServiceId", _unityServiceId);
            paramsObject.put("TravelRequestId", TravelRequestId);
            paramsObject.put("UnityId", UnityId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebServiceCall(paramsObject, _appURL, new WSResponseListener() {
            @Override
            public void onError(String message) {
                listener.onError(message);
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                listener.onResponseObject(jsonResponse);
            }

        });
    }
    public void EndTravel(String Latitude, String Longitude, Double TotalDistanceMts, Double TotalTimeMins, Integer TravelRequestId, Integer TravelRequestStep, Integer DriverUnityId, Integer PassengerUnityId, Integer PaymentId, String DSID, Double SubTotal, Double BankFee, final WSResponseListener listener) {
        JSONObject paramsObject = new JSONObject();
        try {
            paramsObject.put("AccessPointId", _accessPointId);
            paramsObject.put("Request", "End_Travel");
            paramsObject.put("SessionToken", _sessionTokenId);
            paramsObject.put("UnityServiceId", _unityServiceId);
            paramsObject.put("TravelRequestId", TravelRequestId);
            paramsObject.put("UnityId", DriverUnityId);
            paramsObject.put("PassengerUnityId", PassengerUnityId);
            paramsObject.put("Latitud", Latitude);
            paramsObject.put("Longitud", Longitude);
            paramsObject.put("SubTotal", SubTotal);
            paramsObject.put("BankFee", BankFee);
            paramsObject.put("PaymentTypeId", PaymentId);
            paramsObject.put("TotalDistance", TotalDistanceMts);
            paramsObject.put("Description", "Tu viaje con ORI");
            paramsObject.put("TotalTime", TotalTimeMins);
            paramsObject.put("Step", TravelRequestStep);
            paramsObject.put("DSID", DSID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebServiceCall(paramsObject, _appURL, new WSResponseListener() {
            @Override
            public void onError(String message) {
                listener.onError(message);
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                listener.onResponseObject(jsonResponse);
            }

        });
    }
    public void RatePassenger(Integer UnityId, Integer TravelRequestId, Integer PassengerUnityId, Double Rate, final WSResponseListener listener) {
        JSONObject paramsObject = new JSONObject();
        try {
            paramsObject.put("AccessPointId", _accessPointId);
            paramsObject.put("Request", "Rate_Passenger");
            paramsObject.put("SessionToken", _sessionTokenId);
            paramsObject.put("UnityServiceId", _unityServiceId);
            paramsObject.put("TravelRequestId", TravelRequestId);
            paramsObject.put("PassengerUnityId", PassengerUnityId);
            paramsObject.put("Rate", Rate);
            paramsObject.put("UnityId", UnityId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void RejectTravelRequest(Integer UnityId, Integer TravelRequestId, final WSResponseListener listener) {
        JSONObject paramsObject = new JSONObject();
        try {
            paramsObject.put("AccessPointId", _accessPointId);
            paramsObject.put("Request", "Driver_RejectTravelRequest");
            paramsObject.put("SessionToken", _sessionTokenId);
            paramsObject.put("UnityServiceId", _unityServiceId);
            paramsObject.put("TravelRequestId", TravelRequestId);
            paramsObject.put("UnityId", UnityId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebServiceCall(paramsObject, _appURL, new WSResponseListener() {
            @Override
            public void onError(String message) {
                listener.onError(message);
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                listener.onResponseObject(jsonResponse);
            }

        });
    }
    public void AcceptTravelRequest(Integer UnityId, Integer TravelRequestId, final WSResponseListener listener) {
        JSONObject paramsObject = new JSONObject();
        try {
            paramsObject.put("AccessPointId", _accessPointId);
            paramsObject.put("Request", "Driver_AcceptTravelRequest");
            paramsObject.put("SessionToken", _sessionTokenId);
            paramsObject.put("UnityServiceId", _unityServiceId);
            paramsObject.put("TravelRequestId", TravelRequestId);
            paramsObject.put("UnityId", UnityId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebServiceCall(paramsObject, _appURL, new WSResponseListener() {
            @Override
            public void onError(String message) {
                listener.onError(message);
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                listener.onResponseObject(jsonResponse);
            }
        });
    }
    //endregion


    //region Membership
    public void GetCurrentMembershipStatus(int UnityId, final WSResponseListener listener){
        JSONObject paramsObject = new JSONObject();
        try {
            paramsObject.put("AccessPointId", _accessPointId);
            paramsObject.put("Request", "Get_DriversMembershipStatus");
            paramsObject.put("SessionToken", _sessionTokenId);
            paramsObject.put("UnityServiceId", _unityServiceId);
            paramsObject.put("UnityId", UnityId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebServiceCall(paramsObject, _appURL, new WSResponseListener() {
            @Override
            public void onError(String message) {
                listener.onError(message);
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                listener.onResponseObject(jsonResponse);
            }

        });
    }
    //endregion


    private void WebServiceCall(JSONObject paramsObject, String URL, final WSResponseListener listener){
        //Llamada al web service
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, paramsObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("SvcConn", "The Complete Response is \t" + response.toString());
                        try{
                            Object intervention = response.get("WSResponse");
                            if (intervention instanceof String) {
                                Log.d("SvcConn", "Is Object");
                                if (intervention.equals("Error")) {
                                    String Msg = response.get("Message").toString();
                                    listener.onError(Msg);
                                    return;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try{
                            Object intervention = response.get("Message");
                            if (intervention instanceof JSONArray) {
                                Log.d("SvcConn", "Is Array");
                                JSONArray Message = response.getJSONArray("Message");
                                listener.onResponseObject(Message);
                            }
                            else if (intervention instanceof JSONObject) {
                                Log.d("SvcConn", "Is Object");
                                JSONObject obj = response.getJSONObject("Message");
                                JSONArray Message = new JSONArray();
                                Message.put(0,obj);
                                listener.onResponseObject(Message);
                            }else{
                                JSONArray Message = new JSONArray();
                                Message.put(0,intervention);
                                listener.onResponseObject(Message);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                                /*
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.d("SvcConn", "RUN");

                                    }
                                }, 3000);*/
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("SvcConn", "Failed with error msg:\t" + error.getMessage());
                Log.d("SvcConn", "Error StackTrace: \t" + error);
                // edited here
                try {
                    byte[] htmlBodyBytes = error.networkResponse.data;
                    Log.e("SvcConn", new String(htmlBodyBytes), error);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                if (error.getMessage() == null){
                    Log.d("SvcConn", "Create User");
                }

                listener.onError(error.toString());
            }
        })
        {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                int respCode = response.statusCode;
                //Log.d("SvcConn", "Response Code is:\t" + respCode);
                //return super.parseNetworkResponse(response);
                try {
                    String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
                    return Response.success(new JSONObject(jsonString),
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException | JSONException e) {
                    return Response.error(new ParseError(e));
                }
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                String credentials = _serviceName + ":" + _servicePassword;
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", auth);
                return headers;
            }
        };

        //No mover
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(60000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getVolleySingleton(context).addToRequestQueue(jsonObjectRequest);
    }
}
