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

package com.muflone.android.django_hotels;

import com.google.common.collect.Table;
import com.muflone.android.django_hotels.database.models.ServiceActivity;
import com.muflone.android.django_hotels.tasks.TaskExtrasUpdateExtraStatus;

import java.util.Date;

public class ExtraStatus {
    public final long contractId;
    public final long structureId;
    public final long id;
    public long minutes;
    public String description;
    public Date transmission;

    public ExtraStatus(long contractId, long structureId, long id, long minutes, String description, Date transmission) {
        this.contractId = contractId;
        this.structureId = structureId;
        this.id = id;
        this.minutes = minutes;
        this.description = description;
        this.transmission = transmission;
    }

    public void updateDatabase(Table<Long, Long, ServiceActivity> serviceActivityTable) {
        // Update database row
        new TaskExtrasUpdateExtraStatus(serviceActivityTable).execute(this);
    }
}