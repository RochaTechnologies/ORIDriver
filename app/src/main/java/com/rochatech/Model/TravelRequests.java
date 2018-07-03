package com.rochatech.Model;

import com.rochatech.library.Common;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Date;

public class TravelRequests {
    public TravelRequests() { super(); }

    int TravelRequestId, ReqByUnityId, PassengerTotalCompleted, ReqPaymentTypeId, ReqServiceTypeId, AcceptedByDrvUnityId, DriverTotalCompleted;
    String ReqGivenName, ReqLastName, ReqNickName, ReqCellPhone, ReqPhoto, PickUpLocationLatitud, PickUpLocationLongitud, PickupAddress, DropOffLatitud, DropOffLongitud, DropOffAddress, ReqPaymentTypeName, ReqServiceTypeName, DrvGivenName, DrvLastName, DrvNickName, DrvPhoto, DrvCellphone, LicensePlate, AutobileBrand, AutomobileModel, AutomobileColor;
    Date PassengerSetupDate, CreationDate, DriverSetupDate, AttendedDate, EndedDate;
    Double PassengerRate, DriverRate, EstimatedDistance, EstimatedTime, EstimatedFare, TotalDistance, TotalTime, TotalFare;

    //region Set
    public void SetTravelRequestId(int TravelRequestId) {
        this.TravelRequestId = TravelRequestId;
    }
    public void SetReqByUnityId(int ReqByUnityId) {
        this.ReqByUnityId = ReqByUnityId;
    }
    public void SetPassengerTotalCompleted(int PassengerTotalCompleted) {
        this.PassengerTotalCompleted = PassengerTotalCompleted;
    }
    public void SetReqPaymentTypeId(int ReqPaymentTypeId) {
        this.ReqPaymentTypeId = ReqPaymentTypeId;
    }
    public void SetReqServiceTypeId(int ReqServiceTypeId) {
        this.ReqServiceTypeId = ReqServiceTypeId;
    }
    public void SetAcceptedByDrvUnityId(int AcceptedByDrvUnityId) {
        this.AcceptedByDrvUnityId = AcceptedByDrvUnityId;
    }
    public void SetDriverTotalCompleted(int DriverTotalCompleted) {
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
    //endregion

    //region Get
    public int GetTravelRequestId() {
        return TravelRequestId;
    }
    public int GetReqByUnityId() {
        return ReqByUnityId;
    }
    public int GetPassengerTotalCompleted() {
        return PassengerTotalCompleted;
    }
    public int GetReqPaymentTypeId() {
        return ReqPaymentTypeId;
    }
    public int GetReqServiceTypeId() {
        return ReqServiceTypeId;
    }
    public int GetAcceptedByDrvUnityId() {
        return AcceptedByDrvUnityId;
    }
    public int GetDriverTotalCompleted() {
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
    //endregion

    public static ArrayList<TravelRequests> fromJson(JSONArray jsonArray) {
        JSONObject jsonObject;
        ArrayList<TravelRequests> travelRequest = new ArrayList<>(jsonArray.length());
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                try {
                    TravelRequests TR = new TravelRequests();
                    TR.TravelRequestId = jsonObject.getInt("TravelRequestId");
                    TR.ReqByUnityId = jsonObject.getInt("ReqByUnityId");
                    TR.ReqGivenName = jsonObject.getString("ReqGivenName");
                    TR.ReqLastName = jsonObject.getString("ReqLastName");
                    TR.ReqNickName = jsonObject.getString("ReqNickName");
                    TR.ReqCellPhone = jsonObject.getString("ReqCellPhone");
                    TR.ReqPhoto = jsonObject.getString("ReqPhoto");
                    TR.PassengerTotalCompleted = jsonObject.getInt("PassengerTotalCompleted");
                    TR.PassengerSetupDate = Common.getAppFullDateFromAppStringFullDate(jsonObject.getString("PassengerSetupDate"));
                    TR.PassengerRate = jsonObject.getDouble("PassengerRate");
                    TR.PickUpLocationLatitud = jsonObject.getString("FromLatitud");
                    TR.PickUpLocationLongitud = jsonObject.getString("FromLongitud");
                    TR.PickupAddress = jsonObject.getString("FromAddress");
                    TR.DropOffLatitud = jsonObject.getString("ToLatitud");
                    TR.DropOffLongitud = jsonObject.getString("ToLongitud");
                    TR.DropOffAddress = jsonObject.getString("ToAddress");
                    TR.ReqPaymentTypeId = jsonObject.getInt("ReqPaymentTypeId");
                    TR.ReqPaymentTypeName = jsonObject.getString("ReqPaymentTypeName");
                    TR.ReqServiceTypeId = jsonObject.getInt("ReqServiceTypeId");
                    TR.ReqServiceTypeName = jsonObject.getString("ReqServiceTypeName");
                    TR.CreationDate = Common.getAppFullDateFromAppStringFullDate(jsonObject.getString("CreationDate"));
                    TR.AcceptedByDrvUnityId = jsonObject.getInt("AcceptedByUnityId");
                    TR.DrvGivenName = jsonObject.getString("DriverGivenName");
                    TR.DrvLastName = jsonObject.getString("DriverLastName");
                    TR.DrvNickName = jsonObject.getString("DriverNickName");
                    TR.DrvPhoto = jsonObject.getString("DriverPhoto");
                    TR.DriverTotalCompleted = jsonObject.getInt("DriverTotalCompleted");
                    TR.DriverSetupDate = Common.getAppFullDateFromAppStringFullDate(jsonObject.getString("DriverSetupDate"));
                    TR.DriverRate = jsonObject.getDouble("DriverRate");
                    TR.DrvCellphone = jsonObject.getString("DriverCellPhone");
                    TR.LicensePlate = jsonObject.getString("LicensePlateNumber");
                    TR.AutobileBrand = jsonObject.getString("VehicleMake");
                    TR.AutomobileModel = jsonObject.getString("VehicleModel");
                    TR.AutomobileColor = jsonObject.getString("VehicleColor");
                    TR.AttendedDate = Common.getAppFullDateFromAppStringFullDate(jsonObject.getString("AttendedDate"));
                    TR.EndedDate = Common.getAppFullDateFromAppStringFullDate(jsonObject.getString("EndedDate"));
                    TR.EstimatedTime = jsonObject.getDouble("EstimatedTime");
                    TR.EstimatedDistance = jsonObject.getDouble("EstimatedDistance");
                    TR.EstimatedFare = jsonObject.getDouble("EstimatedFare");
                    TR.TotalDistance = jsonObject.getDouble("TotalDistance");
                    TR.TotalTime = jsonObject.getDouble("TotalTime");
                    TR.TotalFare = jsonObject.getDouble("TotalFare");
                    travelRequest.add(TR);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return travelRequest;
    }
}
