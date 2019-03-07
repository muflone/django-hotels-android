package com.muflone.android.django_hotels.api.models;

import com.muflone.android.django_hotels.api.generics.Item2;

import org.json.JSONException;
import org.json.JSONObject;

public class Company extends Item2 {
    public Company(JSONObject jsonObject) throws JSONException {
        super(jsonObject, "id", "name");
    }
}
