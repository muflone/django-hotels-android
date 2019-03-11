package com.muflone.android.django_hotels.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "buildings")
public class Building {
    @PrimaryKey
    public final int id;

    @ColumnInfo(name = "name")
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
