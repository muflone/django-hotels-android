package com.muflone.android.django_hotels.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.Toast;

import com.muflone.android.django_hotels.Settings;
import com.muflone.android.django_hotels.R;
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.api.Api;
import com.muflone.android.django_hotels.database.AppDatabase;
import com.muflone.android.django_hotels.fragments.AboutFragment;
import com.muflone.android.django_hotels.fragments.ExtrasFragment;
import com.muflone.android.django_hotels.fragments.HomeFragment;
import com.muflone.android.django_hotels.fragments.ScannerFragment;
import com.muflone.android.django_hotels.fragments.SettingsFragment;
import com.muflone.android.django_hotels.fragments.StructuresFragment;
import com.muflone.android.django_hotels.fragments.SyncFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private final Singleton singleton = Singleton.getInstance();
    private Fragment fragment = null;
    private MenuItem menuItemHome = null;
    private MenuItem menuItemSettings = null;
    private MenuItem menuItemSync = null;
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
        // Singleton instance
        Settings settings = new Settings(this);
        singleton.settings = settings;
        singleton.api = new Api();
        singleton.selectedDate = singleton.api.getCurrentDate();
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
                case R.id.menuItemHome:
                    this.menuItemHome = menu.getItem(item);
                    break;
                case R.id.menuItemSettings:
                    this.menuItemSettings = menu.getItem(item);
                    break;
                case R.id.menuItemSync:
                    this.menuItemSync = menu.getItem(item);
                    break;
            }
        }
        // Load preferences
        if (settings.getTabletID().isEmpty() |
                settings.getTabletKey().isEmpty()) {
            String message = settings.getTabletID().isEmpty() ?
                    this.getString(R.string.missing_tablet_id) :
                    this.getString(R.string.missing_tablet_key);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            this.onNavigationItemSelected(this.menuItemSettings);
        } else {
            this.onNavigationItemSelected(this.menuItemHome);
        }
        // Reload data from database
        AppDatabase.getAppDatabase(this).reload(this, null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Save active fragment name
        if (this.fragment != null) {
            outState.putString("fragment", this.fragment.getClass().getSimpleName());
        }
        // Save selected date
        outState.putLong("selectedDate", this.singleton.selectedDate.getTime());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore previous selected date
        this.singleton.selectedDate = new Date(savedInstanceState.getLong("selectedDate"));
        // Restore previous active fragment
        String fragmentName = savedInstanceState.getString("fragment");
        if (fragmentName != null) {
            Fragment fragment = this.newFragmentByName(fragmentName);
            if (fragment != null) {
                LoadFragment(fragment);
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Handle back button press
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            // Close drawer if opened
            drawer.closeDrawer(GravityCompat.START);
        } else if (! (this.fragment instanceof HomeFragment)) {
            // Move to the Home section
            this.onNavigationItemSelected(this.menuItemHome);
        } else if (! this.backButtonPressed) {
            // Show message instead of closing
            this.backButtonPressed = true;
            Toast.makeText(this, this.getString(R.string.press_back_again_to_exit),
                    Toast.LENGTH_SHORT).show();
            // Restore confirmation after 2 seconds
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backButtonPressed = false;
                }
            }, 2000);
        } else {
            // Accept back button to close the activity
            AppDatabase.destroyInstance();
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
            case R.id.menuItemHome:
                fragment = new HomeFragment();
                break;
            case R.id.menuItemScanner:
                fragment = new ScannerFragment();
                break;
            case R.id.menuItemStructures:
                fragment = new StructuresFragment();
                break;
            case R.id.menuItemExtras:
                fragment = new ExtrasFragment();
                break;
            case R.id.menuItemSync:
                fragment = new SyncFragment();
                break;
            case R.id.menuItemShortcut:
                fragment = new HomeFragment();
                Intent intent = new Intent(this, CreateShortcutActivity.class);
                this.startActivity(intent);
                break;
            case R.id.menuItemSettings:
                fragment = new SettingsFragment();
                break;
            case R.id.menuItemAbout:
                fragment = new AboutFragment();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return LoadFragment(fragment);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Initialize date menu
        this.toolButtonSetDate = menu.findItem(R.id.toolButtonSetDate);
        this.toolButtonSetDate.setTitle("  " + new SimpleDateFormat("yyyy-MM-dd").format(
                singleton.selectedDate));
        return super.onPrepareOptionsMenu(menu);
    }

    private Fragment newFragmentByName(String fragmentName) {
        // Create new fragment by its name
        Fragment result;
        switch (fragmentName) {
            case "HomeFragment":
                result = new HomeFragment();
                break;
            case "ScannerFragment":
                result = new ScannerFragment();
                break;
            case "StructuresFragment":
                result = new StructuresFragment();
                break;
            case "ExtrasFragment":
                result = new ExtrasFragment();
                break;
            case "SyncFragment":
                result = new SyncFragment();
                break;
            case "SettingsFragment":
                result = new SettingsFragment();
                break;
            case "AboutFragment":
                result = new AboutFragment();
                break;
            default:
                result = null;
                break;
        }
        return result;
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
        // Handle ToolButtons clicks
        switch (item.getItemId()) {
            case R.id.toolButtonSync:
                this.onNavigationItemSelected(this.menuItemSync);
                return true;
            case R.id.toolButtonSetDate:
                // Change current date
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(Singleton.getInstance().selectedDate);
                DatePickerDialog dialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                calendar.set(year, month, day);
                                toolButtonSetDate.setTitle("  " + new SimpleDateFormat(
                                        "yyyy-MM-dd").format(calendar.getTime()));
                                singleton.selectedDate = calendar.getTime();
                                LoadFragment(newFragmentByName(fragment.getClass().getSimpleName()));
                            }
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}
