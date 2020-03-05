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
import com.muflone.android.django_hotels.RoomStatus;
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.database.models.ServiceActivity;

import java.util.List;

public class TaskStructureUpdateRoomStatus extends AsyncTask<RoomStatus, Void, Void> {
    private final Singleton singleton = Singleton.getInstance();
    private final Table<Long, Long, ServiceActivity> serviceActivityTable;

    @SuppressWarnings("WeakerAccess")
    public TaskStructureUpdateRoomStatus(Table<Long, Long, ServiceActivity> serviceActivityTable) {
        this.serviceActivityTable = serviceActivityTable;
    }

    @Override
    protected Void doInBackground(RoomStatus... params) {
        RoomStatus roomStatus = params[0];
        List<ServiceActivity> serviceActivityList =
                this.singleton.database.serviceActivityDao().listByDateContract(
                        this.singleton.selectedDate,
                        roomStatus.contractId, roomStatus.roomId);
        ServiceActivity serviceActivity;
        if (serviceActivityList.size() > 0) {
            serviceActivity = serviceActivityList.get(0);
            if (roomStatus.service != null) {
                // Update existing ServiceActivity
                serviceActivity.serviceId = roomStatus.service.id;
                serviceActivity.description = roomStatus.description;
                serviceActivity.transmission = roomStatus.transmission;
                this.singleton.database.serviceActivityDao().update(serviceActivity);
                serviceActivityTable.put(roomStatus.contractId, roomStatus.roomId,
                        serviceActivity);
            } else {
                // Delete existing ServiceActivity
                this.singleton.database.serviceActivityDao().delete(serviceActivity);
                serviceActivityTable.remove(roomStatus.contractId, roomStatus.roomId);
            }
        } else if (roomStatus.service != null) {
            // Create new ServiceActivity
            serviceActivity = new ServiceActivity(0,
                    this.singleton.selectedDate,
                    roomStatus.contractId,
                    roomStatus.structureId,
                    roomStatus.roomId,
                    roomStatus.service.id,
                    1, false, roomStatus.description, null);
            this.singleton.database.serviceActivityDao().insert(serviceActivity);
            serviceActivityTable.put(roomStatus.contractId, roomStatus.roomId,
                    serviceActivity);
        }
        return null;
    }
}

