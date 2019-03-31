package com.muflone.android.django_hotels.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "timestamp_directions")
public class TimestampDirection {
    @PrimaryKey
    @NonNull
    public final long id;

    @ColumnInfo(name = "name")
    @NonNull
    public final String name;

    @ColumnInfo(name = "description")
    @NonNull
    public final String description;

    @ColumnInfo(name = "short_code")
    @NonNull
    public final String shortCode;

    @ColumnInfo(name = "type_enter")
    public final boolean enter;

    @ColumnInfo(name = "type_exit")
    public final boolean exit;

    public TimestampDirection(long id, String name, String description, String shortCode, boolean enter, boolean exit) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.shortCode = shortCode;
        this.enter = enter;
        this.exit = exit;
    }

    @Ignore
    public TimestampDirection(JSONObject jsonObject) throws JSONException {
        this(jsonObject.getLong("id"),
                jsonObject.getString("name"),
                jsonObject.getString("description"),
                jsonObject.getString("short_code"),
                jsonObject.getBoolean("type_enter"),
                jsonObject.getBoolean("type_exit"));
    }
}
