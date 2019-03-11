package com.muflone.android.django_hotels.api.models;

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

    public Structure(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getJSONObject("structure").getInt("id");
        this.name = jsonObject.getJSONObject("structure").getString("name");
        this.company = new Company(jsonObject.getJSONObject("company"));
        this.companyId = this.company.id;
        this.brand = new Brand(jsonObject.getJSONObject("brand"));
        this.brandId = this.brand.id;
        this.location = new Location(jsonObject.getJSONObject("location"));
        this.locationId = this.location.id;

        // Loop over every building
        JSONArray jsonBuildings = jsonObject.getJSONArray("buildings");
        this.buildings = new ArrayList<Building>();
        for (int i = 0; i < jsonBuildings.length(); i++) {
            Building building = new Building(jsonBuildings.getJSONObject(i));
            this.buildings.add(building);
        }
    }
}
