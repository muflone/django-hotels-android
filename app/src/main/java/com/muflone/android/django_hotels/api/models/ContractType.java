package com.muflone.android.django_hotels.api.models;

import com.muflone.android.django_hotels.api.generics.Item2;

import org.json.JSONException;
import org.json.JSONObject;

public class ContractType extends Item2 {
    public final int dailyHours;
    public final int weeklyHours;

    public ContractType(JSONObject jsonObject) throws JSONException {
        super(jsonObject, "id", "name");
        this.dailyHours = jsonObject.getInt("daily");
        this.weeklyHours = jsonObject.getInt("weekly");
    }
}
