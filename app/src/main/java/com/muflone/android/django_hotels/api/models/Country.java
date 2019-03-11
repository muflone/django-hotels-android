package com.muflone.android.django_hotels.api.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Country {
    public final String id;
    public final String name;

    public Country(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getString("id");
        this.name = jsonObject.getString("name");
    }
}
