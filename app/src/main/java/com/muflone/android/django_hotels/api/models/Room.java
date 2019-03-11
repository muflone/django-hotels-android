package com.muflone.android.django_hotels.api.models;

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
    public final String roomType;

    @ColumnInfo(name = "bed_type")
    public final String bedType;

    public Room(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getJSONObject("room").getInt("id");
        this.name = jsonObject.getJSONObject("room").getString("name");
        this.roomType = jsonObject.getString("room_type");
        this.bedType = jsonObject.getString("bed_type");
    }
}
