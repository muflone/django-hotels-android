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
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "timestamp_directions")
public class TimestampDirection {
    @PrimaryKey
    public final long id;

    @ColumnInfo(name = "name")
    @NonNull
    public final String name;

    @ColumnInfo(name = "description")
    @NonNull
    public final String description;

    @ColumnInfo(name = "short_code")
    @NonNull
    public final String shortCode;

    @ColumnInfo(name = "type_enter")
    public final boolean enter;

    @ColumnInfo(name = "type_exit")
    public final boolean exit;

    @SuppressWarnings("WeakerAccess")
    public TimestampDirection(long id, @NotNull String name, @NotNull String description, @NotNull String shortCode, boolean enter, boolean exit) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.shortCode = shortCode;
        this.enter = enter;
        this.exit = exit;
    }

    @Ignore
    public TimestampDirection(JSONObject jsonObject) throws JSONException {
        this(jsonObject.getLong("id"),
                jsonObject.getString("name"),
                jsonObject.getString("description"),
                jsonObject.getString("short_code"),
                jsonObject.getBoolean("type_enter"),
                jsonObject.getBoolean("type_exit"));
    }
}
