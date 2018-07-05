package com.rochatech.oridriver;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.ListViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.rochatech.Model.TripHistory;
import com.rochatech.library.Common;
import com.rochatech.webService.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Settings_TripHistory extends AppCompatActivity {

    connectToService _svcConnection;
    Common obj;

    ArrayList<TripHistory> trips;

    TextView driverPeriodTxt, driverTotalEarnedTxt, driverTotalTripTxt, driverGrandTotalEarnedTxt, driverGrandTotalTripTxt;
    ListViewCompat tripHistoryList;
    LinearLayoutCompat infoContent, noInfoContent;

    int UID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_triphistory_activity);
        obj = new Common(Settings_TripHistory.this);
        _svcConnection = new connectToService(Settings_TripHistory.this, obj.GetSharedPreferencesValue(Settings_TripHistory.this, "SessionToken"));
        obj.ShowLoadingScreen(Settings_TripHistory.this,"Estamos cargando su información, por favor espere...");
        infoContent = findViewById(R.id.infoContent);
        noInfoContent = findViewById(R.id.noInfoContent);
        UID = Integer.parseInt(obj.GetSharedPreferencesValue(Settings_TripHistory.this,"UID"));
        InitAppControls();
        LoadDriverHeaderTotals(UID);
    }


    //region WebService Call
    private void LoadDriverHeaderTotals(final int UID) {
        _svcConnection.GetTripHistoryByDriverId(UID, new WSResponseListener() {
            @Override
            public void onError(String message) {
                switch (message) {
                    case "Error_InvalidToken":
                        LogoffUser(obj);
                        break;
                }
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                trips = TripHistory.fromJson(jsonResponse);
                if (trips.size() == 0) {
                    noInfoContent.setVisibility(View.VISIBLE);
                    infoContent.setVisibility(View.GONE);
                    obj.CloseLoadingScreen();
                } else {
                    noInfoContent.setVisibility(View.GONE);
                    infoContent.setVisibility(View.VISIBLE);
                    LoadDriverCurrentPaymentPeriod(UID);
                }
//                double TotalEarned = 0;
//                int TotalTrips = trips.size();
//                for (int i = 0; i < trips.size(); i++) {
//                    TotalEarned = TotalEarned + trips.get(i).getTotal();
//                }
//                DecimalFormat formatter = new DecimalFormat("#,###.00");
//                driverTotalEarnedTxt.setText("$ " + Common.DoubleFormatted(TotalEarned));
//                driverTotalTripTxt.setText(Integer.toString(TotalTrips) + " Viajes");
//                LoadDriverDetailTrips();
            }
        });
    }

    private void LoadDriverCurrentPaymentPeriod(int UID) {
        _svcConnection.GetCurrentPaymentPeriod(UID, new WSResponseListener() {
            @Override
            public void onError(String message) {
                switch (message) {
                    case "Error_InvalidToken":
                        Common.LogoffByInvalidToken(Settings_TripHistory.this);
                        break;
                }
            }

            @Override
            public void onResponseObject(JSONArray jsonResponse) {
                try {
                    JSONObject response = jsonResponse.getJSONObject(0);
                    String status = response.getString("Status");
                    String FromDate = "";
                    String ToDate = "";
                    String CurrentWeekDay = "";
                    String StartingDateWeekNum = "";
                    String PeriodName = "";
                    switch (status) {
                        case "OK":
                            FromDate = response.getString("FromDate");
                            ToDate = response.getString("ToDate");
                            CurrentWeekDay = response.getString("CurrentWeekDay");
                            StartingDateWeekNum = response.getString("StartingDateWeekNum");
                            PeriodName = response.getString("PeriodName");
                            break;
                    }
                    String msg = "Periodo ( " + FromDate + " al " + ToDate + " )";
                    driverPeriodTxt.setText(msg);
                    LoadTotals();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void LoadTotals() {
        int periodTrips = 0;
        double periodAmount = 0;
        int historyTrips = 0;
        double historyAmount = 0;
        for (int i = 0; i < trips.size(); i++) {
            if (trips.get(i).getPaidToDriver()) {
                historyTrips = historyTrips + 1;
                historyAmount = historyAmount + trips.get(i).getTotalFare();
            } else {
                periodTrips = periodTrips + 1;
                periodAmount = periodAmount + trips.get(i).getTotalFare();
            }
        }
        driverTotalEarnedTxt.setText(Double.toString(periodAmount));
        driverTotalTripTxt.setText(Integer.toString(periodTrips) + " viajes");
        driverGrandTotalEarnedTxt.setText("$ " + Double.toString(historyAmount));
        driverGrandTotalTripTxt.setText(Integer.toString(historyTrips));
        LoadDriverDetailTrips();
    }

    private void LoadDriverDetailTrips() {
        TripHistoryDetails details = new TripHistoryDetails();
        tripHistoryList.setAdapter(details);
        obj.CloseLoadingScreen();
    }
    //endregion

    //region MISC
    private void InitAppControls() {
        driverPeriodTxt = findViewById(R.id.DriverPeriod);
        driverTotalEarnedTxt = findViewById(R.id.DriverTotalEarned);
        driverTotalTripTxt = findViewById(R.id.DriverTotalTrip);
        driverGrandTotalEarnedTxt = findViewById(R.id.DriverGrandTotalEarned);
        driverGrandTotalTripTxt = findViewById(R.id.DriverGrandTotalTrip);
        tripHistoryList = findViewById(R.id.TripHistoryList);
    }
    //endregion

    private void LogoffUser(Common obj) {
        int UID = Integer.parseInt(obj.GetSharedPreferencesValue(Settings_TripHistory.this, "UID"));
        String sessionToken = obj.GetSharedPreferencesValue(Settings_TripHistory.this, "SessionToken");
        _svcConnection.LogOffUser(UID);
        Common.DeleteAllSharedPreferences(Settings_TripHistory.this);
        final Intent intent = new Intent(Settings_TripHistory.this, Wizard_Login.class);
        AlertDialog.Builder dialog = new AlertDialog.Builder(Settings_TripHistory.this);
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


    class TripHistoryDetails extends BaseAdapter {

        @Override
        public int getCount() {
            return trips.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = getLayoutInflater().inflate(R.layout.template_historytripdetail, null);
            } else {
                view = convertView;
            }
            TextView dayTrip = view.findViewById(R.id.DayTrip);
            TextView monthTrip = view.findViewById(R.id.MonthTrip);
            TextView timeTrip = view.findViewById(R.id.TimeTrip);
            TextView fromTrip = view.findViewById(R.id.FromTrip);
            TextView toTrip = view.findViewById(R.id.ToTrip);
            TextView lenghTrip = view.findViewById(R.id.LengthTrip);
            TextView chargeTrip = view.findViewById(R.id.ChargeTrip);
            dayTrip.setText(Common.getDateComp("Day", trips.get(position).getAttendedDate()));
            monthTrip.setText(Common.getDateComp("Month", trips.get(position).getAttendedDate()));
            timeTrip.setText(Common.getDateComp("Time", trips.get(position).getAttendedDate()));
            fromTrip.setText("De: " + trips.get(position).getFromAddress());
            toTrip.setText("A: " + trips.get(position).getToAddress());
            String distance = new DecimalFormat("##.#").format(trips.get(position).getTotalDistance());
            String duration = new DecimalFormat("##.#").format(trips.get(position).getTotalTime());
            lenghTrip.setText(distance + "Km" + " - " + duration + "mins");
            Double total = trips.get(position).getTotal();
            chargeTrip.setText("$" + Double.toString(total));
            return view;
        }
    }
}
