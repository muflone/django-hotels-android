package com.muflone.android.django_hotels.tasks;

import android.content.Intent;
import android.os.AsyncTask;

import com.muflone.android.django_hotels.Utility;
import com.muflone.android.django_hotels.activities.LoaderActivity;
import com.muflone.android.django_hotels.activities.MainActivity;

public class TaskLoaderActivityStart extends AsyncTask<LoaderActivity, Void, LoaderActivity> {
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