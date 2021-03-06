/*
 *     Project: Django Hotels Android
 * Description: The Android client companion app for Django Hotels
 *     Website: http://www.muflone.com/django-hotels-android/
 *      Author: Fabio Castelli (Muflone) <muflone@muflone.com>
 *   Copyright: 2018-2020 Fabio Castelli
 *     License: GPL-3+
 * Source code: https://github.com/muflone/django-hotels-android
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.muflone.android.django_hotels;

import android.support.v4.app.Fragment;

import com.muflone.android.django_hotels.activities.MainActivity;
import com.muflone.android.django_hotels.fragments.AboutFragment;
import com.muflone.android.django_hotels.fragments.ExtrasFragment;
import com.muflone.android.django_hotels.fragments.HomeFragment;
import com.muflone.android.django_hotels.fragments.ReportsFragment;
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
    public final static String FRAGMENT_REPORTS = ReportsFragment.class.getSimpleName();
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
        } else if (fragmentName.equals(FRAGMENT_REPORTS)) {
            result = new ReportsFragment();
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

    @SuppressWarnings("WeakerAccess")
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
        String title = null;
        if (fragmentName.equals(FRAGMENT_HOME)) {
            title = activity.getString(R.string.section_home);
        } else if (fragmentName.equals(FRAGMENT_SCANNER)) {
            title = activity.getString(R.string.section_scanner);
        } else if (fragmentName.equals(FRAGMENT_STRUCTURES)) {
            title = activity.getString(R.string.section_structures);
        } else if (fragmentName.equals(FRAGMENT_EXTRA)) {
            title = activity.getString(R.string.section_extras);
        } else if (fragmentName.equals(FRAGMENT_REPORTS)) {
            title = activity.getString(R.string.section_reports);
        } else if (fragmentName.equals(FRAGMENT_SYNC)) {
            title = activity.getString(R.string.section_sync);
        } else if (fragmentName.equals(FRAGMENT_SETTINGS)) {
            title = activity.getString(R.string.section_settings);
        } else if (fragmentName.equals(FRAGMENT_ABOUT)) {
            title = activity.getString(R.string.section_about);
        }
        // Change title bar
        Utility.setSupportActionBarTitle(activity, title);
        // Show fragment
        Fragment fragment = FragmentLoader.getFragment(fragmentName);
        return FragmentLoader.loadFragment(activity, containerId, fragment);
    }
}
