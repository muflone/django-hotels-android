package com.muflone.android.django_hotels.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "services")
public class Service {
    @PrimaryKey
    public final long id;

    @ColumnInfo(name = "name")
    public final String name;

    @ColumnInfo(name = "extra_service")
    public final boolean extra_service;

    public Service(long id, String name, boolean extra_service) {
        this.id = id;
        this.name = name;
        this.extra_service = extra_service;
    }

    public Service(JSONObject jsonObject) throws JSONException {
        this(jsonObject.getLong("id"),
                jsonObject.getString("name"),
                jsonObject.getBoolean("extra_service"));
    }
}
