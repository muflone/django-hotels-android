package com.muflone.android.django_hotels.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "countries")
public class Country {
    @PrimaryKey
    @NonNull
    public final String id;

    @ColumnInfo(name = "name")
    public final String name;

    @SuppressWarnings("WeakerAccess")
    public Country(@NotNull String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Country(JSONObject jsonObject) throws JSONException {
        this(jsonObject.getString("id"),
                jsonObject.getString("name"));
    }
}
