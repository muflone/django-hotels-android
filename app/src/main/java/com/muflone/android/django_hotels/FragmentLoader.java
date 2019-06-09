package com.muflone.android.django_hotels;

import android.support.v4.app.Fragment;

import com.muflone.android.django_hotels.activities.MainActivity;
import com.muflone.android.django_hotels.fragments.AboutFragment;
import com.muflone.android.django_hotels.fragments.ExtrasFragment;
import com.muflone.android.django_hotels.fragments.HomeFragment;
import com.muflone.android.django_hotels.fragments.ScannerFragment;
import com.muflone.android.django_hotels.fragments.SettingsFragment;
import com.muflone.android.django_hotels.fragments.StructuresFragment;
import com.muflone.android.django_hotels.fragments.SyncFragment;

public class FragmentLoader {
    public final static String FRAGMENT_HOME = HomeFragment.class.getSimpleName();
    public final static String FRAGMENT_SCANNER = ScannerFragment.class.getSimpleName();
    public final static String FRAGMENT_STRUCTURES = StructuresFragment.class.getSimpleName();
    public final static String FRAGMENT_EXTRA = ExtrasFragment.class.getSimpleName();
    public final static String FRAGMENT_SYNC = SyncFragment.class.getSimpleName();
    public final static String FRAGMENT_SETTINGS = SettingsFragment.class.getSimpleName();
    public final static String FRAGMENT_ABOUT = AboutFragment.class.getSimpleName();

    private static Fragment getFragment(String fragmentName) {
        // Create new fragment by its name
        Fragment result;
        if (fragmentName.equals(FRAGMENT_HOME)) {
            result = new HomeFragment();
        } else if (fragmentName.equals(FRAGMENT_SCANNER)) {
            result = new ScannerFragment();
        } else if (fragmentName.equals(FRAGMENT_STRUCTURES)) {
            result = new StructuresFragment();
        } else if (fragmentName.equals(FRAGMENT_EXTRA)) {
            result = new ExtrasFragment();
        } else if (fragmentName.equals(FRAGMENT_SYNC)) {
            result = new SyncFragment();
        } else if (fragmentName.equals(FRAGMENT_SETTINGS)) {
            result = new SettingsFragment();
        } else if (fragmentName.equals(FRAGMENT_ABOUT)) {
            result = new AboutFragment();
        } else {
            result = null;
        }
        return result;
    }

    public static Fragment loadFragment(MainActivity activity, int containerId, Fragment fragment) {
        if (fragment != null) {
            // Update navigation drawer
            activity.syncNavigationDrawer(fragment.getClass().getName());
            activity.setActiveFragment(fragment);
            // Load the selected fragment
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(containerId, fragment)
                    .commitAllowingStateLoss();
        }
        return fragment;
    }

    public static Fragment loadFragment(MainActivity activity, int containerId, String fragmentName) {
        // Load the selected fragment by its name
        Fragment fragment = FragmentLoader.getFragment(fragmentName);
        return FragmentLoader.loadFragment(activity, containerId, fragment);
    }
}
