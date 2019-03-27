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
    public final String id;

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

    public TimestampDirection(String id, String description, String shortCode, boolean enter, boolean exit) {
        this.id = id;
        this.description = description;
        this.shortCode = shortCode;
        this.enter = enter;
        this.exit = exit;
    }

    @Ignore
    public TimestampDirection(JSONObject jsonObject) throws JSONException {
        this(jsonObject.getString("name"),
                jsonObject.getString("description"),
                jsonObject.getString("short_code"),
                jsonObject.getBoolean("type_enter"),
                jsonObject.getBoolean("type_exit"));
    }
}
