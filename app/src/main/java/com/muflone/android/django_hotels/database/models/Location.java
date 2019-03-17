package com.muflone.android.django_hotels.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "locations")
public class Location {
    @PrimaryKey
    public final long id;

    @ColumnInfo(name = "name")
    public final String name;

    @ColumnInfo(name = "address")
    public final String address;

    @Ignore
    public Region region;

    @ColumnInfo(name = "region_id")
    public final long regionId;

    @Ignore
    public Country country;

    @ColumnInfo(name = "country_id")
    public final String countryId;

    public Location(long id, String name, String address, long regionId, String countryId) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.regionId = regionId;
        this.countryId = countryId;
    }

    @Ignore
    public Location(long id, String name, String address, Region region, Country country) {
        this(id, name, address, region.id, country.id);
        this.region = region;
        this.country = country;
    }

    @Ignore
    public Location(JSONObject jsonObject) throws JSONException {
        this(jsonObject.getLong("id"),
                jsonObject.getString("name"),
                jsonObject.getString("address"),
                new Region(jsonObject.getJSONObject("region")),
                new Country(jsonObject.getJSONObject("country")));
    }
}
