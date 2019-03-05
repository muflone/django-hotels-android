package com.muflone.android.django_hotels.api;

import org.json.JSONException;
import org.json.JSONObject;

public class Item2 {
    public final int id;
    public final String value;

    public Item2(JSONObject jsonObject, String idName, String valueName) throws JSONException {
        // Generic constructor with field names
        this.id = jsonObject.getInt(idName);
        this.value = jsonObject.getString(valueName);
    }

    public Item2(JSONObject jsonObject) throws JSONException {
        // Default constructor with implicit id and name fields
        this(jsonObject, "id", "name");
    }
}
