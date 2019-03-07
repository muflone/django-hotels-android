package com.muflone.android.django_hotels.api.models;

import com.muflone.android.django_hotels.api.generics.Item2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Structure {
    private final Item2 structure;
    private final Company company;
    private final Item2 brand;
    private final Location location;
    private final List<Building> buildings;

    public Structure(JSONObject jsonObject) throws JSONException {
        structure = new Item2(jsonObject.getJSONObject("structure"));
        company = new Company(jsonObject.getJSONObject("company"));
        brand = new Item2(jsonObject.getJSONObject("brand"));
        location = new Location(jsonObject.getJSONObject("location"));

        // Loop over every building
        JSONArray jsonBuildings = jsonObject.getJSONArray("buildings");
        this.buildings = new ArrayList<Building>();
        for (int i = 0; i < jsonBuildings.length(); i++) {
            Building building = new Building(jsonBuildings.getJSONObject(i));
            this.buildings.add(building);
        }
    }
}
