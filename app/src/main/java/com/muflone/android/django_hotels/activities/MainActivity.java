package com.muflone.android.django_hotels.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.muflone.android.django_hotels.Preferences;
import com.muflone.android.django_hotels.R;
import com.muflone.android.django_hotels.fragments.AboutFragment;
import com.muflone.android.django_hotels.fragments.ExtrasFragment;
import com.muflone.android.django_hotels.fragments.HomeFragment;
import com.muflone.android.django_hotels.fragments.StructuresFragment;
import com.muflone.android.django_hotels.fragments.SyncFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private static final int PREFERENCES_RETURN_CODE = 1;
    Preferences preferences = null;
    Fragment fragment = null;
    MenuItem menuItemHome = null;
    MenuItem menuItemPreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        // Add preferences_toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Add Navigation Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        // Find the preferences MenuItems
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);
        Menu menu = navigationView.getMenu();
        for (int item = 0; item < menu.size(); item++) {
            switch (menu.getItem(item).getItemId()) {
                case R.id.menuitemHome:
                    this.menuItemHome = menu.getItem(item);
                    break;
                case R.id.menuitemSettings:
                    this.menuItemPreferences = menu.getItem(item);
                    break;
            }
        }
        // Load prerefences
        this.preferences = new Preferences(this);
        /*
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.tablet_id = sharedPreferences.getString(getString(R.string.preferences_tablet_id_id), "");
        this.tablet_key = sharedPreferences.getString(getString(R.string.preferences_tablet_key_id), "");
        */
        Log.d(TAG, "ID: " + this.preferences.getTabletID());
        Log.d(TAG, "Key: " + this.preferences.getTabletKey());
        if (this.preferences.getTabletID().isEmpty() |
                this.preferences.getTabletKey().isEmpty()) {
            String message = this.preferences.getTabletID().isEmpty() ?
                    getString(R.string.message_missing_tablet_id) :
                    getString(R.string.message_missing_tablet_key);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            this.onNavigationItemSelected(this.menuItemPreferences);
        } else {
            this.onNavigationItemSelected(this.menuItemHome);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Save active fragment name
        if (this.fragment != null) {
            outState.putString("fragment", this.fragment.getClass().getSimpleName());
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore previous active fragment
        String fragmentName = savedInstanceState.getString("fragment");
        if (fragmentName != null) {
            switch (fragmentName) {
                case "HomeFragment":
                    this.fragment = new HomeFragment();
                    break;
                case "StructuresFragment":
                    this.fragment = new StructuresFragment();
                    break;
                case "ExtrasFragment":
                    this.fragment = new ExtrasFragment();
                    break;
                case "AboutFragment":
                    this.fragment = new AboutFragment();
                    break;
                default:
                    Toast.makeText(this, fragmentName, Toast.LENGTH_SHORT).show();

            }
            if (this.fragment != null) {
                LoadFragment(this.fragment);
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Close drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        this.fragment = null;
        // Activate MenuItem
        item.setChecked(true);
        // Open Fragment or related Activity
        switch (item.getItemId()) {
            case R.id.menuitemHome:
                this.fragment = new HomeFragment();
                break;
            case R.id.menuitemStructures:
                this.fragment = new StructuresFragment();
                break;
            case R.id.menuitemExtras:
                this.fragment = new ExtrasFragment();
                break;
            case R.id.menuitemSettings:
                Intent intent = new Intent(this, PreferencesActivity.class);
                startActivityForResult(intent, PREFERENCES_RETURN_CODE);
                break;
            case R.id.menuitemAbout:
                this.fragment = new AboutFragment();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return LoadFragment(this.fragment);
    }

    private boolean LoadFragment(Fragment fragment) {
        if (this.fragment != null) {
            // Load the selected fragment
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, this.fragment)
                    .commitAllowingStateLoss();
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Add preferences_toolbar icons
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle toolbuttons clicks
        switch (item.getItemId()) {
            case R.id.section_sync:
                LoadFragment(new SyncFragment());
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Open HomeFragment after closing the preferences activity
        if (requestCode == PREFERENCES_RETURN_CODE) {
            this.onNavigationItemSelected(this.menuItemHome);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
