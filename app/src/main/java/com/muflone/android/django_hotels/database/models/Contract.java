package com.muflone.android.django_hotels.database.models;

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

    public Contract(int id, String guid, Date startDate, Date endDate,
                    boolean enabled, boolean active,
                    Employee employee, Company company, ContractType type, JobType job,
                    List<Integer> buildings) {
        this.id = id;
        this.guid = guid;
        this.startDate = startDate;
        this.endDate = endDate;
        this.enabled = enabled;
        this.active = active;
        this.employee = employee;
        this.company = company;
        this.type = type;
        this.job = job;
        this.buildings = buildings;
    }

    public Contract(JSONObject jsonObject) throws JSONException, ParseException {
        this(jsonObject.getJSONObject("contract").getInt("id"),
                jsonObject.getJSONObject("contract").getString("guid"),
                new SimpleDateFormat("yyyy-MM-dd").parse(jsonObject.getJSONObject("contract").getString("start")),
                new SimpleDateFormat("yyyy-MM-dd").parse(jsonObject.getJSONObject("contract").getString("end")),
                jsonObject.getJSONObject("contract").getBoolean("enabled"),
                jsonObject.getJSONObject("contract").getBoolean("active"),
                new Employee(jsonObject.getJSONObject("employee")),
                new Company(jsonObject.getJSONObject("company")),
                new ContractType(jsonObject.getJSONObject("type")),
                new JobType(jsonObject.getJSONObject("job")),
                new ArrayList<Integer>());
        // Loop every building id
        JSONArray jsonBuildings = jsonObject.getJSONArray("buildings");
        for (int i = 0; i < jsonBuildings.length(); i++) {
            this.buildings.add(jsonBuildings.getInt(i));
        }
    }
}
