package com.muflone.android.django_hotels.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.muflone.android.django_hotels.Settings;
import com.muflone.android.django_hotels.R;
import com.muflone.android.django_hotels.api.Api;
import com.muflone.android.django_hotels.api.ApiData;
import com.muflone.android.django_hotels.api.tasks.AsyncTaskListener;
import com.muflone.android.django_hotels.database.models.Structure;
import com.muflone.android.django_hotels.fragments.AboutFragment;
import com.muflone.android.django_hotels.fragments.ExtrasFragment;
import com.muflone.android.django_hotels.fragments.HomeFragment;
import com.muflone.android.django_hotels.fragments.StructuresFragment;
import com.muflone.android.django_hotels.fragments.SyncFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private static final int SETTINGS_RETURN_CODE = 1;
    private Api api;
    Settings settings = null;
    Fragment fragment = null;
    MenuItem menuItemHome = null;
    MenuItem menuItemSettings = null;
    MenuItem menuItemSync = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        // Add settings_toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Add Navigation Drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        // Find the settings MenuItems
        NavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);
        Menu menu = navigationView.getMenu();
        for (int item = 0; item < menu.size(); item++) {
            switch (menu.getItem(item).getItemId()) {
                case R.id.menuitemHome:
                    this.menuItemHome = menu.getItem(item);
                    break;
                case R.id.menuitemSettings:
                    this.menuItemSettings = menu.getItem(item);
                    break;
                case R.id.menuitemSync:
                    this.menuItemSync = menu.getItem(item);
                    break;
            }
        }
        // Load preferences
        this.settings = new Settings(this);
        Log.d(TAG, "ID: " + this.settings.getTabletID());
        Log.d(TAG, "Key: " + this.settings.getTabletKey());
        if (this.settings.getTabletID().isEmpty() |
                this.settings.getTabletKey().isEmpty()) {
            String message = this.settings.getTabletID().isEmpty() ?
                    getString(R.string.message_missing_tablet_id) :
                    getString(R.string.message_missing_tablet_key);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            this.onNavigationItemSelected(this.menuItemSettings);
        } else {
            this.onNavigationItemSelected(this.menuItemHome);
        }
        // Load data from database
        this.api = new Api(this);
        this.api.loadFromDatabase(new AsyncTaskListener<ApiData>() {
            @Override
            public void onSuccess(ApiData data) {
                for (Structure structure : data.structures) {
                    System.out.println(structure.name);
                }
            }

            @Override
            public void onFailure(Exception exception) {

            }
        });
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
        Fragment fragment = null;
        if (fragmentName != null) {
            switch (fragmentName) {
                case "HomeFragment":
                    fragment = new HomeFragment();
                    break;
                case "StructuresFragment":
                    fragment = new StructuresFragment();
                    break;
                case "ExtrasFragment":
                    fragment = new ExtrasFragment();
                    break;
                case "SyncFragment":
                    fragment = new SyncFragment();
                    break;
                case "AboutFragment":
                    fragment = new AboutFragment();
                    break;
                default:
                    Toast.makeText(this, fragmentName, Toast.LENGTH_SHORT).show();

            }
            if (fragment != null) {
                LoadFragment(fragment);
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Close drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment fragment = null;
        // Activate MenuItem
        item.setChecked(true);
        // Open Fragment or related Activity
        switch (item.getItemId()) {
            case R.id.menuitemHome:
                fragment = new HomeFragment();
                break;
            case R.id.menuitemStructures:
                fragment = new StructuresFragment();
                break;
            case R.id.menuitemExtras:
                fragment = new ExtrasFragment();
                break;
            case R.id.menuitemSync:
                fragment = new SyncFragment();
                break;
            case R.id.menuitemSettings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivityForResult(intent, SETTINGS_RETURN_CODE);
                break;
            case R.id.menuitemAbout:
                fragment = new AboutFragment();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return LoadFragment(fragment);
    }

    private boolean LoadFragment(Fragment fragment) {
        if (fragment != null) {
            // Load the selected fragment
            this.fragment = fragment;
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
        // Add settings_toolbar icons
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle toolbuttons clicks
        switch (item.getItemId()) {
            case R.id.section_sync:
                this.onNavigationItemSelected(this.menuItemSync);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Open HomeFragment after closing the settings activity
        if (requestCode == SETTINGS_RETURN_CODE) {
            this.onNavigationItemSelected(this.menuItemHome);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
