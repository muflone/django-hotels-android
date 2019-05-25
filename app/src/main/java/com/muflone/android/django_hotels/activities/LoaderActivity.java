package com.muflone.android.django_hotels.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.muflone.android.django_hotels.Constants;
import com.muflone.android.django_hotels.R;
import com.muflone.android.django_hotels.Settings;
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.Utility;
import com.muflone.android.django_hotels.api.Api;
import com.muflone.android.django_hotels.database.AppDatabase;
import com.muflone.android.django_hotels.database.models.Structure;
import com.muflone.android.django_hotels.tasks.AsyncTaskListener;
import com.muflone.android.django_hotels.tasks.AsyncTaskResult;

import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

public class LoaderActivity extends AppCompatActivity {
    private final Singleton singleton = Singleton.getInstance();
    private TextView textViewAppName;
    private ProgressBar progressBarLoading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loader_activity);
        // Load UI
        this.loadUI();
        this.textViewAppName.setText(String.format(Locale.ROOT, "%s %s",
                this.getString(R.string.app_name), this.getString(R.string.app_version)));
        // Singleton instance
        Settings settings = new Settings(this);
        singleton.settings = settings;
        singleton.api = new Api();
        singleton.selectedDate = Utility.getCurrentDate();
        // Reload data from database
        AppDatabase.getAppDatabase(this).reload(this, new AsyncTaskListener() {
            @Override
            public void onSuccess(AsyncTaskResult result) {
                // Select the first structure only if not already selected
                if (singleton.selectedStructure == null && singleton.apiData.structuresMap.size() > 0) {
                    // Select the first available structure
                    SortedSet<Structure> sortedStructures = new TreeSet<>(singleton.apiData.structuresMap.values());
                    singleton.selectedStructure = sortedStructures.first();
                }
                // Await a bit before loading
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        Utility.sleep(500);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        super.onPostExecute(result);
                        // Load the main activity
                        startActivity(new Intent(LoaderActivity.this, MainActivity.class));
                        finish();
                    }
                }.execute();
            }

            @Override
            public void onFailure(Exception e) {
            }

            @Override
            public void onProgress(int step, int total) {
            }
        });
    }

    private void loadUI() {
        this.textViewAppName = this.findViewById(R.id.textViewAppName);
        this.progressBarLoading = this.findViewById(R.id.progressBarLoading);
    }
}
