package com.muflone.android.django_hotels.api.models;

import com.muflone.android.django_hotels.api.Item2;

import org.json.JSONException;
import org.json.JSONObject;

public class Room extends Item2 {
    public final String roomType;
    public final String bedType;

    public Room(JSONObject jsonObject) throws JSONException {
        super(jsonObject.getJSONObject("room"), "id", "name");
        this.roomType = jsonObject.getString("room_type");
        this.bedType = jsonObject.getString("bed_type");
    }
}
