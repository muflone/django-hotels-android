package com.muflone.android.django_hotels.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "command_types")
public class CommandType {
    @PrimaryKey
    @NonNull
    public final String id;

    @ColumnInfo(name = "type")
    public final String type;

    public CommandType(@NotNull String id, String type) {
        this.id = id;
        this.type = type;
    }

    public CommandType(JSONObject jsonObject) throws JSONException {
        this(jsonObject.getString("id"),
                jsonObject.getString("type"));
    }
}
