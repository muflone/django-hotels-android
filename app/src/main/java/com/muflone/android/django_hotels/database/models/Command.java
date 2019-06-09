package com.muflone.android.django_hotels.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "commands",
        foreignKeys = {
            @ForeignKey(entity = CommandType.class,
                        parentColumns = "id",
                        childColumns = "type_id",
                        onDelete = ForeignKey.RESTRICT)
        }
)
public class Command {
    @PrimaryKey
    public final Long id;

    @Ignore
    public CommandType type;

    @ColumnInfo(name = "type_id", index = true)
    public final String typeId;

    @ColumnInfo(name = "context")
    @NonNull
    public final String context;

    @ColumnInfo(name = "command")
    public final JSONObject command;

    public Command(Long id, String typeId, @NotNull String context, JSONObject command) {
        this.id = id;
        this.typeId = typeId;
        this.context = context;
        this.command = command;
    }

    public Command(JSONObject jsonObject) throws JSONException {
        this(jsonObject.getLong("id"),
                jsonObject.getString("command_type"),
                jsonObject.getString("context"),
                jsonObject.getJSONObject("command"));
    }
}
