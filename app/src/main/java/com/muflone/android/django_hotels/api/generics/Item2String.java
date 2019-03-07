package com.muflone.android.django_hotels.api.generics;

import org.json.JSONException;
import org.json.JSONObject;

public class Item2String {
    public final String id;
    public final String value;

    public Item2String(JSONObject jsonObject, String idName, String valueName) throws JSONException {
        // Generic constructor with field names
        this.id = jsonObject.getString(idName);
        this.value = jsonObject.getString(valueName);
    }

    public Item2String(JSONObject jsonObject) throws JSONException {
        // Default constructor with implicit id and name fields
        this(jsonObject, "id", "name");
    }
}
