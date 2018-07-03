/*
 * Created by Adrian Joshet Moreno Fabian on 5/19/18 9:35 PM
 * Rocha Technologies de Mexico SA de CV
 * soporte@rochatech.com
 */

package com.rochatech.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class PaymentTypes {
    int PaymentTypeId;
    String Name, ImageName;

    public PaymentTypes() { super(); }

    public void setPaymentTypeId (int PaymentTypeId) { this.PaymentTypeId = PaymentTypeId; }
    public void setName (String Name) { this.Name = Name;}
    public void setImageName (String ImageName) { this.ImageName = ImageName;}

    public int getPaymentTypeId () { return PaymentTypeId;}
    public String getName (){ return Name;}
    public String getImageName() {return ImageName;}

    public static ArrayList<PaymentTypes> fromJson (JSONArray jsonArray) {
        JSONObject jsonObject;
        ArrayList<PaymentTypes> payments = new ArrayList<>(jsonArray.length());
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                try {
                    PaymentTypes PT = new PaymentTypes();
                    PT.PaymentTypeId = Integer.parseInt(jsonObject.getString("Id"));
                    PT.Name = jsonObject.getString("Name");
                    PT.ImageName = jsonObject.getString("iOSIconName");
                    payments.add(PT);
                } catch (Exception e) {
                    e.printStackTrace();
                    //continue;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return payments;
    }
}
