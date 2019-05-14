package com.muflone.android.django_hotels.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.muflone.android.django_hotels.R;

public class AutoConfigurationActivity extends AppCompatActivity {
    /* Auto configuration class */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri uri = getIntent().getData();
        if (uri != null) {
            String path = uri.getPath();
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
                        case "timezone":
                            preferencesEditor.putString(this.getString(R.string.settings_tablet_timezone_id), value);
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
