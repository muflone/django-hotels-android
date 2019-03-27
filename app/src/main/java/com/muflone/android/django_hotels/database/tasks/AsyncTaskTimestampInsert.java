package com.muflone.android.django_hotels.database.tasks;

import android.os.AsyncTask;

import com.muflone.android.django_hotels.AsyncTaskListener;
import com.muflone.android.django_hotels.api.ApiData;
import com.muflone.android.django_hotels.database.AppDatabase;
import com.muflone.android.django_hotels.database.dao.TimestampDao;
import com.muflone.android.django_hotels.database.models.Timestamp;

public class AsyncTaskTimestampInsert extends AsyncTask<Timestamp, Integer, ApiData> {
    private final AppDatabase database;
    private final AsyncTaskListener callback;

    public AsyncTaskTimestampInsert(AppDatabase database, AsyncTaskListener callback) {
        this.database = database;
        this.callback = callback;
    }

    @Override
    protected ApiData doInBackground(Timestamp... params) {
        TimestampDao timestampDao = this.database.timestampDao();
        timestampDao.insert(params);
        return null;
    }

    @Override
    protected void onPostExecute(ApiData data) {
        super.onPostExecute(data);
        // Check if callback listener was requested
        if (this.callback != null & data != null) {
            if (data.exception == null) {
                // Return flow to the caller
                this.callback.onSuccess(data);
            } else {
                // Failure with exception
                this.callback.onFailure(data.exception);
            }
        }
    }
}
