package com.muflone.android.django_hotels.tasks;

import android.os.AsyncTask;

import com.muflone.android.django_hotels.database.AppDatabase;
import com.muflone.android.django_hotels.database.dao.TimestampDao;
import com.muflone.android.django_hotels.database.models.Timestamp;
import com.muflone.android.django_hotels.database.models.TimestampEmployee;

import java.util.List;

public class AsyncTaskTimestampListByLatest extends AsyncTask<Long, Integer, AsyncTaskResult<List<TimestampEmployee>>> {
    private final AppDatabase database;
    private final AsyncTaskListener callback;

    public AsyncTaskTimestampListByLatest(AppDatabase database, AsyncTaskListener callback) {
        this.database = database;
        this.callback = callback;
    }

    @Override
    protected AsyncTaskResult doInBackground(Long... params) {
        TimestampDao timestampDao = this.database.timestampDao();
        List<TimestampEmployee> results = timestampDao.listByLatest(params[0]);
        return new AsyncTaskResult(results, this.callback, null);
    }

    @Override
    protected void onPostExecute(AsyncTaskResult<List<TimestampEmployee>> results) {
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
