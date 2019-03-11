package com.muflone.android.django_hotels.api.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Building {
    public final int id;
    public final String name;
    public final Location location;
    public final List<Room> rooms;

    public Building(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getJSONObject("building").getInt("id");
        this.name = jsonObject.getJSONObject("building").getString("name");
        this.location = new Location(jsonObject.getJSONObject("location"));
        JSONArray jsonRooms = jsonObject.getJSONArray("rooms");
        // Loop over every room
        this.rooms = new ArrayList<Room>();
        for (int i = 0; i < jsonRooms.length(); i++) {
            Room room = new Room(jsonRooms.getJSONObject(i));
            this.rooms.add(room);
        }
    }
}
