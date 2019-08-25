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

package com.muflone.android.django_hotels.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.muflone.android.django_hotels.R;
import com.muflone.android.django_hotels.Settings;
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.Utility;
import com.muflone.android.django_hotels.api.Api;
import com.muflone.android.django_hotels.commands.CommandConstants;
import com.muflone.android.django_hotels.commands.CommandFactory;
import com.muflone.android.django_hotels.database.AppDatabase;
import com.muflone.android.django_hotels.database.models.Structure;
import com.muflone.android.django_hotels.tasks.TaskListenerInterface;
import com.muflone.android.django_hotels.tasks.TaskLoaderActivityStart;
import com.muflone.android.django_hotels.tasks.TaskResult;

import java.text.SimpleDateFormat;
import java.util.SortedSet;
import java.util.TreeSet;

public class LoaderActivity extends AppCompatActivity {
    private final Singleton singleton = Singleton.getInstance();
    private TextView textViewAppName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loader_activity);
        // Singleton instance
        this.singleton.settings = new Settings(this);
        this.singleton.api = new Api();
        this.singleton.selectedDate = Utility.getCurrentDate();
        this.singleton.defaultTimeFormat = singleton.settings.getString(CommandConstants.SETTING_DEFAULT_TIME_FORMAT, "HH:mm.ss");
        this.singleton.defaultDateFormat = singleton.settings.getString(CommandConstants.SETTING_DEFAULT_DATE_FORMAT, "yyyy-MM-dd");
        this.singleton.defaultDateFormatter = new SimpleDateFormat(this.singleton.defaultDateFormat);
        this.singleton.openDatabase(this);
        // Prepares CommandFactory for executing commands
        this.singleton.commandFactory = new CommandFactory();
        // Load UI
        this.loadUI();
        this.textViewAppName.setText(this.singleton.settings.getApplicationNameVersion());
        // Reload data from database
        AppDatabase.getAppDatabase(this).reload(this, new TaskListenerInterface() {
            @Override
            public void onSuccess(TaskResult result) {
                // Execute APP BEGIN commands
                LoaderActivity.this.singleton.commandFactory.executeCommands(
                        LoaderActivity.this,
                        LoaderActivity.this.getBaseContext(),
                        CommandConstants.CONTEXT_APP_BEGIN);
                // Select the first structure only if not already selected
                if (LoaderActivity.this.singleton.selectedStructure == null &&
                        LoaderActivity.this.singleton.apiData.structuresMap.size() > 0) {
                    // Select the first available structure
                    SortedSet<Structure> sortedStructures = new TreeSet<>(
                            LoaderActivity.this.singleton.apiData.structuresMap.values());
                    LoaderActivity.this.singleton.selectedStructure = sortedStructures.first();
                }
                new TaskLoaderActivityStart().execute(LoaderActivity.this);
            }

            @Override
            public void onFailure(Exception exception) {
            }

            @Override
            public void onProgress(int step, int total) {
            }
        });
    }

    private void loadUI() {
        this.textViewAppName = this.findViewById(R.id.textViewAppName);
    }
}
