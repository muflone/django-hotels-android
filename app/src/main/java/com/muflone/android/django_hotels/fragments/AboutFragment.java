package com.muflone.android.django_hotels.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.muflone.android.django_hotels.R;
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.Utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutFragment extends Fragment {
    private Context context;

    @Override
    public View onCreateView(@NonNull  LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Singleton singleton = Singleton.getInstance();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        List<SystemInformationValue> systemInformationValuesList = new ArrayList<>();

        AboutPage aboutPage = new AboutPage(this.context)
                .isRTL(false)
                .setImage(R.mipmap.ic_launcher_round)
                .setDescription(String.format(Locale.ROOT,
                        "%s %s\n%s",
                        singleton.settings.getApplicationName(),
                        singleton.settings.getApplicationVersion(),
                        this.getString(R.string.app_description)))
                .addPlayStore(this.getString(R.string.about_app_playstore))
                .addGitHub(this.getString(R.string.about_app_github));
        // Contacts section
        aboutPage.addGroup(this.getString(R.string.about_contacts))
                .addEmail(this.getString(R.string.author_email))
                .addWebsite(this.getString(R.string.author_web))
                .addTwitter(this.getString(R.string.author_twitter));
        // License section
        aboutPage.addGroup(this.getString(R.string.about_license))
                .addItem(this.getCopyRightsElement())
                .addWebsite(this.getString(R.string.about_license_url),
                        this.getString(R.string.about_app_license));
        // Credits section
        aboutPage.addGroup(this.getString(R.string.about_credits))
                .addGitHub(this.getString(R.string.about_zxing_url),
                        this.getString(R.string.about_zxing_title))
                .addGitHub(this.getString(R.string.about_about_url),
                        this.getString(R.string.about_about_title))
                .addGitHub(this.getString(R.string.about_guava_url),
                        this.getString(R.string.about_guava_title))
                .addGitHub(this.getString(R.string.about_numberprogressbar_url),
                        this.getString(R.string.about_numberprogressbar_title));
        // Application Information section
        aboutPage.addGroup(this.getString(R.string.about_application_information));
        // Application name
        systemInformationValuesList.add(new SystemInformationValue(
                R.string.about_application_information_name,
                singleton.settings.getApplicationName()));
        // Application version
        systemInformationValuesList.add(new SystemInformationValue(
                R.string.about_application_information_version,
                singleton.settings.getApplicationVersion()));
        // Package name
        systemInformationValuesList.add(new SystemInformationValue(
                R.string.about_application_information_package,
                singleton.settings.getPackageName()));
        // Add Application Information elements
        for (SystemInformationValue value : systemInformationValuesList) {
            aboutPage.addItem(this.newSystemInformationElement(value.toString(false)));
        }
        systemInformationValuesList.clear();
        // System Information section
        aboutPage.addGroup(this.getString(R.string.about_system_information));
        // Android version
        systemInformationValuesList.add(new SystemInformationValue(
                R.string.about_system_information_android,
                Build.VERSION.RELEASE,
                Build.ID));
        // SDK API Level
        systemInformationValuesList.add(new SystemInformationValue(
                R.string.about_system_information_sdk,
                String.valueOf(Build.VERSION.SDK_INT)));
        // ROM version
        systemInformationValuesList.add(new SystemInformationValue(
                R.string.about_system_information_rom,
                Build.VERSION.INCREMENTAL));
        // Kernel version
        systemInformationValuesList.add(new SystemInformationValue(
                R.string.about_system_information_kernel,
                System.getProperty("os.version")));
        // Architecture
        systemInformationValuesList.add(new SystemInformationValue(
                R.string.about_system_information_architecture,
                System.getProperty("os.arch")));
        // Device Manufacturer
        systemInformationValuesList.add(new SystemInformationValue(
                R.string.about_system_information_manufacturer,
                Build.MANUFACTURER));
        // Device Model
        systemInformationValuesList.add(new SystemInformationValue(
                R.string.about_system_information_model,
                Build.MODEL));
        // Device Product
        systemInformationValuesList.add(new SystemInformationValue(
                R.string.about_system_information_product,
                Build.PRODUCT));
        // Device Brand
        systemInformationValuesList.add(new SystemInformationValue(
                R.string.about_system_information_brand,
                Build.BRAND));
        // Device
        systemInformationValuesList.add(new SystemInformationValue(
                R.string.about_system_information_device,
                Build.DEVICE));
        // Display Resolution
        systemInformationValuesList.add(new SystemInformationValue(
                R.string.about_system_information_resolution,
                String.valueOf(metrics.widthPixels),
                String.valueOf(metrics.heightPixels),
                String.valueOf(metrics.density)));
        // Timezone
        systemInformationValuesList.add(new SystemInformationValue(
                R.string.about_system_information_timezone,
                TimeZone.getDefault().getID(),
                TimeZone.getDefault().getDisplayName(Locale.ROOT)));
        // Android ID
        systemInformationValuesList.add(new SystemInformationValue(
                R.string.about_system_information_android_id,
                Settings.Secure.getString(
                        Objects.requireNonNull(this.getActivity()).getBaseContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID)));
        // User Agent
        systemInformationValuesList.add(new SystemInformationValue(
                R.string.about_system_information_user_agent,
                System.getProperty("http.agent")));
        // Add System Information elements
        for (SystemInformationValue value : systemInformationValuesList) {
            aboutPage.addItem(this.newSystemInformationElement(value.toString(false)));
        }
        systemInformationValuesList.clear();
        return aboutPage.create();
    }

    private Element getCopyRightsElement() {
        Element copyRightsElement = new Element();
        final String copyrights = String.format(this.getString(R.string.about_app_copyright),
                this.getString(R.string.author_name),
                Calendar.getInstance().get(Calendar.YEAR));
        copyRightsElement.setTitle(copyrights);
        copyRightsElement.setIconDrawable(R.drawable.ic_copyright);
        copyRightsElement.setIconTint(mehdi.sakout.aboutpage.R.color.about_item_icon_color);
        copyRightsElement.setIconNightTint(android.R.color.white);
        copyRightsElement.setGravity(Gravity.START);
        copyRightsElement.setOnClickListener(view -> Toast.makeText(context, copyrights, Toast.LENGTH_SHORT).show());
        return copyRightsElement;
    }

    private Element newSystemInformationElement(String value) {
        // Return an element for System Information
        return new Element()
                .setIconDrawable(R.drawable.ic_info)
                .setTitle(value)
                .setOnClickListener(view -> {
                    Utility.copyToClipboard(context, value);
                    Toast.makeText(context, R.string.about_feedback_copied_to_clipboard, Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }

    private class SystemInformationValue {
        // Container for System Information id and multiple values
        private int id;
        private List<String> values;

        public SystemInformationValue(int id, String... values) {
            // Add multiple values at once
            this.id = id;
            this.values = Arrays.asList(values);
        }

        public String toString(boolean newLine) {
            String result;
            if (getString(this.id).contains("%s")) {
                // Title already contains format specifiers
                result = String.format(Locale.ROOT, getString(this.id), this.values.toArray());
            } else {
                // With no format specifiers we add title: value standard format
                result = String.format(Locale.ROOT, "%s: %s", getString(this.id), this.values.get(0));
            }
            // Add a newline character if required
            if (newLine) {
                result += "\n";
            }
            return result;
        }
    }
}
