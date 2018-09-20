/*
 * Created by Adrian Joshet Moreno Fabian on 5/19/18 9:37 PM
 * Rocha Technologies de Mexico SA de CV
 * soporte@rochatech.com
 */

package com.rochatech.Model;

import com.rochatech.library.Common;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Date;
@SuppressWarnings({"WeakerAccess","unused"})
public class TripHistory {

    private Integer ReceiptId, TravelRequestId, DriverUnityId, PassengerUnityId, PaymentTypeId, ServiceTypeId, BankAccountId;
    private String ReceiptNumber, PaymentTypeName, ServiceTypeName, GatewayReceiptId, GatewayReceiptStatus, ReceiptStatus, FromAddress, ToAddress, ReqStatus;
    Double SubTotal, Discounts, Taxes, Total, TotalTime, TotalDistance, TotalFare;
    private Date  ReceiptDate, CreationDate, AcceptedDate, AttendedDate, EndedDate;
    private Boolean PaidToDriver;

    public TripHistory() { super(); }

    //region Set
    public void SetReceiptId(int ReceiptId) { this.ReceiptId = ReceiptId; }
    public void SetTravelRequestId(int TravelRequestId) { this.TravelRequestId = TravelRequestId; }
    public void SetDriverUnityId(int DriverUnityId) { this.DriverUnityId = DriverUnityId; }
    public void SetPassengerUnityId(int PassengerUnityId) { this.PassengerUnityId = PassengerUnityId; }
    public void SetPaymentTypeId(int PaymentTypeId) { this.PaymentTypeId = PaymentTypeId; }
    public void SetServiceTypeId(int ServiceTypeId) { this.ServiceTypeId = ServiceTypeId; }
    public void SetBankAccountId(int BankAccountId) { this.BankAccountId = BankAccountId; }

    public void SetReceiptNumber(String ReceiptNumber) { this.ReceiptNumber = ReceiptNumber; }
    public void SetPaymentTypeName(String PaymentTypeName) { this.PaymentTypeName = PaymentTypeName; }
    public void SetServiceTypeName(String ServiceTypeName) { this.ServiceTypeName = ServiceTypeName; }
    public void SetGatewayReceiptId(String GatewayReceiptId) { this.GatewayReceiptId = GatewayReceiptId; }
    public void SetGatewayReceiptStatus(String GatewayReceiptStatus) { this.GatewayReceiptStatus = GatewayReceiptStatus; }
    public void SetReceiptStatus(String ReceiptStatus) { this.ReceiptStatus = ReceiptStatus; }
    public void SetFromAddress(String FromAddress) { this.FromAddress = FromAddress; }
    public void SetToAddress(String ToAddress) { this.ToAddress = ToAddress; }
    public void SetReqStatus(String ReqStatus) { this.ReqStatus = ReqStatus; }

    public void SetSubTotal(double SubTotal) { this.SubTotal = SubTotal;}
    public void SetDiscounts(double Discounts) { this.Discounts = Discounts;}
    public void SetTaxes(double Taxes) { this.Taxes = Taxes;}
    public void SetTotal(double Total) { this.Total = Total;}
    public void SetTotalTime(double TotalTime) { this.TotalTime = TotalTime;}
    public void SetTotalDistance(double TotalDistance) { this.TotalDistance = TotalDistance;}
    public void SetTotalFare(double TotalFare) { this.TotalFare = TotalFare;}

    public void SetReceiptDate(Date ReceiptDate) {this.ReceiptDate = ReceiptDate;}
    public void SetCreationDate(Date CreationDate) {this.CreationDate = CreationDate;}
    public void SetAcceptedDate(Date AcceptedDate) {this.AcceptedDate = AcceptedDate;}
    public void SetAttendedDate(Date AttendedDate) {this.AttendedDate = AttendedDate;}
    public void SetEndedDate(Date EndedDate) {this.EndedDate = EndedDate;}

    public void SetPaidToDriver (Boolean PaidToDriver) { this.PaidToDriver = PaidToDriver; }
    //endregion

    //region Get
    public int getReceiptId() { return ReceiptId;}
    public int getTravelRequestId() { return TravelRequestId;}
    public int getDriverUnityId() { return DriverUnityId;}
    public int getPassengerUnityId() { return PassengerUnityId;}
    public int getPaymentTypeId() { return PaymentTypeId;}
    public int getServiceTypeId() { return ServiceTypeId;}
    public int getBankAccountId() { return BankAccountId;}

    public String getReceiptNumber() { return ReceiptNumber;}
    public String getPaymentTypeName() { return PaymentTypeName;}
    public String getServiceTypeName() { return ServiceTypeName;}
    public String getGatewayReceiptId() { return GatewayReceiptId;}
    public String getGatewayReceiptStatus() { return GatewayReceiptStatus;}
    public String getReceiptStatus() { return ReceiptStatus;}
    public String getFromAddress() { return FromAddress;}
    public String getToAddress() { return ToAddress;}
    public String getReqStatus() { return ReqStatus;}

    public double getSubTotal() { return SubTotal;}
    public double getDiscounts() { return Discounts;}
    public double getTaxes() { return Taxes;}
    public double getTotal() { return Total;}
    public double getTotalTime() { return TotalTime;}
    public double getTotalDistance() { return TotalDistance;}
    public double getTotalFare() { return TotalFare;}

    public Date getReceiptDate() { return ReceiptDate;}
    public Date getCreationDate() { return CreationDate;}
    public Date getAcceptedDate() { return AcceptedDate;}
    public Date getAttendedDate() { return AttendedDate;}
    public Date getEndedDate() { return EndedDate;}

    public Boolean getPaidToDriver() { return PaidToDriver;}
    //endregion

    public static ArrayList<TripHistory> fromJson (JSONArray jsonArray) {
        JSONObject jsonObject;
        ArrayList<TripHistory> history = new ArrayList<>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                jsonObject = jsonArray.getJSONObject(i);
                TripHistory TH = new TripHistory();
                TH.ReceiptId = Integer.parseInt(jsonObject.getString("ReceiptId"));
                TH.ReceiptNumber = jsonObject.getString("ReceiptNumber");
                TH.TravelRequestId = Integer.parseInt(jsonObject.getString("TravelRequestId"));
                TH.DriverUnityId = Integer.parseInt(jsonObject.getString("DriverUnityId"));
                TH.PassengerUnityId = Integer.parseInt(jsonObject.getString("PassengerUnityId"));
                TH.SubTotal = Double.parseDouble(jsonObject.getString("SubTotal"));
                TH.Discounts = Double.parseDouble(jsonObject.getString("Discounts"));
                TH.Taxes = Double.parseDouble(jsonObject.getString("Taxes"));
                TH.Total = Double.parseDouble(jsonObject.getString("Total"));
                TH.PaymentTypeId = Integer.parseInt(jsonObject.getString("PaymentTypeId"));
                TH.PaymentTypeName = jsonObject.getString("ReqPaymentTypeName");
                TH.BankAccountId = Integer.parseInt(jsonObject.getString("BankAccountId"));
                TH.ReceiptDate = Common.getAppFullDateFromAppStringFullDate(jsonObject.getString("ReceiptDate"));
                TH.CreationDate = Common.getAppFullDateFromAppStringFullDate(jsonObject.getString("CreationDate"));
                TH.AcceptedDate = Common.getAppFullDateFromAppStringFullDate(jsonObject.getString("AcceptedDate"));
                TH.AttendedDate = Common.getAppFullDateFromAppStringFullDate(jsonObject.getString("AttendedDate"));
                TH.EndedDate = Common.getAppFullDateFromAppStringFullDate(jsonObject.getString("EndedDate"));
                TH.GatewayReceiptId = jsonObject.getString("GatewayReceiptId");
                TH.GatewayReceiptStatus = jsonObject.getString("GatewayReceiptStatus");
                TH.ReceiptStatus = jsonObject.getString("ReceiptStatus");
                TH.PaidToDriver = jsonObject.getString("PaidToDriver").contains("1");
                TH.FromAddress = jsonObject.getString("FromAddress");
                TH.ToAddress = jsonObject.getString("ToAddress");
                TH.ServiceTypeId = Integer.parseInt(jsonObject.getString("ReqServiceTypeId"));
                TH.ServiceTypeName = jsonObject.getString("ReqServiceTypeName");
                TH.TotalTime = Double.parseDouble(jsonObject.getString("TotalTime"));
                TH.TotalDistance = Double.parseDouble(jsonObject.getString("TotalDistance"));
                TH.TotalFare = Double.parseDouble(jsonObject.getString("TotalFare"));
                TH.ReqStatus = jsonObject.getString("ReqStatus");
                history.add(TH);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        return history;
    }

}
