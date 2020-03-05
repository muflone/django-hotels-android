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

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import java.util.Locale;

public class Settings {
    private final SharedPreferences preferences;
    public final Context context;

    public Settings(Context context) {
        this.context = context;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
    }

    public String getTabletID() {
        return preferences.getString(this.context.getString(R.string.settings_tablet_id_id), "");
    }

    public String getTabletKey() {
        return preferences.getString(this.context.getString(R.string.settings_tablet_key_id), "");
    }

    public Uri getApiUri() {
        return Uri.parse(preferences.getString(this.context.getString(R.string.settings_api_url_id), ""));
    }

    public boolean getScanBeep() {
        return this.preferences.getBoolean(this.context.getString(R.string.settings_scan_beep_id), false);
    }

    public boolean getScanOrientationLock() {
        return this.preferences.getBoolean(this.context.getString(R.string.settings_scan_orientation_lock_id), false);
    }

    public String getPackageName() {
        return this.context.getPackageName();
    }

    public String getApplicationName() {
        return this.context.getString(R.string.app_name);
    }

    public String getApplicationVersion() {
        return this.context.getString(R.string.app_version);
    }

    public String getApplicationNameVersion() {
        return String.format(Locale.ROOT, "%s %s",
                this.getApplicationName(),
                this.getApplicationVersion());
    }

    @SuppressWarnings("unused")
    public String getString(String key, String defaultValue) {
        // Get a string value, using the default value if not existing
        return this.preferences.getString(key, defaultValue);
    }

    public void setValue(String key, @NonNull String value) {
        this.preferences.edit().putString(key, value).apply();
    }

    @SuppressWarnings("unused")
    public int getInteger(String key, int defaultValue) {
        // Get an int value, using the default value if not existing
        return this.preferences.getInt(key, defaultValue);
    }

    public void setValue(String key, Integer value) {
        this.preferences.edit().putInt(key, value).apply();
    }

    public long getLong(String key, long defaultValue) {
        // Get a long value, using the default value if not existing
        return this.preferences.getLong(key, defaultValue);
    }

    public void setValue(String key, Long value) {
        this.preferences.edit().putLong(key, value).apply();
    }

    @SuppressWarnings("unused")
    public boolean getBoolean(String key, boolean defaultValue) {
        // Get a boolean value, using the default value if not existing
        return this.preferences.getBoolean(key, defaultValue);
    }

    public void setValue(String key, Boolean value) {
        this.preferences.edit().putBoolean(key, value).apply();
    }

    @SuppressWarnings("unused")
    public float getFloat(String key, float defaultValue) {
        // Get a float value, using the default value if not existing
        return this.preferences.getFloat(key, defaultValue);
    }

    public void setValue(String key, Float value) {
        this.preferences.edit().putFloat(key, value).apply();
    }

    public void unset(String key) {
        this.preferences.edit().remove(key).apply();
    }
}
