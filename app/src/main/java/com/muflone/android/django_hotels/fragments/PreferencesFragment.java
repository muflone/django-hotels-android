package com.muflone.android.django_hotels.fragments;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.muflone.android.django_hotels.R;

public class PreferencesFragment extends PreferenceFragmentCompat  {
    public PreferencesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Inflate the preferences menu for this fragment
        addPreferencesFromResource(R.xml.preferences);
    }

}
