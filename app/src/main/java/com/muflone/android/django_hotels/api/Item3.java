package com.muflone.android.django_hotels.api;

import org.json.JSONException;
import org.json.JSONObject;

public class Item3 extends Item2 {
    public final String value2;

    public Item3(JSONObject jsonObject, String idName, String valueName, String value2Name) throws JSONException {
        // Generic constructor with field names
        super(jsonObject, idName, valueName);
        this.value2 = jsonObject.getString(value2Name);
    }

    public Item3(JSONObject jsonObject, String value2Name) throws JSONException {
        // Default constructor with implicit id and name fields
        super(jsonObject);
        this.value2 = jsonObject.getString(value2Name);
    }
}
