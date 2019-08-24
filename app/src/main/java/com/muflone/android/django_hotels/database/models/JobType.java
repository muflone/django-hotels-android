package com.muflone.android.django_hotels.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "job_types")
public class JobType {
    @PrimaryKey
    public final long id;

    @ColumnInfo(name = "name")
    public final String name;

    @SuppressWarnings("WeakerAccess")
    public JobType(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public JobType(JSONObject jsonObject) throws JSONException {
        this(jsonObject.getLong("id"),
                jsonObject.getString("name"));
    }
}
