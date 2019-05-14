package com.muflone.android.django_hotels.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.muflone.android.django_hotels.R;
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.api.ApiData;

public class CreateShortcutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ApiData apiData = Singleton.getInstance().apiData;

        super.onCreate(savedInstanceState);

        Intent shortcutIntent;
        shortcutIntent = new Intent(this, MainActivity.class);

        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        // Get shortcut title
        String title = apiData.tabletSettingsMap.containsKey("app.shortcut.title") ?
                apiData.tabletSettingsMap.get("app.shortcut.title").getString() :
                this.getString(R.string.app_name);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
        // Get shortcut icon
        if (apiData.tabletSettingsMap.containsKey("app.shortcut.icon")) {
            // Use custom bitmap icon
            byte[] icon = apiData.tabletSettingsMap.get("app.shortcut.icon").decodeBase64();
            Bitmap bitmap = BitmapFactory.decodeByteArray(icon, 0, icon.length);
            // Resize bitmap if needed
            int width = apiData.tabletSettingsMap.containsKey("app.shortcut.icon.width") ?
                    apiData.tabletSettingsMap.get("app.shortcut.icon.width").getInteger() :
                    bitmap.getWidth();
            int height = apiData.tabletSettingsMap.containsKey("app.shortcut.icon.height") ?
                    apiData.tabletSettingsMap.get("app.shortcut.icon.height").getInteger() :
                    bitmap.getHeight();
            if (bitmap.getWidth() != width | bitmap.getHeight() != height) {
                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
            }
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, bitmap);
        } else {
            // Use default application launcher icon
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                    Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.ic_launcher));
        }
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        this.sendBroadcast(intent);
        Toast.makeText(this, R.string.add_shortcut_succeeded, Toast.LENGTH_LONG).show();
        // Terminate this activity
        this.finish();
    }
}
