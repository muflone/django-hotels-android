package com.muflone.android.django_hotels.api.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Employee {
    public final int id;
    public final String first_name;
    public final String last_name;
    public final String genre;

    public Employee(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getInt("id");
        this.first_name = jsonObject.getString("first_name");
        this.last_name = jsonObject.getString("last_name");
        this.genre = jsonObject.getString("genre");
    }
}
