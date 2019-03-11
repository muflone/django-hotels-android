package com.muflone.android.django_hotels.api.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "brands")
public class Brand {
    @PrimaryKey
    public final int id;

    @ColumnInfo(name = "name")
    public final String name;

    public Brand(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getInt("id");
        this.name = jsonObject.getString("name");
    }
}
