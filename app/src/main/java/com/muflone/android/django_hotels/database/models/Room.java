package com.muflone.android.django_hotels.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "rooms")
public class Room {
    @PrimaryKey
    public final int id;

    @ColumnInfo(name = "name")
    public final String name;

    @ColumnInfo(name = "room_type")
    public final String type;

    @ColumnInfo(name = "bed_type")
    public final String bedType;

    public Room(int id, String name, String type, String bedType) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.bedType = bedType;
    }

    public Room(JSONObject jsonObject) throws JSONException {
        this(jsonObject.getJSONObject("room").getInt("id"),
                jsonObject.getJSONObject("room").getString("name"),
                jsonObject.getString("room_type"),
                jsonObject.getString("bed_type"));
    }
}
