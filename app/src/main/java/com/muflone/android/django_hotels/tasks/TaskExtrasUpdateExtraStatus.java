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

package com.muflone.android.django_hotels.tasks;

import android.os.AsyncTask;

import com.google.common.collect.Table;
import com.muflone.android.django_hotels.Constants;
import com.muflone.android.django_hotels.ExtraStatus;
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.database.models.ServiceActivity;

import java.util.List;

public class TaskExtrasUpdateExtraStatus extends AsyncTask<ExtraStatus, Void, Void> {
    private final Singleton singleton = Singleton.getInstance();
    private final Table<Long, Long, ServiceActivity> serviceActivityTable;

    @SuppressWarnings("WeakerAccess")
    public TaskExtrasUpdateExtraStatus(Table<Long, Long, ServiceActivity> serviceActivityTable) {
        this.serviceActivityTable = serviceActivityTable;
    }

    @Override
    protected Void doInBackground(ExtraStatus... params) {
        ExtraStatus extraStatus = params[0];
        List<ServiceActivity> serviceActivityList =
                this.singleton.database.serviceActivityDao().listByDateContract(
                        this.singleton.selectedDate,
                        extraStatus.contractId, extraStatus.id);
        ServiceActivity serviceActivity;
        if (serviceActivityList.size() > 0) {
            serviceActivity = serviceActivityList.get(0);
            if (extraStatus.minutes > 0) {
                // Update existing ServiceActivity
                serviceActivity.serviceId = Constants.EXTRAS_SERVICE_ID;
                serviceActivity.description = extraStatus.description;
                serviceActivity.transmission = extraStatus.transmission;
                serviceActivity.serviceQty = extraStatus.minutes;
                this.singleton.database.serviceActivityDao().update(serviceActivity);
                serviceActivityTable.put(serviceActivity.contractId, serviceActivity.roomId, serviceActivity);
            } else {
                // Delete existing ServiceActivity
                this.singleton.database.serviceActivityDao().delete(serviceActivity);
                serviceActivityTable.remove(serviceActivity.contractId, serviceActivity.roomId);
            }
        } else if (extraStatus.minutes > 0) {
            // Create new ServiceActivity
            serviceActivity = new ServiceActivity(0,
                    this.singleton.selectedDate,
                    extraStatus.contractId,
                    extraStatus.structureId,
                    extraStatus.id,
                    Constants.EXTRAS_SERVICE_ID,
                    extraStatus.minutes,
                    true,
                    extraStatus.description,
                    null);
            this.singleton.database.serviceActivityDao().insert(serviceActivity);
            serviceActivityTable.put(serviceActivity.contractId, serviceActivity.roomId, serviceActivity);
        }
        return null;
    }
}

