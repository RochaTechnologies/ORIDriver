/*
 * Created by Adrian Joshet Moreno Fabian on 5/19/18 9:36 PM
 * Rocha Technologies de Mexico SA de CV
 * soporte@rochatech.com
 */

package com.rochatech.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class ServiceTypes {
    int ServiceTypeId;
    String Name, ImageName, Description;

    public ServiceTypes() { super();}
    public void setServiceTypeId(int ServiceTypeId) { this.ServiceTypeId = ServiceTypeId;}
    public void setName(String Name) { this.Name = Name;}
    public void setImageName(String ImageName) { this.ImageName = ImageName;}
    public void setDescription(String Description) { this.Description = Description;}

    public int getServiceTypeId() {return ServiceTypeId;}
    public String getName() {return Name;}
    public String getImageName() {return ImageName;}
    public String getDescription() {return Description;}

    public static ArrayList<ServiceTypes> fromJson (JSONArray jsonArray) {
        JSONObject jsonObject;
        ArrayList<ServiceTypes> services = new ArrayList<>(jsonArray.length());
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                try {
                    ServiceTypes ST = new ServiceTypes();
                    ST.ServiceTypeId = Integer.parseInt(jsonObject.getString("Id"));
                    ST.Name = jsonObject.getString("Name");
                    ST.ImageName = jsonObject.getString("iOSIconName");
                    ST.Description = jsonObject.getString("Description");
                    services.add(ST);
                } catch (Exception e) {
                    e.printStackTrace();
                    //continue;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return services;
    }
}
