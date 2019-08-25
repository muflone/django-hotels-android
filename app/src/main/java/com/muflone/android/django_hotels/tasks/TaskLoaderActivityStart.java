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

import android.content.Intent;
import android.os.AsyncTask;

import com.muflone.android.django_hotels.Utility;
import com.muflone.android.django_hotels.activities.LoaderActivity;
import com.muflone.android.django_hotels.activities.MainActivity;

public class TaskLoaderActivityStart extends AsyncTask<LoaderActivity, Void, LoaderActivity> {
    // Await a bit before loading
    @Override
    protected LoaderActivity doInBackground(LoaderActivity... params) {
        Utility.sleep(500);
        return params[0];
    }

    @Override
    protected void onPostExecute(LoaderActivity result) {
        super.onPostExecute(result);
        // Load the main activity
        result.startActivity(new Intent(result, MainActivity.class));
        result.finish();
    }
}