package com.muflone.android.django_hotels.api.generics;

import org.json.JSONException;
import org.json.JSONObject;

public class Item4 extends Item3 {
    public final String value3;

    public Item4(JSONObject jsonObject, String idName, String valueName, String value2Name, String value3Name) throws JSONException {
        // Generic constructor with field names
        super(jsonObject, idName, valueName, value2Name);
        this.value3 = jsonObject.getString(value3Name);
    }

    public Item4(JSONObject jsonObject, String value2Name, String value3Name) throws JSONException {
        // Default constructor with implicit id and name fields
        super(jsonObject, value2Name);
        this.value3 = jsonObject.getString(value3Name);
    }

}
