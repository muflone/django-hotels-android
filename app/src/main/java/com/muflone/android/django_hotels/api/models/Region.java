package com.muflone.android.django_hotels.api.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Region {
    public final int id;
    public final String name;

    public Region(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getInt("id");
        this.name = jsonObject.getString("name");
    }
}
