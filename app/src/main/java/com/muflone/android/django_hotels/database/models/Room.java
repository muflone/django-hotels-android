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
import android.arch.persistence.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "rooms",
        foreignKeys = {
            @ForeignKey(entity = Building.class,
                        parentColumns = "id",
                        childColumns = "building_id",
                        onDelete = ForeignKey.RESTRICT)
        })
public class Room {
    @PrimaryKey
    public final long id;

    @ColumnInfo(name = "name")
    public final String name;

    @ColumnInfo(name = "room_type")
    public final String type;

    @ColumnInfo(name = "bed_type")
    public final String bedType;

    @ColumnInfo(name = "building_id", index = true)
    public final long buildingId;

    @SuppressWarnings("WeakerAccess")
    public Room(long id, String name, String type, String bedType, long buildingId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.bedType = bedType;
        this.buildingId = buildingId;
    }

    public Room(JSONObject jsonObject, long buildingId) throws JSONException {
        this(jsonObject.getJSONObject("room").getLong("id"),
                jsonObject.getJSONObject("room").getString("name"),
                jsonObject.getString("room_type"),
                jsonObject.getString("bed_type"),
                buildingId);
    }
}
