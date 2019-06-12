package com.muflone.android.django_hotels.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.muflone.android.django_hotels.Constants;
import com.muflone.android.django_hotels.R;
import com.muflone.android.django_hotels.Settings;
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.Utility;
import com.muflone.android.django_hotels.api.Api;
import com.muflone.android.django_hotels.commands.CommandFactory;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loader_activity);
        // Singleton instance
        this.singleton.settings = new Settings(this);
        this.singleton.api = new Api();
        this.singleton.selectedDate = Utility.getCurrentDate();
        this.singleton.openDatabase(this);
        // Prepares CommandFactory for executing commands
        this.singleton.commandFactory = new CommandFactory();
        // Load UI
        this.loadUI();
        this.textViewAppName.setText(String.format(Locale.ROOT, "%s %s",
                this.singleton.settings.getApplicationName(),
                this.singleton.settings.getApplicationVersion()));
        // Reload data from database
        AppDatabase.getAppDatabase(this).reload(this, new AsyncTaskListener() {
            @Override
            public void onSuccess(AsyncTaskResult result) {
                // Execute APP BEGIN commands
                singleton.commandFactory.executeCommands(
                        LoaderActivity.this,
                        LoaderActivity.this.getBaseContext(),
                        Constants.CONTEXT_APP_BEGIN);
                // Select the first structure only if not already selected
                if (singleton.selectedStructure == null && singleton.apiData.structuresMap.size() > 0) {
                    // Select the first available structure
                    SortedSet<Structure> sortedStructures = new TreeSet<>(singleton.apiData.structuresMap.values());
                    singleton.selectedStructure = sortedStructures.first();
                }
                new LoaderActivityStartTask().execute(LoaderActivity.this);
            }

            @Override
            public void onFailure(Exception exception) {
            }

            @Override
            public void onProgress(int step, int total) {
            }
        });
    }

    private void loadUI() {
        this.textViewAppName = this.findViewById(R.id.textViewAppName);
    }

    private static class LoaderActivityStartTask extends AsyncTask<LoaderActivity, Void, LoaderActivity> {
        // Await a bit before loading
        @Override
        protected LoaderActivity doInBackground(LoaderActivity... params) {
            Utility.sleep(500);
            return params[0];
        }

        @Override
        protected void onPostExecute(LoaderActivity result) {
            super.onPostExecute(result);
            // Load the main activity
            result.startActivity(new Intent(result, MainActivity.class));
            result.finish();
        }
    }
}
