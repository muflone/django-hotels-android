package com.muflone.android.django_hotels.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "contract_types")
public class ContractType {
    @PrimaryKey
    public final int id;

    @ColumnInfo(name = "name")
    public final String name;

    @ColumnInfo(name = "daily")
    public final int dailyHours;

    @ColumnInfo(name = "weekly")
    public final int weeklyHours;

    public ContractType(int id, String name, int dailyHours, int weeklyHours) {
        this.id = id;
        this.name = name;
        this.dailyHours = dailyHours;
        this.weeklyHours = weeklyHours;
    }

    public ContractType(JSONObject jsonObject) throws JSONException {
        this(jsonObject.getInt("id"),
                jsonObject.getString("name"),
                jsonObject.getInt("daily"),
                jsonObject.getInt("weekly"));
    }
}
