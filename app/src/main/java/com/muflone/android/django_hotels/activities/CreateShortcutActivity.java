package com.muflone.android.django_hotels.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.muflone.android.django_hotels.R;
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.Utility;
import com.muflone.android.django_hotels.api.ApiData;

import java.util.Objects;

public class CreateShortcutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Singleton singleton = Singleton.getInstance();
        ApiData apiData = singleton.apiData;

        super.onCreate(savedInstanceState);

        String title = apiData.tabletSettingsMap.containsKey("app.shortcut.title") ?
                Objects.requireNonNull(apiData.tabletSettingsMap.get("app.shortcut.title")).getString() :
                singleton.settings.getApplicationName();
        int width = Objects.requireNonNull(apiData.tabletSettingsMap.get("app.shortcut.icon.width")).getInteger();
        int height = Objects.requireNonNull(apiData.tabletSettingsMap.get("app.shortcut.icon.height")).getInteger();
        Utility.createShortcutIcon(this, title, width, height,
                apiData.tabletSettingsMap.get("app.shortcut.icon").getString(),
                LoaderActivity.class);
        Toast.makeText(this, R.string.add_shortcut_succeeded, Toast.LENGTH_LONG).show();
        // Terminate this activity
        this.finish();
    }
}
