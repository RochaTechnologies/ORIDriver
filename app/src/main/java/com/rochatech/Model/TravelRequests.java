package com.rochatech.Model;

import com.rochatech.library.Common;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Date;
@SuppressWarnings({"WeakerAccess","unused"})
public class TravelRequests {

    private TravelRequests() { super(); }

    private Integer TravelRequestId, ReqByUnityId, PassengerTotalCompleted, ReqPaymentTypeId, ReqServiceTypeId, AcceptedByDrvUnityId, DriverTotalCompleted;
    private String ReqGivenName, ReqLastName, ReqNickName, ReqCellPhone, ReqPhoto, PickUpLocationLatitud, PickUpLocationLongitud, PickupAddress, DropOffLatitud, DropOffLongitud, DropOffAddress, ReqPaymentTypeName, ReqServiceTypeName, DrvGivenName, DrvLastName, DrvNickName, DrvPhoto, DrvCellphone, LicensePlate, AutobileBrand, AutomobileModel, AutomobileColor, AutomobileYear;
    private Date PassengerSetupDate, CreationDate, DriverSetupDate, AttendedDate, EndedDate;
    private Double PassengerRate, DriverRate, EstimatedDistance, EstimatedTime, EstimatedFare, TotalDistance, TotalTime, TotalFare;
    private String ReqStatus;

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
                    AC.ReqGivenName = jsonObject.getString("ReqGivenName");
                    AC.ReqLastName = jsonObject.getString("ReqLastName");
                    AC.ReqNickName = jsonObject.getString("ReqNickName");
                    AC.ReqCellPhone = jsonObject.getString("ReqCellPhone");
                    AC.ReqPhoto = jsonObject.getString("ReqPhoto");
                    AC.PassengerSetupDate = Common.getAppFullDateFromAppStringFullDate(jsonObject.getString("PassengerSetupDate"));
                    AC.PickUpLocationLatitud = jsonObject.getString("FromLatitud");
                    AC.PickUpLocationLongitud = jsonObject.getString("FromLongitud");
                    AC.PickupAddress = jsonObject.getString("FromAddress");
                    AC.DropOffLatitud = jsonObject.getString("ToLatitud");
                    AC.DropOffLongitud = jsonObject.getString("ToLongitud");
                    AC.DropOffAddress = jsonObject.getString("ToAddress");
                    AC.ReqPaymentTypeName = jsonObject.getString("ReqPaymentTypeName");
                    AC.ReqServiceTypeName = jsonObject.getString("ReqServiceTypeName");
                    AC.CreationDate = Common.getAppFullDateFromAppStringFullDate(jsonObject.getString("CreationDate"));
                    AC.ReqStatus = jsonObject.getString("ReqStatus");
                    AC.DrvGivenName = jsonObject.getString("DriverGivenName");
                    AC.DrvLastName = jsonObject.getString("DriverLastName");
                    AC.DrvNickName = jsonObject.getString("DriverNickName");
                    AC.DrvPhoto = jsonObject.getString("DriverPhoto");
                    AC.DriverSetupDate = Common.getAppFullDateFromAppStringFullDate(jsonObject.getString("DriverSetupDate"));
                    AC.DrvCellphone = jsonObject.getString("DriverCellPhone");
                    AC.LicensePlate = jsonObject.getString("LicensePlateNumber");
                    AC.AutobileBrand = jsonObject.getString("VehicleMake");
                    AC.AutomobileModel = jsonObject.getString("VehicleModel");
                    AC.AutomobileColor = jsonObject.getString("VehicleColor");
                    AC.AutomobileYear = jsonObject.getString("VehicleYear");
                    AC.AttendedDate = Common.getAppFullDateFromAppStringFullDate(jsonObject.getString("AttendedDate"));
                    AC.EndedDate = Common.getAppFullDateFromAppStringFullDate(jsonObject.getString("EndedDate"));
                    if (jsonObject.getString("TravelRequestId").trim().isEmpty() || jsonObject.getString("TravelRequestId").contains("null") || jsonObject.getString("TravelRequestId") == null) {
                        AC.TravelRequestId = null;
                    } else {
                        AC.TravelRequestId = Integer.parseInt(jsonObject.getString("TravelRequestId"));
                    }
                    if (jsonObject.getString("ReqByUnityId").trim().isEmpty() || jsonObject.getString("ReqByUnityId").contains("null") || jsonObject.getString("ReqByUnityId") == null) {
                        AC.ReqByUnityId = null;
                    } else {
                        AC.ReqByUnityId = Integer.parseInt(jsonObject.getString("ReqByUnityId"));
                    }
                    if (jsonObject.getString("PassengerTotalCompleted").trim().isEmpty() || jsonObject.getString("PassengerTotalCompleted").contains("null") || jsonObject.getString("PassengerTotalCompleted") == null) {
                        AC.PassengerTotalCompleted = null;
                    } else {
                        AC.PassengerTotalCompleted = Integer.parseInt(jsonObject.getString("PassengerTotalCompleted"));
                    }
                    if (jsonObject.getString("PassengerRate").trim().isEmpty() || jsonObject.getString("PassengerRate").contains("null") || jsonObject.getString("PassengerRate") == null) {
                        AC.PassengerRate = null;
                    } else {
                        AC.PassengerRate = Double.parseDouble(jsonObject.getString("PassengerRate"));
                    }
                    if (jsonObject.getString("ReqPaymentTypeId").trim().isEmpty() || jsonObject.getString("ReqPaymentTypeId").contains("null") || jsonObject.getString("ReqPaymentTypeId") == null) {
                        AC.ReqPaymentTypeId = null;
                    } else {
                        AC.ReqPaymentTypeId = Integer.parseInt(jsonObject.getString("ReqPaymentTypeId"));
                    }
                    if (jsonObject.getString("ReqServiceTypeId").trim().isEmpty() || jsonObject.getString("ReqServiceTypeId").contains("null") || jsonObject.getString("ReqServiceTypeId") == null) {
                        AC.ReqServiceTypeId = null;
                    } else {
                        AC.ReqServiceTypeId = Integer.parseInt(jsonObject.getString("ReqServiceTypeId"));
                    }
                    if (jsonObject.getString("DriverTotalCompleted").trim().isEmpty() || jsonObject.getString("DriverTotalCompleted").contains("null") || jsonObject.getString("DriverTotalCompleted") == null) {
                        AC.DriverTotalCompleted = null;
                    } else {
                        AC.DriverTotalCompleted = Integer.parseInt(jsonObject.getString("DriverTotalCompleted"));
                    }
                    if (jsonObject.getString("DriverRate").trim().isEmpty() || jsonObject.getString("DriverRate").contains("null") || jsonObject.getString("DriverRate") == null) {
                        AC.DriverRate = null;
                    } else {
                        AC.DriverRate = Double.parseDouble(jsonObject.getString("DriverRate"));
                    }
                    if (jsonObject.getString("EstimatedDistance").trim().isEmpty() || jsonObject.getString("EstimatedDistance").contains("null") || jsonObject.getString("EstimatedDistance") == null) {
                        AC.EstimatedDistance = null;
                    } else {
                        AC.EstimatedDistance = Double.parseDouble(jsonObject.getString("EstimatedDistance"));
                    }
                    if (jsonObject.getString("EstimatedTime").trim().isEmpty() || jsonObject.getString("EstimatedTime").contains("null") || jsonObject.getString("EstimatedTime") == null) {
                        AC.EstimatedTime = null;
                    } else {
                        AC.EstimatedTime = Double.parseDouble(jsonObject.getString("EstimatedTime"));
                    }
                    if (jsonObject.getString("EstimatedFare").trim().isEmpty() || jsonObject.getString("EstimatedFare").contains("null") || jsonObject.getString("EstimatedFare") == null) {
                        AC.EstimatedFare = null;
                    } else {
                        AC.EstimatedFare = Double.parseDouble(jsonObject.getString("EstimatedFare"));
                    }
                    if (jsonObject.getString("DriverTotalCompleted").trim().isEmpty() || jsonObject.getString("DriverTotalCompleted").contains("null") || jsonObject.getString("DriverTotalCompleted") == null) {
                        AC.DriverTotalCompleted = null;
                    } else {
                        AC.DriverTotalCompleted = Integer.parseInt(jsonObject.getString("DriverTotalCompleted"));
                    }
                    if (jsonObject.getString("AcceptedByUnityId").trim().isEmpty() || jsonObject.getString("AcceptedByUnityId").contains("null") || jsonObject.getString("AcceptedByUnityId") == null) {
                        AC.AcceptedByDrvUnityId = null;
                    } else {
                        AC.AcceptedByDrvUnityId = Integer.parseInt(jsonObject.getString("AcceptedByUnityId"));
                    }
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