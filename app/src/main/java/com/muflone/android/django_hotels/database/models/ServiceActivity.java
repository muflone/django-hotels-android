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
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

@Entity(tableName = "activities",
        indices = {
                @Index(value = {"date", "contract_id", "room_id"}, unique = true),
        })
public class ServiceActivity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public final long id;

    @ColumnInfo(name = "date")
    @NonNull
    public final Date date;

    @ColumnInfo(name = "contract_id", index = true)
    public final long contractId;

    @ColumnInfo(name = "structure_id")
    public final long structureId;

    @ColumnInfo(name = "room_id", index = true)
    public final long roomId;

    @ColumnInfo(name = "service_id", index = true)
    public long serviceId;

    @ColumnInfo(name = "service_qty")
    public long serviceQty;

    @ColumnInfo(name = "description")
    @NonNull
    public String description;

    @ColumnInfo(name = "transmission", index = true)
    @Nullable
    public Date transmission;

    @ColumnInfo(name = "extras")
    public final boolean extras;

    public ServiceActivity(long id, @NonNull Date date, long contractId,
                           long structureId, long roomId,
                           long serviceId, long serviceQty, boolean extras,
                           @NonNull String description, @Nullable Date transmission) {
        this.id = id;
        this.date = date;
        this.contractId = contractId;
        this.structureId = structureId;
        this.roomId = roomId;
        this.serviceId = serviceId;
        this.serviceQty = serviceQty;
        this.extras = extras;
        this.description = description;
        this.transmission = transmission;
    }
}
