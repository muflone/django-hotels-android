package com.muflone.android.django_hotels;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

public class Settings {
    private final SharedPreferences preferences;
    private final Context context;

    public Settings(Context context) {
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
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

    public boolean getBuildingsInitiallyClosed() {
        return this.preferences.getBoolean(this.context.getString(R.string.settings_structures_buildings_closed_id), false);
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

    public boolean getHomeScreenShortcutAdded() {
        return this.preferences.getBoolean(this.context.getString(R.string.settings_home_screen_shortcut), false);
    }

    public void setHomeScreenShortcutAdded(boolean value) {
        this.preferences.edit().putBoolean(this.context.getString(R.string.settings_home_screen_shortcut), value).commit();
    }

    public boolean getRoomsListStandardHeight() {
        return this.preferences.getBoolean(this.context.getString(R.string.settings_structures_rooms_list_standard_height_id), true);
    }
}
