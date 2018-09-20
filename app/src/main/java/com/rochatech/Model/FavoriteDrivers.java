package com.rochatech.Model;

/*
 *
 *  * Created by ProfJosh on 4/23/18 6:46 PM
 *  * Rocha Technologies de Mexico SA de CV
 *  * soporte@rochatech.com
 *
 */

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import com.rochatech.library.Common;
@SuppressWarnings({"WeakerAccess","unused"})
public class FavoriteDrivers {

    private int FavoriteDriverId, PassengerUnityId, DriverUnityId;
    private String PassengerNickName, PassengerPhoto, RequestStatus;
    private Date AddedOn;

    //region Set
    public void SetFavoriteDriverId (int FavoriteDriverId) {
        this.FavoriteDriverId = FavoriteDriverId;
    }
    public void SetPassengerUnityId (int PassengerUnityId) {
        this.PassengerUnityId = PassengerUnityId;
    }
    public void SetDriverUnityId (int DriverUnityId) {
        this.DriverUnityId = DriverUnityId;
    }
    public void SetPassengerNickName (String PassengerNickName) {
        this.PassengerNickName = PassengerNickName;
    }
    public void SetPassengerPhoto (String PassengerPhoto) {
        this.PassengerPhoto = PassengerPhoto;
    }
    public void SetRequestStatus (String RequestStatus) {
        this.RequestStatus = RequestStatus;
    }
    public void SetAddedOn (Date AddedOn) {
        this.AddedOn = AddedOn;
    }
    //endregion

    //region Get
    public int GetFavoriteDriverId () {
        return FavoriteDriverId;
    }
    public int GetPassengerUnityId () {
        return PassengerUnityId;
    }
    public int GetDriverUnityId () {
        return DriverUnityId;
    }
    public String GetPassengerNickName () {
        return PassengerNickName;
    }
    public String GetPassengerPhoto () {
        return PassengerPhoto;
    }
    public String GetRequestStatus () {
        return RequestStatus;
    }
    public Date GetAddedOn () {
        return AddedOn;
    }
    //endregion

    public static ArrayList<FavoriteDrivers> fromJson (JSONArray jsonArray) {
        JSONObject jsonObject;
        ArrayList<FavoriteDrivers> favDriver = new ArrayList<>(jsonArray.length());
        for (int i =0; i < jsonArray.length(); i++){
            try {
                jsonObject = jsonArray.getJSONObject(i);
                FavoriteDrivers FD = new FavoriteDrivers();
                FD.FavoriteDriverId = Integer.parseInt(jsonObject.getString("Id"));
                FD.PassengerUnityId = Integer.parseInt(jsonObject.getString("PassengerUnityId"));
                FD.PassengerNickName = jsonObject.getString("NickName");
                FD.PassengerPhoto = jsonObject.getString("PhotoFilePath");
                FD.DriverUnityId = Integer.parseInt(jsonObject.getString("DriverUnityId"));
                FD.RequestStatus = jsonObject.getString("RequestStatus");
                FD.AddedOn = Common.getAppFullDateFromAppStringFullDate(jsonObject.getString("AddedOn"));
                favDriver.add(FD);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        return favDriver;
    }
}
