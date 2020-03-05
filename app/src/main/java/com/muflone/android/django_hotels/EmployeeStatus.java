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

import com.muflone.android.django_hotels.database.models.Contract;
import com.muflone.android.django_hotels.database.models.Timestamp;
import com.muflone.android.django_hotels.database.models.TimestampDirection;
import com.muflone.android.django_hotels.tasks.TaskStructureUpdateEmployeeStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EmployeeStatus {
    public final Contract contract;
    public final Date date;
    public final List<TimestampDirection> timestampDirections;
    public final String[] directionsArray;
    public final boolean[] directionsCheckedArray;

    @SuppressWarnings("WeakerAccess")
    public EmployeeStatus(Contract contract, Date date,
                          List<TimestampDirection> timestampDirections,
                          List<Timestamp> timestampsEmployee) {
        this.contract = contract;
        this.date = date;
        // Initialize directionsArray and directionsCheckedArray
        this.timestampDirections = timestampDirections;
        this.directionsArray = new String[timestampDirections.size()];
        this.directionsCheckedArray = new boolean[timestampDirections.size()];
        // Get the already assigned timestamp directions to restore
        List<Long> assignedTimestampDirectionsList = new ArrayList<>();
        for (Timestamp timestamp : timestampsEmployee) {
            assignedTimestampDirectionsList.add(timestamp.directionId);
        }
        // Translate timestamp directions to array, needed by the AlertDialog for selections
        int index = 0;
        for (TimestampDirection direction : timestampDirections) {
            this.directionsArray[index] = direction.name;
            this.directionsCheckedArray[index] = assignedTimestampDirectionsList.contains(direction.id);
            index++;
        }
    }

    public void updateDatabase() {
        // Update database row
        new TaskStructureUpdateEmployeeStatus().execute(this);
    }
}
