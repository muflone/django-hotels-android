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

    @ColumnInfo(name = "command")
    public final String command;

    public CommandType(@NotNull String id, String type, String command) {
        this.id = id;
        this.type = type;
        this.command = command;
    }

    public CommandType(JSONObject jsonObject) throws JSONException {
        this(jsonObject.getString("id"),
                jsonObject.getString("type"),
                jsonObject.getString("command"));
    }

    public JSONObject getJsonCommand() {
        // Return a JSONObject from command
        try {
            return new JSONObject(this.command);
        } catch (JSONException e) {
            return null;
        }
    }
}
