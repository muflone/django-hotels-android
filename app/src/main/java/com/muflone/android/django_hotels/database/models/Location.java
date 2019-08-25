/*
 *     Project: Django Hotels Android
 * Description: The Android client companion app for Django Hotels
 *     Website: http://www.muflone.com/django-hotels-android/
 *      Author: Fabio Castelli (Muflone) <muflone@muflone.com>
 *   Copyright: 2018-2019 Fabio Castelli
 *     License: GPL-3+
 * Source code: https://github.com/muflone/django-hotels-android
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.muflone.android.django_hotels.database.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "locations",
        foreignKeys = {
            @ForeignKey(entity = Region.class,
                        parentColumns = "id",
                        childColumns = "region_id",
                        onDelete = ForeignKey.RESTRICT),
            @ForeignKey(entity = Country.class,
                        parentColumns = "id",
                        childColumns = "country_id",
                        onDelete = ForeignKey.RESTRICT)
        })
public class Location {
    @PrimaryKey
    public final long id;

    @ColumnInfo(name = "name")
    public final String name;

    @ColumnInfo(name = "address")
    public final String address;

    @Ignore
    public Region region;

    @ColumnInfo(name = "region_id", index = true)
    public final long regionId;

    @Ignore
    public Country country;

    @ColumnInfo(name = "country_id", index = true)
    public final String countryId;

    @SuppressWarnings("WeakerAccess")
    public Location(long id, String name, String address, long regionId, String countryId) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.regionId = regionId;
        this.countryId = countryId;
    }

    @SuppressWarnings("WeakerAccess")
    @Ignore
    public Location(long id, String name, String address, Region region, Country country) {
        this(id, name, address, region.id, country.id);
        this.region = region;
        this.country = country;
    }

    @Ignore
    public Location(JSONObject jsonObject) throws JSONException {
        this(jsonObject.getLong("id"),
                jsonObject.getString("name"),
                jsonObject.getString("address"),
                new Region(jsonObject.getJSONObject("region")),
                new Country(jsonObject.getJSONObject("country")));
    }
}
