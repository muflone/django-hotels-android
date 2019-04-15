package com.muflone.android.django_hotels.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "services")
public class Service implements Comparable<Service> {
    @PrimaryKey
    public final long id;

    @ColumnInfo(name = "name")
    public final String name;

    @ColumnInfo(name = "extra_service")
    public final boolean extra_service;

    @ColumnInfo(name = "show_in_app")
    public final boolean show_in_app;

    public Service(long id, String name, boolean extra_service, boolean show_in_app) {
        this.id = id;
        this.name = name;
        this.extra_service = extra_service;
        this.show_in_app = show_in_app;
    }

    public Service(JSONObject jsonObject) throws JSONException {
        this(jsonObject.getLong("id"),
                jsonObject.getString("name"),
                jsonObject.getBoolean("extra_service"),
                jsonObject.getBoolean("show_in_app"));
    }

    @Override
    public int compareTo(Service service) {
        return this.name.compareTo(service.name);
    }
}
