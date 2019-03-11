package com.muflone.android.django_hotels.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "structures",
        foreignKeys = {
            @ForeignKey(entity = Company.class,
                    parentColumns = "id",
                    childColumns = "company_id",
                    onDelete = ForeignKey.RESTRICT),
            @ForeignKey(entity = Brand.class,
                    parentColumns = "id",
                    childColumns = "brand_id",
                    onDelete = ForeignKey.RESTRICT),
            @ForeignKey(entity = Location.class,
                    parentColumns = "id",
                    childColumns = "location_id",
                    onDelete = ForeignKey.RESTRICT)
            })
public class Structure {
    @PrimaryKey
    public final int id;

    @ColumnInfo(name = "name")
    public final String name;

    @ColumnInfo(name = "company_id")
    public final int companyId;
    private final Company company;

    @ColumnInfo(name = "brand_id")
    public final int brandId;
    private final Brand brand;

    @ColumnInfo(name = "location_id")
    public final int locationId;
    private final Location location;

    private final List<Building> buildings;

    public Structure(int id, String name, Company company, Brand brand, Location location,
                     List<Building> buildings) {
        this.id = id;
        this.name = name;
        this.companyId = company.id;
        this.company = company;
        this.brandId = brand.id;
        this.brand = brand;
        this.locationId = location.id;
        this.location = location;
        this.buildings = buildings;
    }

    public Structure(JSONObject jsonObject) throws JSONException {
        this(jsonObject.getJSONObject("structure").getInt("id"),
                jsonObject.getJSONObject("structure").getString("name"),
                new Company(jsonObject.getJSONObject("company")),
                new Brand(jsonObject.getJSONObject("brand")),
                new Location(jsonObject.getJSONObject("location")),
                new ArrayList<Building>());
        // Loop over every building
        JSONArray jsonBuildings = jsonObject.getJSONArray("buildings");
        for (int i = 0; i < jsonBuildings.length(); i++) {
            Building building = new Building(jsonBuildings.getJSONObject(i));
            this.buildings.add(building);
        }
    }
}
