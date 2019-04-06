package com.muflone.android.django_hotels.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;

import com.muflone.android.django_hotels.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    private Context context;

    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        PreferenceManager.setDefaultValues(this.context,
                R.xml.settings, false);
        // Inflate the settings menu for this fragment
        setPreferencesFromResource(R.xml.settings, rootKey);
    }
}
