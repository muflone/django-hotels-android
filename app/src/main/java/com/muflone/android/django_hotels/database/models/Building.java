package com.muflone.android.django_hotels.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "buildings",
        foreignKeys = {
                @ForeignKey(entity = Location.class,
                            parentColumns = "id",
                            childColumns = "location_id",
                            onDelete = ForeignKey.NO_ACTION)},
        indices = {
                @Index(value = "location_id")
        })
public class Building {
    @PrimaryKey
    public final long id;

    @ColumnInfo(name = "name")
    public final String name;

    @Ignore
    public Location location = null;

    @ColumnInfo(name = "location_id")
    public final long locationId;

    @Ignore
    public List<Room> rooms = null;

    public Building(long id, String name, long locationId) {
        this.id = id;
        this.name = name;
        this.locationId = locationId;
    }

    @Ignore
    public Building(long id, String name, Location location, List<Room> rooms) {
        this(id, name, location.id);
        this.location = location;
        this.rooms = rooms;
    }

    @Ignore
    public Building(JSONObject jsonObject) throws JSONException {
        this(jsonObject.getJSONObject("building").getLong("id"),
                jsonObject.getJSONObject("building").getString("name"),
                new Location(jsonObject.getJSONObject("location")),
                new ArrayList<Room>());
        // Loop over every room
        JSONArray jsonRooms = jsonObject.getJSONArray("rooms");
        for (int i = 0; i < jsonRooms.length(); i++) {
            Room room = new Room(jsonRooms.getJSONObject(i),
                                 jsonObject.getJSONObject("building").getLong("id"));
            this.rooms.add(room);
        }
    }
}
