package com.muflone.android.django_hotels.tasks;

import android.os.AsyncTask;

import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.TimestampEmployeeItem;
import com.muflone.android.django_hotels.database.models.TimestampEmployee;
import com.muflone.android.django_hotels.fragments.ScannerFragment;

import java.util.List;

public class TaskTimestampListLatest extends AsyncTask<Long, Void, List<TimestampEmployee>> {
    private final List<TimestampEmployeeItem> timestampEmployeeList;
    private final ScannerFragment.TimestampAdapter timestampAdapter;
    private final Singleton singleton = Singleton.getInstance();

    @SuppressWarnings("WeakerAccess")
    public TaskTimestampListLatest(List<TimestampEmployeeItem> timestampEmployeeList,
                                   ScannerFragment.TimestampAdapter timestampAdapter) {
        this.timestampEmployeeList = timestampEmployeeList;
        this.timestampAdapter = timestampAdapter;
    }

    @Override
    protected List<TimestampEmployee> doInBackground(Long... params) {
        return this.singleton.database.timestampDao().listByLatestEnterExit(
                this.singleton.selectedDate,
                this.singleton.selectedStructure != null ? this.singleton.selectedStructure.id : 0,
                params[0]);
    }

    @Override
    protected void onPostExecute(List<TimestampEmployee> result) {
        // Reload the timestamps list
        this.timestampEmployeeList.clear();
        for (TimestampEmployee timestamp : result) {
            this.timestampEmployeeList.add(new TimestampEmployeeItem(
                    timestamp.id,
                    String.format("%s %s", timestamp.firstName, timestamp.lastName),
                    timestamp.datetime,
                    timestamp.direction,
                    timestamp.transmission));
        }
        this.timestampAdapter.notifyDataSetChanged();
    }
}

