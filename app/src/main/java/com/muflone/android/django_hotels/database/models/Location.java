package com.muflone.android.django_hotels.database.models;

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

    public Location(int id, String name, String address, Region region, Country county) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.region = region;
        this.country = county;
    }

    public Location(JSONObject jsonObject) throws JSONException {
        this(jsonObject.getInt("id"),
                jsonObject.getString("name"),
                jsonObject.getString("address"),
                new Region(jsonObject.getJSONObject("region")),
                new Country(jsonObject.getJSONObject("country")));
    }
}
