package com.rochatech.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
@SuppressWarnings({"WeakerAccess","unused"})
public class AvailableCities {
    private int CityId, StateId, CountryId;
    private String CityName, StateName, CountryName;

    public AvailableCities() { super(); }

    public void setCityId(int CityId) {
        this.CityId = CityId;
    }

    public int getCityId() {
        return CityId;
    }

    public void setStateId(int StateId) {
        this.StateId = StateId;
    }

    public int getStateId() {
        return StateId;
    }

    public void setCountryId(int CountryId) {
        this.CountryId = CountryId;
    }

    public int getCountryId() {
        return CountryId;
    }

    public void setCityName(String CityName) {
        this.CityName = CityName;
    }

    public String getCityName() {
        return CityName;
    }

    public void setStateName(String StateName) {
        this.StateName = StateName;
    }

    public String getStateName() {
        return StateName;
    }

    public void setCountryName(String CountryName) {
        this.CountryName = CountryName;
    }

    public String getCountryName() {
        return CountryName;
    }

    public static ArrayList<AvailableCities> fromJson(JSONArray jsonArray) {
        JSONObject jsonObject;
        ArrayList<AvailableCities> cities = new ArrayList<>(jsonArray.length());
        try {
            for (int i=0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                try {
                    AvailableCities AC = new AvailableCities();
                    AC.CityId = Integer.parseInt(jsonObject.getString("CityId"));
                    AC.CityName = jsonObject.getString("CityName");
                    AC.StateId = Integer.parseInt(jsonObject.getString("StateId"));
                    AC.StateName = jsonObject.getString("StateName");
                    AC.CountryId = Integer.parseInt(jsonObject.getString("CountryId"));
                    AC.CountryName = jsonObject.getString("CountryName");
                    cities.add(AC);
                } catch (Exception e) {
                    e.printStackTrace();
                    //continue;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return  cities;
    }
}
