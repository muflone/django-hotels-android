package com.muflone.android.django_hotels.api.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Structure {
    public final int id;
    public final String name;
    private final Company company;
    private final Brand brand;
    private final Location location;
    private final List<Building> buildings;

    public Structure(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getJSONObject("structure").getInt("id");
        this.name = jsonObject.getJSONObject("structure").getString("name");
        this.company = new Company(jsonObject.getJSONObject("company"));
        this.brand = new Brand(jsonObject.getJSONObject("brand"));
        this.location = new Location(jsonObject.getJSONObject("location"));

        // Loop over every building
        JSONArray jsonBuildings = jsonObject.getJSONArray("buildings");
        this.buildings = new ArrayList<Building>();
        for (int i = 0; i < jsonBuildings.length(); i++) {
            Building building = new Building(jsonBuildings.getJSONObject(i));
            this.buildings.add(building);
        }
    }
}
