package com.muflone.android.django_hotels.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.muflone.android.django_hotels.R;

public class Main extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_about:
                /* Show the about activity */
                Intent intent = new Intent(this, About.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            String title = "";
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    title = getString(R.string.navigation_title_home);
                    break;
                case R.id.navigation_dashboard:
                    title = getString(R.string.navigation_title_structures);
                    break;
                case R.id.navigation_notifications:
                    title = getString(R.string.navigation_title_extra);
                    break;
            }
            if (title.length() > 0) {
                Toast.makeText(Main.this, title, Toast.LENGTH_SHORT).show();
                return true;
            }
            else {
                return false;
            }
        }
    };

}
