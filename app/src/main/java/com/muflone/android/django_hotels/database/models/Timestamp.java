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
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

@Entity(tableName = "timestamps",
        indices = {
                @Index(value = {"contract_id", "direction_id", "datetime"}, unique = true),
        })
public class Timestamp {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public final long id;

    @ColumnInfo(name = "contract_id", index = true)
    public final long contractId;

    @ColumnInfo(name = "direction_id", index = true)
    public final long directionId;

    @ColumnInfo(name = "datetime")
    @NonNull
    public final Date datetime;

    @ColumnInfo(name = "description")
    @NonNull
    public final String description;

    @ColumnInfo(name = "transmission", index = true)
    @Nullable
    public Date transmission;

    public Timestamp(long id, long contractId, long directionId, @NotNull Date datetime, @NonNull String description, @Nullable Date transmission) {
        this.id = id;
        this.contractId = contractId;
        this.directionId = directionId;
        this.datetime = datetime;
        this.description = description;
        this.transmission = transmission;
    }
}
