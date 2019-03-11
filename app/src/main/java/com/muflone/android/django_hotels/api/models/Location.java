package com.muflone.android.django_hotels.api.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "locations")
public class Location {
    @PrimaryKey
    public final int id;

    @ColumnInfo(name = "name")
    public final String name;

    @ColumnInfo(name = "address")
    public final String address;

    @Embedded
    public final Region region;

    @Embedded
    public final Country country;

    public Location(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getInt("id");
        this.name = jsonObject.getString("name");
        this.address = jsonObject.getString("address");
        region = new Region(jsonObject.getJSONObject("region"));
        country = new Country(jsonObject.getJSONObject("country"));
    }
}
