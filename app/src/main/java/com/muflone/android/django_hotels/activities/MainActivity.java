/*
 *     Project: Django Hotels Android
 * Description: The Android client companion app for Django Hotels
 *     Website: http://www.muflone.com/django-hotels-android/
 *      Author: Fabio Castelli (Muflone) <muflone@muflone.com>
 *   Copyright: 2018-2019 Fabio Castelli
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

package com.muflone.android.django_hotels.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.muflone.android.django_hotels.FragmentLoader;
import com.muflone.android.django_hotels.R;
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.Utility;
import com.muflone.android.django_hotels.commands.CommandConstants;
import com.muflone.android.django_hotels.database.models.Structure;
import com.muflone.android.django_hotels.fragments.HomeFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private final String TAG = getClass().getSimpleName();
    private final Singleton singleton = Singleton.getInstance();
    private Fragment fragment = null;
    private DrawerLayout drawerLayout = null;
    private NavigationView navigationView = null;
    private Toolbar toolbar = null;
    private MenuItem menuItemHome = null;
    private MenuItem menuItemScanner = null;
    private MenuItem menuItemStructures = null;
    private MenuItem menuItemExtras = null;
    private MenuItem menuItemSync = null;
    private MenuItem menuItemSettings = null;
    private MenuItem menuItemAbout = null;
    private MenuItem toolButtonSetStructure = null;
    private MenuItem toolButtonSetDate = null;
    private boolean backButtonPressed = false;

    // Allow the use of (insecure) drawables for API < 21
    // https://developer.android.com/reference/android/support/v7/app/AppCompatDelegate.html#setCompatVectorFromResourcesEnabled(boolean)
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        // Initialize UI
        this.loadUI();
        // Add settings_toolbar
        this.setSupportActionBar(this.toolbar);
        // Add Navigation Drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, this.drawerLayout, this.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        this.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        this.navigationView.setNavigationItemSelectedListener(this);
        // Load preferences
        if (this.singleton.settings.getTabletID().isEmpty() |
                this.singleton.settings.getTabletKey().isEmpty()) {
            String message = this.singleton.settings.getTabletID().isEmpty() ?
                    this.getString(R.string.missing_tablet_id) :
                    this.getString(R.string.missing_tablet_key);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            this.onNavigationItemSelected(this.menuItemSettings);
        } else {
            this.onNavigationItemSelected(this.menuItemHome);
            // Grant write storage permission
            Utility.requestWriteStoragePermission(this);
        }
        invalidateOptionsMenu();
    }

    private void loadUI() {
        this.drawerLayout = this.findViewById(R.id.drawer_layout);
        this.navigationView = this.findViewById(R.id.navigation);
        this.toolbar = this.findViewById(R.id.toolbar);
        // Find the navigation drawer MenuItems
        Menu menu = this.navigationView.getMenu();
        for (int item = 0; item < menu.size(); item++) {
            switch (menu.getItem(item).getItemId()) {
                case R.id.menuItemHome:
                    this.menuItemHome = menu.getItem(item);
                    break;
                case R.id.menuItemScanner:
                    this.menuItemScanner = menu.getItem(item);
                    break;
                case R.id.menuItemStructures:
                    this.menuItemStructures = menu.getItem(item);
                    break;
                case R.id.menuItemExtras:
                    this.menuItemExtras = menu.getItem(item);
                    break;
                case R.id.menuItemSync:
                    this.menuItemSync = menu.getItem(item);
                    break;
                case R.id.menuItemSettings:
                    this.menuItemSettings = menu.getItem(item);
                    break;
                case R.id.menuItemAbout:
                    this.menuItemAbout = menu.getItem(item);
                    break;
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Save currently active fragment name
        if (this.fragment != null) {
            outState.putString("fragment", this.fragment.getClass().getSimpleName());
        }
        // Save currently selected structure ID
        outState.putLong("selectedStructure", this.singleton.selectedStructure != null ?
                this.singleton.selectedStructure.id: 0);
        // Save currently selected date
        outState.putLong("selectedDate", this.singleton.selectedDate.getTime());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore previously selected structure
        Long structureId = savedInstanceState.getLong("selectedStructure");
        this.singleton.selectedStructure = this.singleton.apiData.structuresMap.containsKey(structureId) ?
                this.singleton.apiData.structuresMap.get(structureId) : null;
        // Restore previously selected date
        this.singleton.selectedDate = new Date(savedInstanceState.getLong("selectedDate"));
        // Reload options menu
        this.invalidateOptionsMenu();
        // Restore previously active fragment
        String fragmentName = savedInstanceState.getString("fragment");
        this.setActiveFragment(FragmentLoader.loadFragment(this, R.id.fragment_container, Objects.requireNonNull(fragmentName)));
    }

    @Override
    public void onBackPressed() {
        // Handle back button press
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            // Close drawer if opened
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else if (! (this.fragment instanceof HomeFragment)) {
            // Move to the Home section
            this.onNavigationItemSelected(this.menuItemHome);
        } else if (! this.backButtonPressed) {
            // Show message instead of closing
            this.backButtonPressed = true;
            Toast.makeText(this, this.getString(R.string.press_back_again_to_exit),
                    Toast.LENGTH_SHORT).show();
            // Restore confirmation after 2 seconds
            new Handler().postDelayed(() -> backButtonPressed = false, 2000);
        } else {
            // Execute APP END commands
            this.singleton.commandFactory.executeCommands(
                    this,
                    this.getBaseContext(),
                    CommandConstants.CONTEXT_APP_END);
            // Accept back button to close the activity
            Log.d(this.TAG, "Closing database instance");
            this.singleton.database.destroyInstance();
            Log.d(this.TAG, "Closing current activity");
            super.onBackPressed();
            Log.d(this.TAG, "Closing any other activity");
            this.finishAffinity();
            Log.d(this.TAG, "End of the program");
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        String fragmentName = null;
        // Activate MenuItem
        item.setChecked(true);
        this.drawerLayout.closeDrawer(GravityCompat.START);
        // Open Fragment or related Activity
        if (item == this.menuItemHome) {
            fragmentName = FragmentLoader.FRAGMENT_HOME;
        } else if (item == this.menuItemScanner) {
            fragmentName = FragmentLoader.FRAGMENT_SCANNER;
        } else if (item == this.menuItemStructures) {
            fragmentName = FragmentLoader.FRAGMENT_STRUCTURES;
        } else if (item == this.menuItemExtras) {
            fragmentName = FragmentLoader.FRAGMENT_EXTRA;
        } else if (item == this.menuItemSync) {
            fragmentName = FragmentLoader.FRAGMENT_SYNC;
        } else if (item == this.menuItemSettings) {
            fragmentName = FragmentLoader.FRAGMENT_SETTINGS;
        } else if (item == this.menuItemAbout) {
            fragmentName = FragmentLoader.FRAGMENT_ABOUT;
        }
        this.setActiveFragment(FragmentLoader.loadFragment(this, R.id.fragment_container, Objects.requireNonNull(fragmentName)));
        return this.fragment != null;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Check if the option menu was already built
        if (this.toolButtonSetStructure != null) {
            // Assign structure to the toolbar button
            this.toolButtonSetStructure.setTitle(this.singleton.selectedStructure != null ?
                    this.singleton.selectedStructure.name :
                    this.getString(R.string.no_available_structures));
        }
        // Set the date on option menu
        if (this.toolButtonSetDate != null) {
            this.toolButtonSetDate.setTitle("  " + this.singleton.defaultDateFormatter.format(
                    this.singleton.selectedDate));
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Add settings_toolbar icons
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_toolbar, menu);
        this.toolButtonSetStructure = menu.findItem(R.id.toolButtonSetStructure);
        this.toolButtonSetDate = menu.findItem(R.id.toolButtonSetDate);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle ToolButtons clicks
        switch (item.getItemId()) {
            case R.id.toolButtonSync: {
                this.onNavigationItemSelected(this.menuItemSync);
                return true;
            }
            case R.id.toolButtonSetStructure: {
                // Change current structure
                // Show contextual menu for services
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.select_a_structure);
                // Sort structures list
                List<String> structuresList = new ArrayList<>();
                List<Long> structureIdList = new ArrayList<>();
                SortedSet<Structure> sortedStructures = new TreeSet<>(this.singleton.apiData.structuresMap.values());
                for (Structure structure : sortedStructures) {
                    structuresList.add(structure.name);
                    structureIdList.add(structure.id);
                }
                builder.setItems(structuresList.toArray(new String[0]), (dialog, position) -> {
                    // Get the selected structure and update the user interface
                    this.singleton.selectedStructure = this.singleton.apiData.structuresMap.get(structureIdList.get(position));
                    invalidateOptionsMenu();
                    dialog.dismiss();
                    // Reload fragment
                    Utility.reloadFragment(MainActivity.this, fragment);
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
            case R.id.toolButtonSetDate: {
                // Change current date
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(this.singleton.selectedDate);
                DatePickerDialog dialog = new DatePickerDialog(MainActivity.this,
                        (view, year, month, day) -> {
                            calendar.set(year, month, day);
                            toolButtonSetDate.setTitle("  " + this.singleton.defaultDateFormatter.format(calendar.getTime()));
                            this.singleton.selectedDate = calendar.getTime();
                            FragmentLoader.loadFragment(
                                    MainActivity.this,
                                    R.id.fragment_container,
                                    fragment.getClass().getSimpleName());
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
                return true;
            }
            default: {
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
            }
        }
    }

    public void syncNavigationDrawer(String fragmentName) {
        // Activate the menu on the navigation drawer
        if (fragmentName.equals(FragmentLoader.FRAGMENT_HOME)) {
            this.menuItemHome.setChecked(true);
        } else if (fragmentName.equals(FragmentLoader.FRAGMENT_SCANNER)) {
            this.menuItemScanner.setChecked(true);
        } else if (fragmentName.equals(FragmentLoader.FRAGMENT_STRUCTURES)) {
            this.menuItemStructures.setChecked(true);
        } else if (fragmentName.equals(FragmentLoader.FRAGMENT_EXTRA)) {
            this.menuItemExtras.setChecked(true);
        } else if (fragmentName.equals(FragmentLoader.FRAGMENT_SYNC)) {
            this.menuItemSync.setChecked(true);
        } else if (fragmentName.equals(FragmentLoader.FRAGMENT_SETTINGS)) {
            this.menuItemSettings.setChecked(true);
        } else if (fragmentName.equals(FragmentLoader.FRAGMENT_ABOUT)) {
            this.menuItemAbout.setChecked(true);
        }
    }

    public void setActiveFragment(Fragment fragment) {
        this.fragment = fragment;
    }
}
