package com.muflone.android.django_hotels.fragments;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;

import com.muflone.android.django_hotels.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        PreferenceManager.setDefaultValues(getActivity(),
                R.xml.settings, false);
        // Inflate the settings menu for this fragment
        setPreferencesFromResource(R.xml.settings, rootKey);
    }
}
