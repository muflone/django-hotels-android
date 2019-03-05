package com.muflone.android.django_hotels.api;

import org.json.JSONException;
import org.json.JSONObject;

public class Location extends Item3 {
    public final Item2 region;
    public final Item2String country;

    public Location(JSONObject jsonObject) throws JSONException {
        super(jsonObject, "id", "name", "address");
        region = new Item2(jsonObject.getJSONObject("region"));
        country = new Item2String(jsonObject.getJSONObject("country"));
    }
}
