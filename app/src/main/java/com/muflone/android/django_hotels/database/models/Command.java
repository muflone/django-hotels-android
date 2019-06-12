package com.muflone.android.django_hotels.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "commands")
public class Command {
    @PrimaryKey
    public final Long id;

    @ColumnInfo(name = "name")
    @NonNull
    public final String name;

    @ColumnInfo(name = "type")
    @NonNull
    public final String type;

    @ColumnInfo(name = "context")
    @NonNull
    public final String context;

    @ColumnInfo(name = "command")
    public final JSONObject command;

    public Command(Long id, String name, String type, @NotNull String context, JSONObject command) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.context = context;
        this.command = command;
    }

    public Command(JSONObject jsonObject) throws JSONException {
        this(jsonObject.getLong("id"),
                jsonObject.getString("name"),
                jsonObject.getString("command_type"),
                jsonObject.getString("context"),
                jsonObject.getJSONObject("command"));
    }
}
