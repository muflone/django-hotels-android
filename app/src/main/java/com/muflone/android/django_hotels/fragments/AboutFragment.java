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

import java.util.Calendar;
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
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return new AboutPage(this.context)
                .isRTL(false)
                .setImage(R.mipmap.ic_launcher_round)
                .setDescription(String.format(Locale.ROOT,
                        "%s %s\n%s",
                        this.getString(R.string.app_name),
                        this.getString(R.string.app_version),
                        this.getString(R.string.app_description)))
                .addPlayStore(this.getString(R.string.about_app_playstore))
                .addGitHub(this.getString(R.string.about_app_github))
                // Contacts section
                .addGroup(this.getString(R.string.about_contacts))
                .addEmail(this.getString(R.string.author_email))
                .addWebsite(this.getString(R.string.author_web))
                .addTwitter(this.getString(R.string.author_twitter))
                // License section
                .addGroup(this.getString(R.string.about_license))
                .addItem(this.getCopyRightsElement())
                .addWebsite(this.getString(R.string.about_license_url),
                        this.getString(R.string.about_app_license))
                // Credits section
                .addGroup(this.getString(R.string.about_credits))
                .addGitHub(this.getString(R.string.about_zxing_url),
                        this.getString(R.string.about_zxing_title))
                .addGitHub(this.getString(R.string.about_about_url),
                        this.getString(R.string.about_about_title))
                .addGitHub(this.getString(R.string.about_guava_url),
                        this.getString(R.string.about_guava_title))
                .addGitHub(this.getString(R.string.about_numberprogressbar_url),
                        this.getString(R.string.about_numberprogressbar_title))
                // System Information section
                .addGroup(this.getString(R.string.about_system_information))
                .addItem(this.newSystemInformationElement(
                        R.string.about_system_information_android,
                        Build.VERSION.RELEASE,
                        Build.ID))
                .addItem(this.newSystemInformationElement(
                        R.string.about_system_information_sdk,
                        String.valueOf(Build.VERSION.SDK_INT)))
                .addItem(this.newSystemInformationElement(
                        R.string.about_system_information_rom, Build.VERSION.INCREMENTAL))
                .addItem(this.newSystemInformationElement(
                        R.string.about_system_information_kernel,
                        System.getProperty("os.version")))
                .addItem(this.newSystemInformationElement(
                        R.string.about_system_information_architecture,
                        System.getProperty("os.arch")))
                .addItem(this.newSystemInformationElement(
                        R.string.about_system_information_manufacturer, Build.MANUFACTURER))
                .addItem(this.newSystemInformationElement(
                        R.string.about_system_information_model, Build.MODEL))
                .addItem(this.newSystemInformationElement(
                        R.string.about_system_information_product, Build.PRODUCT))
                .addItem(this.newSystemInformationElement(
                        R.string.about_system_information_brand, Build.BRAND))
                .addItem(this.newSystemInformationElement(
                        R.string.about_system_information_device, Build.DEVICE))
                .addItem(this.newSystemInformationElement(
                        R.string.about_system_information_resolution,
                        String.valueOf(metrics.widthPixels),
                        String.valueOf(metrics.heightPixels),
                        String.valueOf(metrics.density)))
                .addItem(this.newSystemInformationElement(
                        R.string.about_system_information_timezone,
                        TimeZone.getDefault().getID(),
                        TimeZone.getDefault().getDisplayName(Locale.ROOT)))
                .addItem(this.newSystemInformationElement(
                        R.string.about_system_information_android_id,
                        Settings.Secure.getString(
                                Objects.requireNonNull(this.getActivity()).getBaseContext().getContentResolver(),
                                Settings.Secure.ANDROID_ID)))
                .addItem(this.newSystemInformationElement(
                        R.string.about_system_information_user_agent,
                        System.getProperty("http.agent")))
                .create();
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

    private Element newSystemInformationElement(int resId, String... values) {
        // Return an element for System Information
        return new Element()
                .setIconDrawable(R.drawable.ic_info)
                .setTitle(String.format(Locale.ROOT, this.getString(resId), values));
    }

    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }
}
