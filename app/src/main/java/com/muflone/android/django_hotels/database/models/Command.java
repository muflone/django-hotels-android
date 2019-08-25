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
import android.support.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "commands")
public class Command implements Comparable<Command> {
    @PrimaryKey
    public final Long id;

    @ColumnInfo(name = "name")
    @NonNull
    public final String name;

    @ColumnInfo(name = "type")
    @NonNull
    public final String type;

    @ColumnInfo(name = "context")
    @NonNull
    public final String context;

    @ColumnInfo(name = "command")
    public final JSONObject command;

    @ColumnInfo(name = "uses")
    public final int uses;

    public Command(Long id, @NotNull String name, @NotNull String type, @NotNull String context, JSONObject command, int uses) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.context = context;
        this.command = command;
        this.uses = uses;
    }

    public Command(JSONObject jsonObject) throws JSONException {
        this(jsonObject.getLong("id"),
                jsonObject.getString("name"),
                jsonObject.getString("command_type"),
                jsonObject.getString("context"),
                jsonObject.getJSONObject("command"),
                jsonObject.getInt("uses"));
    }

    @Override
    public int compareTo(Command command) {
        // Compare two commands to allow sorting
        return this.id.compareTo(command.id);
    }
}
