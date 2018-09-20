package com.rochatech.oridriver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.rochatech.webService.*;
import com.rochatech.library.Common;
import com.rochatech.Model.FavoriteDrivers;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.ListViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Settings_FavRequest extends AppCompatActivity {

    connectToService _svcConnection;
    Common obj;
    Integer _UID = 0;
    int pendingRequest = 0;
    int totalaccepted = 0;
    LinearLayoutCompat _NoAvailableRequest, _AvailableRequest;
    ArrayList<FavoriteDrivers> favrequest;
    ListViewCompat pendingReqList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_favrequest_activity);
        obj = new Common(Settings_FavRequest.this);
        _svcConnection = new connectToService(Settings_FavRequest.this, obj.GetSharedPreferencesValue(Settings_FavRequest.this, "SessionToken"));
        obj.ShowLoadingScreen(Settings_FavRequest.this,"Cargando información, por favor espere...");
        _UID = Integer.parseInt(obj.GetSharedPreferencesValue(Settings_FavRequest.this, "UID"));
        _NoAvailableRequest = findViewById(R.id.NoAvailableRequest);
        _AvailableRequest = findViewById(R.id.AvailableFavRequest);
        pendingReqList = findViewById(R.id.PassengersRequestList);
        GetTotalFavDriverPendingRequest();
    }

    //region WebService Call
    private void GetTotalFavDriverPendingRequest() {
        _svcConnection.GetMyFavoriteDriverRequest(_UID, new WSResponseListener() {
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
                    favrequest = FavoriteDrivers.fromJson(jsonResponse);
                    if (favrequest != null) {
                        pendingRequest = favrequest.size();
                        if (pendingRequest > 0) {
                            LoadAllPendingRequset();
                        } else {
                            TotalApprovedFavoriteDriverRequest();
                        }
                    }
                } catch (Exception e) {
                    obj.CloseLoadingScreen();
                    Common.DialogStatusAlert(Settings_FavRequest.this, e.toString(),getResources().getString(R.string.ORIGlobal_webServiceError),"Error");
                }
            }
        });
    }
    private void TotalApprovedFavoriteDriverRequest() {
        _svcConnection.GetTotalApprovedFavoriteDriverRequest(_UID, new WSResponseListener() {
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
                    TextView passengerCount = findViewById(R.id.FavPassengerCount);
                    String msg = "";
                    if (totalaccepted == 0) {
                        msg = totalaccepted + "pasajeros";
                    } else if (totalaccepted == 1) {
                        msg = totalaccepted + "pasajero";
                    } else if (totalaccepted > 1) {
                        msg = totalaccepted + "pasajeros";
                    }
                    passengerCount.setText(msg);
                    DismissAvailableRequest();
                    ShowNoAvailableRequest();
                    obj.CloseLoadingScreen();
                } catch (JSONException e) {
                    obj.CloseLoadingScreen();
                    Common.DialogStatusAlert(Settings_FavRequest.this, e.toString(),getResources().getString(R.string.ORIGlobal_webServiceError),"Error");
                }
            }
        });

    }
    //endregion

    //region MISC
    private void LoadAllPendingRequset() {
        FavPendingRequest pending = new FavPendingRequest();
        pendingReqList.setAdapter(pending);
        DismissNoAvailableRequest();
        ShowAvailableRequest();
        obj.CloseLoadingScreen();
    }
    private void ShowAvailableRequest() {
        _AvailableRequest.setVisibility(View.VISIBLE);
    }
    private void DismissAvailableRequest() {
        _AvailableRequest.setVisibility(View.GONE);
    }
    private void ShowNoAvailableRequest() {
        _NoAvailableRequest.setVisibility(View.VISIBLE);
    }
    private void DismissNoAvailableRequest() {
        _NoAvailableRequest.setVisibility(View.GONE);
    }
    //endregion

    public class FavPendingRequest extends BaseAdapter {

        @Override
        public int getCount() {
            return favrequest.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = getLayoutInflater().inflate(R.layout.template_favdriverpendingrequest, null);
            } else {
                view = convertView;
            }
            ImageView FavPassengerPicture = view.findViewById(R.id.FavPassengerPicture);
            TextView PassengerName = view.findViewById(R.id.PassengerName);
            TextView PassengerDateRequest = view.findViewById(R.id.PassengerDateRequest);
            Button AcceptPassengerRequest = view.findViewById(R.id.AcceptPassengerRequest);
            Button DeclinePassengerRequest = view.findViewById(R.id.DeclinePassengerRequest);

            FavPassengerPicture.setImageBitmap(_svcConnection.GetProfilePictureFromUID(favrequest.get(position).GetPassengerUnityId()));
            PassengerName.setText(favrequest.get(position).GetPassengerNickName());
            String date = Common.getAppStringFullDateFromFullDate(favrequest.get(position).GetAddedOn());
            PassengerDateRequest.setText(date);
            AcceptPassengerRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer passengerUID = favrequest.get(position).GetPassengerUnityId();
                    int a = 0;
                }
            });
            DeclinePassengerRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            return view;
        }
    }
}
