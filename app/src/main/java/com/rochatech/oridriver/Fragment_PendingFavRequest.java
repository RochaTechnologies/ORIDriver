package com.rochatech.oridriver;

import android.app.AlertDialog;
//import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.ListViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rochatech.webService.*;
import com.rochatech.library.Common;
import com.rochatech.Model.FavoriteDrivers;

import org.json.JSONArray;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class Fragment_PendingFavRequest extends Fragment {

    connectToService _svcConnection;
    Common obj;

    ArrayList<FavoriteDrivers> favpending;
    ListViewCompat pendinglist;
    int UID = 0;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pendingfavrequest, container, false);
        obj = new Common(v.getContext());
        _svcConnection = new connectToService(v.getContext(), obj.GetSharedPreferencesValue(v.getContext(), "SessionToken"));
        pendinglist = v.findViewById(R.id.PassengersRequestList);
        UID = Integer.parseInt(obj.GetSharedPreferencesValue(v.getContext(), "UID"));
        LoadPendingRequest(UID, v, container);
        return v;
    }

    private void LoadPendingRequest(int UID, final View view, final ViewGroup container) {
        _svcConnection.GetMyFavoriteDriverRequest(UID, new WSResponseListener() {
            @Override
            public void onError(String message) {
                switch (message) {
                    case "Error_InvalidToken":
                        Common.LogoffByInvalidToken(getContext());
                        break;
                    case "NO_CONNECTION":
                        Common.DialogStatusAlert(getContext(),getResources().getString(R.string.ORI_NoInternetConnection_Msg),getResources().getString(R.string.ORI_NoInternetConnection_Title),"Error");
                        break;
                }
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                favpending = FavoriteDrivers.fromJson(jsonResponse);
                FavPendingRequest pendingobj = new FavPendingRequest(container);
                pendinglist.setAdapter(pendingobj);
            }
        });
    }

    private void LogoffUser(Common obj, View view) {
        int UID = Integer.parseInt(obj.GetSharedPreferencesValue(view.getContext(), "UID"));
        String sessionToken = obj.GetSharedPreferencesValue(view.getContext(), "SessionToken");
        _svcConnection.LogOffUser(UID);
        Common.DeleteAllSharedPreferences(view.getContext());
        final Intent intent = new Intent(view.getContext(), Wizard_Login.class);
        AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
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

    public class FavPendingRequest extends BaseAdapter {

        ViewGroup context;

        FavPendingRequest(ViewGroup context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return favpending.size();
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
        public View getView(int position, View convertView, ViewGroup parent) {
//            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v;
            if (convertView == null) {
//                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_favdriverpendingrequest, null);
//                parent.getContext(),----------
                v = getLayoutInflater().inflate(R.layout.template_favdriverpendingrequest, null);
//                v = getLayoutInflater().inflate(R.layout.template_favdriverpendingrequest, null);
//                v = inflater.inflate(R.layout.template_favdriverpendingrequest, null);
            } else {
                v = convertView;
            }
            TextView passName = v.findViewById(R.id.PassengerName);
            TextView passReqDate = v.findViewById(R.id.PassengerDateRequest);
            ImageView passPic = v.findViewById(R.id.DriverProfilePic);
            passName.setText(favpending.get(position).GetPassengerNickName());
            passReqDate.setText(Common.getAppStringFullDateFromFullDate(favpending.get(position).GetAddedOn()));
            /*passpic*/
            return v;
        }
    }

}
