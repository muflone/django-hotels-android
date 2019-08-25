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

import java.util.Date;

public class ReportActivityDetail {
    @ColumnInfo(name = "company_id")
    public long companyId;

    @ColumnInfo(name = "company")
    public String company;

    @ColumnInfo(name = "employee_id")
    public long employeeId;

    @ColumnInfo(name = "first_name")
    public String firstName;

    @ColumnInfo(name = "last_name")
    public String lastName;

    @ColumnInfo(name = "contract_id")
    public long contractId;

    @ColumnInfo(name = "datetime")
    public Date datetime;

    @ColumnInfo(name = "building_id")
    public long buildingId;

    @ColumnInfo(name = "building")
    public String building;

    @ColumnInfo(name = "room_id")
    public long roomId;

    @ColumnInfo(name = "room")
    public String room;

    @ColumnInfo(name = "service_id")
    public long serviceId;

    @ColumnInfo(name = "service")
    public String service;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "service_qty")
    public long service_qty;
}
