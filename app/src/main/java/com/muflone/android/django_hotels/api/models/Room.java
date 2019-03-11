package com.muflone.android.django_hotels.api.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Room {
    public final int id;
    public final String name;
    public final String roomType;
    public final String bedType;

    public Room(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getJSONObject("room").getInt("id");
        this.name = jsonObject.getJSONObject("room").getString("name");
        this.roomType = jsonObject.getString("room_type");
        this.bedType = jsonObject.getString("bed_type");
    }
}
