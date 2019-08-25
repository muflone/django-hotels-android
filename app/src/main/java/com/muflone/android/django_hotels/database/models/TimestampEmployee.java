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
import android.support.annotation.NonNull;

import java.util.Date;

public class TimestampEmployee {
    @ColumnInfo(name = "id")
    public final long id;

    @ColumnInfo(name = "first_name")
    public final String firstName;

    @ColumnInfo(name = "last_name")
    public final String lastName;

    @ColumnInfo(name = "datetime")
    @NonNull
    public final Date datetime;

    @ColumnInfo(name = "direction")
    @NonNull
    public final String direction;

    @ColumnInfo(name = "transmission")
    public final Date transmission;

    public TimestampEmployee(long id, String firstName, String lastName, @NonNull Date datetime,
                             @NonNull String direction, Date transmission) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.datetime = datetime;
        this.direction = direction;
        this.transmission = transmission;
    }
}
