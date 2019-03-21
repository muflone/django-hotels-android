package com.muflone.android.django_hotels.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "structures",
        indices = {
            @Index("company_id"),
            @Index("brand_id"),
            @Index("location_id")
        },
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
    public final long id;

    @ColumnInfo(name = "name")
    public final String name;

    @Ignore
    public Company company;

    @ColumnInfo(name = "company_id")
    public final long companyId;

    @Ignore
    public Brand brand;

    @ColumnInfo(name = "brand_id")
    public final long brandId;

    @Ignore
    public Location location;

    @ColumnInfo(name = "location_id")
    public final long locationId;

    @Ignore
    public List<Building> buildings;

    @Ignore
    public List<Employee> employees;

    public Structure(long id, String name, long companyId, long brandId, long locationId) {
        this.id = id;
        this.name = name;
        this.companyId = companyId;
        this.brandId = brandId;
        this.locationId = locationId;
    }

    @Ignore
    public Structure(long id, String name, Company company, Brand brand, Location location,
                     List<Building> buildings) {
        this(id, name, company.id, brand.id, location.id);
        this.company = company;
        this.brand = brand;
        this.location = location;
        this.buildings = buildings;
    }

    @Ignore
    public Structure(JSONObject jsonObject) throws JSONException {
        this(jsonObject.getJSONObject("structure").getLong("id"),
                jsonObject.getJSONObject("structure").getString("name"),
                new Company(jsonObject.getJSONObject("company")),
                new Brand(jsonObject.getJSONObject("brand")),
                new Location(jsonObject.getJSONObject("location")),
                new ArrayList<Building>());
        // Loop over every building
        JSONArray jsonBuildings = jsonObject.getJSONArray("buildings");
        for (int i = 0; i < jsonBuildings.length(); i++) {
            Building building = new Building(jsonBuildings.getJSONObject(i), this);
            this.buildings.add(building);
        }
    }
}
