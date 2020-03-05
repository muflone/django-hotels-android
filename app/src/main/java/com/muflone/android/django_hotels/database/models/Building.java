/*
 *     Project: Django Hotels Android
 * Description: The Android client companion app for Django Hotels
 *     Website: http://www.muflone.com/django-hotels-android/
 *      Author: Fabio Castelli (Muflone) <muflone@muflone.com>
 *   Copyright: 2018-2020 Fabio Castelli
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "buildings",
        foreignKeys = {
            @ForeignKey(entity = Structure.class,
                        parentColumns = "id",
                        childColumns = "structure_id",
                        onDelete = ForeignKey.RESTRICT),
            @ForeignKey(entity = Location.class,
                        parentColumns = "id",
                        childColumns = "location_id",
                        onDelete = ForeignKey.RESTRICT)
        })
public class Building {
    @PrimaryKey
    public final long id;

    @ColumnInfo(name = "name")
    public final String name;

    @ColumnInfo(name = "structure_id", index = true)
    public final long structureId;

    @Ignore
    public Location location = null;

    @ColumnInfo(name = "location_id", index = true)
    public final long locationId;

    @ColumnInfo(name = "extras")
    public final boolean extras;

    @Ignore
    public List<Room> rooms = null;

    @Ignore
    public List<Employee> employees = null;

    @SuppressWarnings("WeakerAccess")
    public Building(long id, String name, long structureId, long locationId, boolean extras) {
        this.id = id;
        this.name = name;
        this.structureId = structureId;
        this.locationId = locationId;
        this.extras = extras;
    }

    @SuppressWarnings("WeakerAccess")
    @Ignore
    public Building(long id, String name, Structure structure, Location location, boolean extras, List<Room> rooms) {
        this(id, name, structure.id, location.id, extras);
        this.location = location;
        this.rooms = rooms;
    }

    @Ignore
    public Building(JSONObject jsonObject, Structure structure, boolean extras) throws JSONException {
        this(jsonObject.getJSONObject("building").getLong("id"),
                jsonObject.getJSONObject("building").getString("name"),
                structure,
                new Location(jsonObject.getJSONObject("location")),
                extras,
                new ArrayList<>());
        // Loop over every room
        JSONArray jsonRooms = jsonObject.getJSONArray("rooms");
        for (int i = 0; i < jsonRooms.length(); i++) {
            Room room = new Room(jsonRooms.getJSONObject(i),
                                 jsonObject.getJSONObject("building").getLong("id"));
            this.rooms.add(room);
        }
    }
}
