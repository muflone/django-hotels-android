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

import com.muflone.android.django_hotels.EmployeeStatus;
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.database.models.Timestamp;
import com.muflone.android.django_hotels.database.models.TimestampDirection;

import java.util.List;

public class TaskStructureUpdateEmployeeStatus extends AsyncTask<EmployeeStatus, Void, Void> {
    private final Singleton singleton = Singleton.getInstance();

    // Update database for EmployeeStatus
    @Override
    protected Void doInBackground(EmployeeStatus... params) {
        EmployeeStatus employeeStatus = params[0];
        List<Timestamp> timestampsEmployee = this.singleton.database.timestampDao().listByContractNotEnterExit(
                employeeStatus.date, employeeStatus.contract.id);
        // Delete any previous timestamp
        this.singleton.database.timestampDao().delete(timestampsEmployee.toArray(new Timestamp[0]));
        // Re-add every active timestamp
        timestampsEmployee.clear();
        for (int index = 0; index < employeeStatus.timestampDirections.size(); index++) {
            if (employeeStatus.directionsCheckedArray[index]) {
                TimestampDirection timestampDirection = employeeStatus.timestampDirections.get(index);
                timestampsEmployee.add(new Timestamp(0, employeeStatus.contract.id,
                        timestampDirection.id, employeeStatus.date,"", null));
            }
        }
        this.singleton.database.timestampDao().insert(timestampsEmployee.toArray(new Timestamp[0]));
        return null;
    }
}
