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
import android.arch.persistence.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "services")
public class Service implements Comparable<Service> {
    @PrimaryKey
    public final long id;

    @ColumnInfo(name = "name")
    public final String name;

    @ColumnInfo(name = "extra_service")
    public final boolean extra_service;

    @ColumnInfo(name = "show_in_app")
    public final boolean show_in_app;

    public Service(long id, String name, boolean extra_service, boolean show_in_app) {
        this.id = id;
        this.name = name;
        this.extra_service = extra_service;
        this.show_in_app = show_in_app;
    }

    public Service(JSONObject jsonObject) throws JSONException {
        this(jsonObject.getLong("id"),
                jsonObject.getString("name"),
                jsonObject.getBoolean("extra_service"),
                jsonObject.getBoolean("show_in_app"));
    }

    @Override
    public int compareTo(Service service) {
        return this.name.compareTo(service.name);
    }
}
