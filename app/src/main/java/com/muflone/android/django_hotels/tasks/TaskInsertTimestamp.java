package com.muflone.android.django_hotels.tasks;

import android.os.AsyncTask;

import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.database.dao.TimestampDao;
import com.muflone.android.django_hotels.database.models.Timestamp;

public class TaskInsertTimestamp extends AsyncTask<Timestamp, Void, Void> {
    private final TaskListenerInterface listener;
    private final Singleton singleton = Singleton.getInstance();

    public TaskInsertTimestamp(TaskListenerInterface listener) {
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Timestamp... params) {
        // Insert new Timestamp
        TimestampDao timestampDao = this.singleton.database.timestampDao();
        timestampDao.insert(params);
        this.listener.onSuccess(null);
        return null;
    }
}

