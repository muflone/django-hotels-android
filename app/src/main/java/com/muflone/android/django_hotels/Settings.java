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
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getTabletID() {
        return preferences.getString(this.context.getString(R.string.settings_tablet_id_id), "");
    }

    public String getTabletKey() {
        return preferences.getString(this.context.getString(R.string.settings_tablet_key_id), "");
    }

    public String getTimeZone() {
        return preferences.getString(this.context.getString(R.string.settings_timezone_id), "UTC");
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
}
