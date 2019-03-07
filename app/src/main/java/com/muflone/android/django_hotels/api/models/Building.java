package com.muflone.android.django_hotels.api.models;

import com.muflone.android.django_hotels.api.generics.Item2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Building extends Item2 {
    public final Location location;
    public final List<Room> rooms;

    public Building(JSONObject jsonObject) throws JSONException {
        super(jsonObject.getJSONObject("building"), "id", "name");
        location = new Location(jsonObject.getJSONObject("location"));
        JSONArray jsonRooms = jsonObject.getJSONArray("rooms");
        // Loop over every room
        this.rooms = new ArrayList<Room>();
        for (int i = 0; i < jsonRooms.length(); i++) {
            Room room = new Room(jsonRooms.getJSONObject(i));
            this.rooms.add(room);
        }
    }
}
