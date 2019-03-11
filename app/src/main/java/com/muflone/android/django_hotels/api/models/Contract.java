package com.muflone.android.django_hotels.api.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(tableName = "contracts",
        indices = {@Index(value = {"first_name", "last_name"}, unique = true)})
public class Contract {
    @PrimaryKey
    public final int id;

    @ColumnInfo(name = "guid")
    public final String guid;

    @ColumnInfo(name = "start")
    public final Date startDate;

    @ColumnInfo(name = "end")
    public final Date endDate;

    @ColumnInfo(name = "enabled")
    public final boolean enabled;

    @ColumnInfo(name = "active")
    public final boolean active;

    public final Employee employee;
    public final Company company;
    public final ContractType type;
    public final JobType job;
    public final List<Integer> buildings;

    public Contract(JSONObject jsonObject) throws JSONException, ParseException {
        JSONObject jsonContract = jsonObject.getJSONObject("contract");
        this.id = jsonContract.getInt("id");
        this.guid = jsonContract.getString("guid");
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
        this.startDate = parser.parse(jsonContract.getString("start"));
        this.endDate = parser.parse(jsonContract.getString("end"));
        this.enabled = jsonContract.getBoolean("enabled");
        this.active = jsonContract.getBoolean("active");
        this.employee = new Employee(jsonObject.getJSONObject("employee"));
        this.company = new Company(jsonObject.getJSONObject("company"));
        this.type = new ContractType(jsonObject.getJSONObject("type"));
        this.job = new JobType(jsonObject.getJSONObject("job"));
        JSONArray jsonBuildings = jsonObject.getJSONArray("buildings");
        // Loop every building id
        this.buildings = new ArrayList<Integer>();
        for (int i = 0; i < jsonBuildings.length(); i++) {
            this.buildings.add(jsonBuildings.getInt(i));
        }
    }
}
