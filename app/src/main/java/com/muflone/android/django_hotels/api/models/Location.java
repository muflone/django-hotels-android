package com.muflone.android.django_hotels.api.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Location {
    public final int id;
    public final String name;
    public final String address;

    public final Region region;
    public final Country country;

    public Location(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getInt("id");
        this.name = jsonObject.getString("name");
        this.address = jsonObject.getString("address");
        region = new Region(jsonObject.getJSONObject("region"));
        country = new Country(jsonObject.getJSONObject("country"));
    }
}
