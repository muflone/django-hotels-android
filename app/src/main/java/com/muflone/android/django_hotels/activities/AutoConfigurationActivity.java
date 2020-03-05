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

package com.muflone.android.django_hotels.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.muflone.android.django_hotels.R;

import java.util.Objects;

public class AutoConfigurationActivity extends AppCompatActivity {
    /* Auto configuration class */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri uri = getIntent().getData();
        if (uri != null) {
            String path = Objects.requireNonNull(uri.getPath());
            if (path.equals("/api/v1/configuration/")) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor preferencesEditor = preferences.edit();

                for (String argument : uri.getQueryParameterNames()) {
                    String value = uri.getQueryParameter(argument);
                    switch (argument) {
                        case "tablet_id":
                            preferencesEditor.putString(this.getString(R.string.settings_tablet_id_id), value);
                            break;
                        case "tablet_key":
                            preferencesEditor.putString(this.getString(R.string.settings_tablet_key_id), value);
                            break;
                        case "api_url":
                            preferencesEditor.putString(this.getString(R.string.settings_api_url_id), value);
                            break;
                    }
                }
                preferencesEditor.apply();
            }
        }
        // Go to the main activity and terminate this activity
        startActivity(new Intent(this, MainActivity.class));
        this.finish();
    }
}
