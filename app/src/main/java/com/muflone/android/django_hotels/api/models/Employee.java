package com.muflone.android.django_hotels.api.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "employees")
public class Employee {
    @PrimaryKey
    public final int id;

    @ColumnInfo(name = "first_name")
    public final String first_name;

    @ColumnInfo(name = "last_name")
    public final String last_name;

    @ColumnInfo(name = "genre")
    public final String genre;

    public Employee(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getInt("id");
        this.first_name = jsonObject.getString("first_name");
        this.last_name = jsonObject.getString("last_name");
        this.genre = jsonObject.getString("genre");
    }
}
