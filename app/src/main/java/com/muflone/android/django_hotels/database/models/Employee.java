package com.muflone.android.django_hotels.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

@Entity(tableName = "employees")
public class Employee {
    @PrimaryKey
    public final long id;

    @ColumnInfo(name = "first_name")
    public final String firstName;

    @ColumnInfo(name = "last_name")
    public final String lastName;

    @ColumnInfo(name = "gender")
    public final String gender;

    @Ignore
    public List<ContractBuildings> contractBuildings = null;

    public Employee(long id, String firstName, String lastName, String gender) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
    }

    @Ignore
    public Employee(JSONObject jsonObject) throws JSONException {
        this(jsonObject.getLong("id"),
                jsonObject.getString("first_name"),
                jsonObject.getString("last_name"),
                jsonObject.getString("gender"));
    }
}
