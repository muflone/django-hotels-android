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

package com.muflone.android.django_hotels.tasks;

import android.os.AsyncTask;

import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.database.dao.TimestampDao;
import com.muflone.android.django_hotels.database.models.Timestamp;

public class TaskTimestampInsert extends AsyncTask<Timestamp, Void, Void> {
    private final TaskListenerInterface listener;
    private final Singleton singleton = Singleton.getInstance();

    public TaskTimestampInsert(TaskListenerInterface listener) {
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Timestamp... params) {
        // Insert new Timestamp
        TimestampDao timestampDao = this.singleton.database.timestampDao();
        timestampDao.insert(params);
        this.listener.onSuccess(null);
        return null;
    }
}

