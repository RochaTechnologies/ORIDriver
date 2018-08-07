package com.rochatech.Model;

import com.rochatech.library.Common;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Date;

public class TravelRequests {

    public TravelRequests() { super(); }

    Integer TravelRequestId, ReqByUnityId, PassengerTotalCompleted, ReqPaymentTypeId, ReqServiceTypeId, AcceptedByDrvUnityId, DriverTotalCompleted;
    String ReqGivenName, ReqLastName, ReqNickName, ReqCellPhone, ReqPhoto, PickUpLocationLatitud, PickUpLocationLongitud, PickupAddress, DropOffLatitud, DropOffLongitud, DropOffAddress, ReqPaymentTypeName, ReqServiceTypeName, DrvGivenName, DrvLastName, DrvNickName, DrvPhoto, DrvCellphone, LicensePlate, AutobileBrand, AutomobileModel, AutomobileColor, AutomobileYear;
    Date PassengerSetupDate, CreationDate, DriverSetupDate, AttendedDate, EndedDate;
    Double PassengerRate, DriverRate, EstimatedDistance, EstimatedTime, EstimatedFare, TotalDistance, TotalTime, TotalFare;
    String ReqStatus;

    //region Set
    public void SetTravelRequestId(Integer TravelRequestId) {
        this.TravelRequestId = TravelRequestId;
    }
    public void SetReqByUnityId(int ReqByUnityId) {
        this.ReqByUnityId = ReqByUnityId;
    }
    public void SetPassengerTotalCompleted(Integer PassengerTotalCompleted) {
        this.PassengerTotalCompleted = PassengerTotalCompleted;
    }
    public void SetReqPaymentTypeId(int ReqPaymentTypeId) {
        this.ReqPaymentTypeId = ReqPaymentTypeId;
    }
    public void SetReqServiceTypeId(int ReqServiceTypeId) {
        this.ReqServiceTypeId = ReqServiceTypeId;
    }
    public void SetAcceptedByDrvUnityId(Integer AcceptedByDrvUnityId) {
        this.AcceptedByDrvUnityId = AcceptedByDrvUnityId;
    }
    public void SetDriverTotalCompleted(Integer DriverTotalCompleted) {
        this.DriverTotalCompleted = DriverTotalCompleted;
    }
    public void SetReqGivenName(String ReqGivenName) {
        this.ReqGivenName = ReqGivenName;
    }
    public void SetReqLastName(String ReqLastName) {
        this.ReqLastName = ReqLastName;
    }
    public void SetReqNickName(String ReqNickName) {
        this.ReqNickName = ReqNickName;
    }
    public void SetReqCellPhone(String ReqCellPhone) {
        this.ReqCellPhone = ReqCellPhone;
    }
    public void SetReqPhoto(String ReqPhoto) {
        this.ReqPhoto = ReqPhoto;
    }
    public void SetPickUpLocationLatitud(String PickUpLocationLatitud) {
        this.PickUpLocationLatitud = PickUpLocationLatitud;
    }
    public void SetPickUpLocationLongitud(String PickUpLocationLongitud) {
        this.PickUpLocationLongitud = PickUpLocationLongitud;
    }
    public void SetPickupAddress(String PickupAddress) {
        this.PickupAddress = PickupAddress;
    }
    public void SetDropOffLatitud(String DropOffLatitud) {
        this.DropOffLatitud = DropOffLatitud;
    }
    public void SetDropOffLongitud(String DropOfLongitud) {
        this.DropOffLongitud = DropOfLongitud;
    }
    public void SetDropOffAddress(String DropOffAddress) {
        this.DropOffAddress = DropOffAddress;
    }
    public void SetReqPaymentTypeName(String ReqPaymentTypeName) {
        this.ReqPaymentTypeName = ReqPaymentTypeName;
    }
    public void SetReqServiceTypeName(String ReqServiceTypeName) {
        this.ReqServiceTypeName = ReqServiceTypeName;
    }
    public void SetDrvGivenName(String DrvGivenName) {
        this.DrvGivenName = DrvGivenName;
    }
    public void SetDrvLastName(String DrvLastName) {
        this.DrvLastName = DrvLastName;
    }
    public void SetDrvNickName(String DrvNickName) {
        this.DrvNickName = DrvNickName;
    }
    public void SetDrvPhoto(String DrvPhoto) {
        this.DrvPhoto = DrvPhoto;
    }
    public void SetDrvCellphone(String DrvCellphone) {
        this.DrvCellphone = DrvCellphone;
    }
    public void SetLicensePlate(String LicensePlate) {
        this.LicensePlate = LicensePlate;
    }
    public void SetAutobileBrand(String AutobileBrand) {
        this.AutobileBrand = AutobileBrand;
    }
    public void SetAutomobileModel(String AutomobileModel) {
        this.AutomobileModel = AutomobileModel;
    }
    public void SetAutomobileColor(String AutomobileColor) {
        this.AutomobileColor = AutomobileColor;
    }
    public void SetAutomobileYear(String AutomobileYear) {
        this.AutomobileYear = AutomobileYear;
    }
    public void SetPassengerSetupDate(Date PassengerSetupDate) {
        this.PassengerSetupDate = PassengerSetupDate;
    }
    public void SetCreationDate(Date CreationDate) {
        this.CreationDate = CreationDate;
    }
    public void SetDriverSetupDate(Date DriverSetupDate) {
        this.DriverSetupDate = DriverSetupDate;
    }
    public void SetAttendedDate(Date AttendedDate) {
        this.AttendedDate = AttendedDate;
    }
    public void SetEndedDate(Date EndedDate) {
        this.EndedDate = EndedDate;
    }
    public void SetPassengerRate(Double PassengerRate) {
        this.PassengerRate = PassengerRate;
    }
    public void SetDriverRate(Double DriverRate) {
        this.DriverRate = DriverRate;
    }
    public void SetEstimatedDistance(Double EstimatedDistance) {
        this.EstimatedDistance = EstimatedDistance;
    }
    public void SetEstimatedTime(Double EstimatedTime) {
        this.EstimatedTime = EstimatedTime;
    }
    public void SetEstimatedFare(Double EstimatedFare) {
        this.EstimatedFare = EstimatedFare;
    }
    public void SetTotalDistance(Double TotalDistance) {
        this.TotalDistance = TotalDistance;
    }
    public void SetTotalTime(Double TotalTime) {
        this.TotalTime = TotalTime;
    }
    public void SetTotalFare(Double TotalFare) {
        this.TotalFare = TotalFare;
    }
    public void SetReqStatus(String ReqStatus) {this.ReqStatus = ReqStatus;}
    //endregion

    //region Get
    public Integer GetTravelRequestId() {
        return TravelRequestId;
    }
    public Integer GetReqByUnityId() {
        return ReqByUnityId;
    }
    public Integer GetPassengerTotalCompleted() {
        return PassengerTotalCompleted;
    }
    public Integer GetReqPaymentTypeId() {
        return ReqPaymentTypeId;
    }
    public Integer GetReqServiceTypeId() {
        return ReqServiceTypeId;
    }
    public Integer GetAcceptedByDrvUnityId() {
        return AcceptedByDrvUnityId;
    }
    public Integer GetDriverTotalCompleted() {
        return DriverTotalCompleted;
    }
    public String GetReqGivenName() {
        return ReqGivenName;
    }
    public String GetReqLastName() {
        return ReqLastName;
    }
    public String GetReqNickName() {
        return ReqNickName;
    }
    public String GetReqCellPhone() {
        return ReqCellPhone;
    }
    public String GetReqPhoto() {
        return ReqPhoto;
    }
    public String GetPickUpLocationLatitud() {
        return PickUpLocationLatitud;
    }
    public String GetPickUpLocationLongitud() {
        return PickUpLocationLongitud;
    }
    public String GetPickupAddress() {
        return PickupAddress;
    }
    public String GetDropOffLatitud() {
        return DropOffLatitud;
    }
    public String GetDropOffLongitud() {
        return DropOffLongitud;
    }
    public String GetDropOffAddress() {
        return DropOffAddress;
    }
    public String GetReqPaymentTypeName() {
        return ReqPaymentTypeName;
    }
    public String GetReqServiceTypeName() {return ReqServiceTypeName;}
    public String GetDrvGivenName() {
        return DrvGivenName;
    }
    public String GetDrvLastName() {
        return DrvLastName;
    }
    public String GetDrvNickName() {
        return DrvNickName;
    }
    public String GetDrvPhoto() {
        return DrvPhoto;
    }
    public String GetDrvCellphone() {
        return DrvCellphone;
    }
    public String GetLicensePlate() {
        return LicensePlate;
    }
    public String GetAutobileBrand() {
        return AutobileBrand;
    }
    public String GetAutomobileModel() {
        return AutomobileModel;
    }
    public String GetAutomobileColor() {
        return AutomobileColor;
    }
    public String GetAutomobileYear() {return AutomobileYear;}
    public Date GetPassengerSetupDate() {
        return PassengerSetupDate;
    }
    public Date GetCreationDate() {
        return CreationDate;
    }
    public Date GetDriverSetupDate() {
        return DriverSetupDate;
    }
    public Date GetAttendedDate() {
        return AttendedDate;
    }
    public Date GetEndedDate() {
        return EndedDate;
    }
    public Double GetPassengerRate() {
        return PassengerRate;
    }
    public Double GetDriverRate() {
        return DriverRate;
    }
    public Double GetEstimatedDistance() {
        return EstimatedDistance;
    }
    public Double GetEstimatedTime() {
        return EstimatedTime;
    }
    public Double GetEstimatedFare() {
        return EstimatedFare;
    }
    public Double GetTotalDistance() {
        return TotalDistance;
    }
    public Double GetTotalTime() {
        return TotalTime;
    }
    public Double GetTotalFare() {
        return TotalFare;
    }
    public String GetReqStatus() {return ReqStatus;}
    //endregion

    public JSONObject toJSON(){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("TravelRequestId", GetTravelRequestId());
            jsonObject.put("ReqByUnityId", GetReqByUnityId());
            jsonObject.put("ReqGivenName", GetReqGivenName());
            jsonObject.put("ReqLastName", GetReqLastName());
            jsonObject.put("ReqNickName", GetReqNickName());
            jsonObject.put("ReqCellPhone", GetReqCellPhone());
            jsonObject.put("ReqPhoto", GetReqPhoto());
            jsonObject.put("PassengerTotalCompleted", GetPassengerTotalCompleted());
            jsonObject.put("PassengerSetupDate", Common.getAppStringFullDateFromFullDate(GetPassengerSetupDate()));
            jsonObject.put("PassengerRate", GetPassengerRate());
            jsonObject.put("FromLatitud", GetPickUpLocationLatitud());
            jsonObject.put("FromLongitud", GetPickUpLocationLongitud());
            jsonObject.put("FromAddress", GetPickupAddress());
            jsonObject.put("ToLatitud", GetDropOffLatitud());
            jsonObject.put("ToLongitud", GetDropOffLongitud());
            jsonObject.put("ToAddress", GetDropOffAddress());
            jsonObject.put("ReqPaymentTypeId", GetReqPaymentTypeId());
            jsonObject.put("ReqPaymentTypeName", GetReqPaymentTypeName());
            jsonObject.put("ReqServiceTypeId", GetReqServiceTypeId());
            jsonObject.put("ReqServiceTypeName", GetReqServiceTypeName());
            jsonObject.put("CreationDate", Common.getAppStringFullDateFromFullDate(GetCreationDate()));
            jsonObject.put("ReqStatus", GetReqStatus());
            jsonObject.put("AcceptedByUnityId", GetAcceptedByDrvUnityId());
            jsonObject.put("DriverGivenName", GetDrvGivenName());
            jsonObject.put("DriverLastName", GetDrvLastName());
            jsonObject.put("DriverNickName", GetDrvNickName());
            jsonObject.put("DriverPhoto", GetDrvPhoto());
            jsonObject.put("DriverTotalCompleted", GetDriverTotalCompleted());
            jsonObject.put("DriverRate", GetDriverRate());
            jsonObject.put("DriverSetupDate", Common.getAppStringFullDateFromFullDate(GetDriverSetupDate()));
            jsonObject.put("DriverCellPhone", GetDrvCellphone());
            jsonObject.put("LicensePlateNumber", GetLicensePlate());
            jsonObject.put("VehicleMake", GetAutobileBrand());
            jsonObject.put("VehicleModel", GetAutomobileModel());
            jsonObject.put("VehicleColor", GetAutomobileColor());
            jsonObject.put("VehicleYear", GetAutomobileYear());
            jsonObject.put("AttendedDate", Common.getAppStringFullDateFromFullDate(GetAttendedDate()));
            jsonObject.put("EndedDate", Common.getAppStringFullDateFromFullDate(GetEndedDate()));
            jsonObject.put("EstimatedDistance", GetEstimatedDistance());
            jsonObject.put("EstimatedTime", GetEstimatedTime());
            jsonObject.put("EstimatedFare", GetEstimatedFare());
            jsonObject.put("TotalDistance", GetTotalDistance());
            jsonObject.put("TotalTime", GetTotalTime());
            jsonObject.put("TotalFare", GetTotalFare());
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static ArrayList<TravelRequests> fromJson(JSONArray jsonArray) {
        JSONObject jsonObject;
        ArrayList<TravelRequests> trvlReq = new ArrayList<>(jsonArray.length());
        try {
            for (int i=0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                try {
                    TravelRequests AC = new TravelRequests();
                    AC.TravelRequestId = jsonObject.getString("TravelRequestId").equals("") ? null : Integer.parseInt(jsonObject.getString("TravelRequestId"));
                    AC.ReqByUnityId = jsonObject.getString("ReqByUnityId").equals("") ? null : Integer.parseInt(jsonObject.getString("ReqByUnityId"));
                    AC.ReqGivenName = jsonObject.getString("ReqGivenName");
                    AC.ReqLastName = jsonObject.getString("ReqLastName");
                    AC.ReqNickName = jsonObject.getString("ReqNickName");
                    AC.ReqCellPhone = jsonObject.getString("ReqCellPhone");
                    AC.ReqPhoto = jsonObject.getString("ReqPhoto");
                    AC.PassengerTotalCompleted = jsonObject.getString("PassengerTotalCompleted").equals("") ? null : Integer.parseInt(jsonObject.getString("PassengerTotalCompleted"));
                    AC.PassengerRate = jsonObject.getString("PassengerRate").equals("") ? null : Double.parseDouble(jsonObject.getString("PassengerRate"));
                    AC.PassengerSetupDate = Common.getAppFullDateFromAppStringFullDate(jsonObject.getString("PassengerSetupDate"));
                    AC.PickUpLocationLatitud = jsonObject.getString("FromLatitud");
                    AC.PickUpLocationLongitud = jsonObject.getString("FromLongitud");
                    AC.PickupAddress = jsonObject.getString("FromAddress");
                    AC.DropOffLatitud = jsonObject.getString("ToLatitud");
                    AC.DropOffLongitud = jsonObject.getString("ToLongitud");
                    AC.DropOffAddress = jsonObject.getString("ToAddress");
                    AC.ReqPaymentTypeId = jsonObject.getString("ReqPaymentTypeId").equals("") ? null : Integer.parseInt(jsonObject.getString("ReqPaymentTypeId"));
                    AC.ReqPaymentTypeName = jsonObject.getString("ReqPaymentTypeName");
                    AC.ReqServiceTypeId = jsonObject.getString("ReqServiceTypeId").equals("") ? null : Integer.parseInt(jsonObject.getString("ReqServiceTypeId"));
                    AC.ReqServiceTypeName = jsonObject.getString("ReqServiceTypeName");
                    AC.CreationDate = Common.getAppFullDateFromAppStringFullDate(jsonObject.getString("CreationDate"));
                    AC.ReqStatus = jsonObject.getString("ReqStatus");
                    AC.AcceptedByDrvUnityId = jsonObject.getString("AcceptedByUnityId").equals("") ? null : Integer.parseInt(jsonObject.getString("AcceptedByUnityId"));
                    AC.DrvGivenName = jsonObject.getString("DriverGivenName");
                    AC.DrvLastName = jsonObject.getString("DriverLastName");
                    AC.DrvNickName = jsonObject.getString("DriverNickName");
                    AC.DrvPhoto = jsonObject.getString("DriverPhoto");
                    AC.DriverTotalCompleted = jsonObject.getString("DriverTotalCompleted").equals("") ? null : Integer.parseInt(jsonObject.getString("DriverTotalCompleted"));
                    AC.DriverRate = jsonObject.getString("DriverRate").equals("") ? null : Double.parseDouble(jsonObject.getString("DriverRate"));
                    AC.DriverSetupDate = Common.getAppFullDateFromAppStringFullDate(jsonObject.getString("DriverSetupDate"));
                    AC.DrvCellphone = jsonObject.getString("DriverCellPhone");
                    AC.LicensePlate = jsonObject.getString("LicensePlateNumber");
                    AC.AutobileBrand = jsonObject.getString("VehicleMake");
                    AC.AutomobileModel = jsonObject.getString("VehicleModel");
                    AC.AutomobileColor = jsonObject.getString("VehicleColor");
                    AC.AutomobileYear = jsonObject.getString("VehicleYear");
                    AC.AttendedDate = Common.getAppFullDateFromAppStringFullDate(jsonObject.getString("AttendedDate"));
                    AC.EndedDate = Common.getAppFullDateFromAppStringFullDate(jsonObject.getString("EndedDate"));
                    AC.EstimatedDistance = jsonObject.getString("EstimatedDistance").equals("") ? null : Double.parseDouble(jsonObject.getString("EstimatedDistance"));
                    AC.EstimatedTime = jsonObject.getString("EstimatedTime").equals("") ? null : Double.parseDouble(jsonObject.getString("EstimatedTime"));
                    AC.EstimatedFare = jsonObject.getString("EstimatedFare").equals("") ? null : Double.parseDouble(jsonObject.getString("EstimatedFare"));
                    String algo = jsonObject.getString("TotalDistance");
                    if (jsonObject.getString("TotalDistance").trim().isEmpty() || jsonObject.getString("TotalDistance").contains("null") || jsonObject.getString("TotalDistance") == null) {
                        AC.TotalDistance = 0.0;
                    } else {
                        AC.TotalDistance = Double.parseDouble(jsonObject.getString("TotalDistance"));
                    }
                    if (jsonObject.getString("TotalTime").trim().isEmpty() || jsonObject.getString("TotalTime").contains("null") || jsonObject.getString("TotalTime") == null) {
                        AC.TotalTime = 0.0;
                    } else {
                        AC.TotalTime = Double.parseDouble(jsonObject.getString("TotalTime"));
                    }
                    if (jsonObject.getString("TotalFare").trim().isEmpty() || jsonObject.getString("TotalFare").contains("null") || jsonObject.getString("TotalFare") == null) {
                        AC.TotalFare = 0.0;
                    } else {
                        AC.TotalFare = Double.parseDouble(jsonObject.getString("TotalFare"));
                    }
                    trvlReq.add(AC);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        // Return new object
        return trvlReq;
    }
}