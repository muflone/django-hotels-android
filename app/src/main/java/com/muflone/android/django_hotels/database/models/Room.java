package com.muflone.android.django_hotels.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "rooms",
        foreignKeys = {
            @ForeignKey(entity = Building.class,
                        parentColumns = "id",
                        childColumns = "building_id",
                        onDelete = ForeignKey.RESTRICT)
        })
public class Room {
    @PrimaryKey
    public final long id;

    @ColumnInfo(name = "name")
    public final String name;

    @ColumnInfo(name = "room_type")
    public final String type;

    @ColumnInfo(name = "bed_type")
    public final String bedType;

    @ColumnInfo(name = "building_id", index = true)
    public final long buildingId;

    @SuppressWarnings("WeakerAccess")
    public Room(long id, String name, String type, String bedType, long buildingId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.bedType = bedType;
        this.buildingId = buildingId;
    }

    public Room(JSONObject jsonObject, long buildingId) throws JSONException {
        this(jsonObject.getJSONObject("room").getLong("id"),
                jsonObject.getJSONObject("room").getString("name"),
                jsonObject.getString("room_type"),
                jsonObject.getString("bed_type"),
                buildingId);
    }
}
