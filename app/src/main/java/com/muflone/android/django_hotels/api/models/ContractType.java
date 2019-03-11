package com.muflone.android.django_hotels.api.models;

import org.json.JSONException;
import org.json.JSONObject;

public class ContractType {
    public final int id;
    public final String name;
    public final int dailyHours;
    public final int weeklyHours;

    public ContractType(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getInt("id");
        this.name = jsonObject.getString("name");
        this.dailyHours = jsonObject.getInt("daily");
        this.weeklyHours = jsonObject.getInt("weekly");
    }
}
