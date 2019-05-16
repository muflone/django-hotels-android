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

import com.muflone.android.django_hotels.FragmentLoader;
import com.muflone.android.django_hotels.Settings;
import com.muflone.android.django_hotels.R;
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.api.Api;
import com.muflone.android.django_hotels.database.AppDatabase;
import com.muflone.android.django_hotels.fragments.HomeFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
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
    private MenuItem menuItemShortcut = null;
    private MenuItem menuItemSettings = null;
    private MenuItem menuItemAbout = null;
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
                case R.id.menuItemShortcut:
                    this.menuItemShortcut = menu.getItem(item);
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
        this.setActiveFragment(FragmentLoader.loadFragment(this, R.id.fragment_container, fragmentName));
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
        } else if (item == this.menuItemShortcut) {
            fragmentName = FragmentLoader.FRAGMENT_HOME;
            Intent intent = new Intent(this, CreateShortcutActivity.class);
            this.startActivity(intent);
        } else if (item == this.menuItemSettings) {
            fragmentName = FragmentLoader.FRAGMENT_SETTINGS;
        } else if (item == this.menuItemAbout) {
            fragmentName = FragmentLoader.FRAGMENT_ABOUT;
        }
        this.setActiveFragment(FragmentLoader.loadFragment(this, R.id.fragment_container, fragmentName));
        return this.fragment != null;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Initialize date menu
        this.toolButtonSetDate = menu.findItem(R.id.toolButtonSetDate);
        this.toolButtonSetDate.setTitle("  " + new SimpleDateFormat("yyyy-MM-dd").format(
                singleton.selectedDate));
        return super.onPrepareOptionsMenu(menu);
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
                                FragmentLoader.loadFragment(
                                        MainActivity.this,
                                        R.id.fragment_container,
                                        fragment.getClass().getSimpleName());
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
