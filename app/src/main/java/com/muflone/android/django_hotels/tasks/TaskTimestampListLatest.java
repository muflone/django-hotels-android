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

import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.TimestampEmployeeItem;
import com.muflone.android.django_hotels.database.models.TimestampEmployee;
import com.muflone.android.django_hotels.fragments.ScannerFragment;

import java.util.List;

public class TaskTimestampListLatest extends AsyncTask<Long, Void, List<TimestampEmployee>> {
    private final List<TimestampEmployeeItem> timestampEmployeeList;
    private final ScannerFragment.TimestampAdapter timestampAdapter;
    private final Singleton singleton = Singleton.getInstance();

    @SuppressWarnings("WeakerAccess")
    public TaskTimestampListLatest(List<TimestampEmployeeItem> timestampEmployeeList,
                                   ScannerFragment.TimestampAdapter timestampAdapter) {
        this.timestampEmployeeList = timestampEmployeeList;
        this.timestampAdapter = timestampAdapter;
    }

    @Override
    protected List<TimestampEmployee> doInBackground(Long... params) {
        return this.singleton.database.timestampDao().listByLatestEnterExit(
                this.singleton.selectedDate,
                this.singleton.selectedStructure != null ? this.singleton.selectedStructure.id : 0,
                params[0]);
    }

    @Override
    protected void onPostExecute(List<TimestampEmployee> result) {
        // Reload the timestamps list
        this.timestampEmployeeList.clear();
        for (TimestampEmployee timestamp : result) {
            this.timestampEmployeeList.add(new TimestampEmployeeItem(
                    timestamp.id,
                    String.format("%s %s", timestamp.firstName, timestamp.lastName),
                    timestamp.datetime,
                    timestamp.direction,
                    timestamp.transmission));
        }
        this.timestampAdapter.notifyDataSetChanged();
    }
}

