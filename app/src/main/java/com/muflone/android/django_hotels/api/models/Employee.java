package com.muflone.android.django_hotels.api.models;

import com.muflone.android.django_hotels.api.generics.Item4;

import org.json.JSONException;
import org.json.JSONObject;

public class Employee extends Item4 {
    public Employee(JSONObject jsonObject) throws JSONException {
        super(jsonObject, "id", "first_name", "last_name", "genre");
    }
}
