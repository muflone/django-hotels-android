package com.muflone.android.django_hotels.api.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Contract {
    public final int id;
    public final String guid;
    public final Date startDate;
    public final Date endDate;
    public final boolean enabled;
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
