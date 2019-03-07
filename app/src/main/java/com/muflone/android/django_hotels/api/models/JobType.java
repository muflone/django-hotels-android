package com.muflone.android.django_hotels.api.models;

import com.muflone.android.django_hotels.api.generics.Item2;

import org.json.JSONException;
import org.json.JSONObject;

public class JobType extends Item2 {
    public JobType(JSONObject jsonObject) throws JSONException {
        super(jsonObject, "id", "name");
    }
}
