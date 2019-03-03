package com.muflone.android.django_hotels.fragments;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;

import com.muflone.android.django_hotels.R;

public class PreferencesFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        PreferenceManager.setDefaultValues(getActivity(),
                R.xml.preferences, false);
        // Inflate the preferences menu for this fragment
        addPreferencesFromResource(R.xml.preferences);
    }
}
