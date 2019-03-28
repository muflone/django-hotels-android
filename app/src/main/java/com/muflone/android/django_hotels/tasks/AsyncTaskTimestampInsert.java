package com.muflone.android.django_hotels.tasks;

import android.os.AsyncTask;

import com.muflone.android.django_hotels.api.ApiData;
import com.muflone.android.django_hotels.database.AppDatabase;
import com.muflone.android.django_hotels.database.dao.TimestampDao;
import com.muflone.android.django_hotels.database.models.Timestamp;

public class AsyncTaskTimestampInsert extends AsyncTask<Timestamp, Integer, AsyncTaskResult<Void>> {
    private final AppDatabase database;
    private final AsyncTaskListener callback;

    public AsyncTaskTimestampInsert(AppDatabase database, AsyncTaskListener callback) {
        this.database = database;
        this.callback = callback;
    }

    @Override
    protected AsyncTaskResult doInBackground(Timestamp... params) {
        TimestampDao timestampDao = this.database.timestampDao();
        timestampDao.insert(params);
        return new AsyncTaskResult(null, this.callback, null);
    }

    @Override
    protected void onPostExecute(AsyncTaskResult<Void> results) {
        super.onPostExecute(results);
        // Check if callback listener was requested
        if (this.callback != null & results != null) {
            if (results.exception == null) {
                // Return flow to the caller
                this.callback.onSuccess(results);
            } else {
                // Failure with exception
                this.callback.onFailure(results.exception);
            }
        }
    }
}
