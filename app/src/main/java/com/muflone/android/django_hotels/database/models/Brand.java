package com.muflone.android.django_hotels.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "brands")
public class Brand {
    @PrimaryKey
    public final long id;

    @ColumnInfo(name = "name")
    public final String name;

    @SuppressWarnings("WeakerAccess")
    public Brand(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Brand(JSONObject jsonObject) throws JSONException {
        this(jsonObject.getLong("id"),
                jsonObject.getString("name"));
    }
}
