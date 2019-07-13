package com.muflone.android.django_hotels.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.muflone.android.django_hotels.R;
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.TimestampEmployeeItem;
import com.muflone.android.django_hotels.database.models.Timestamp;
import com.muflone.android.django_hotels.fragments.ScannerFragment;

import java.lang.ref.WeakReference;
import java.util.List;

public class TaskTimestampUnsetTransmission extends AsyncTask<Integer, Void, Void> {
    private final WeakReference<Context> context;
    private final List<TimestampEmployeeItem> timestampEmployeeList;
    private final ScannerFragment.TimestampAdapter timestampAdapter;
    private final Singleton singleton = Singleton.getInstance();

    @SuppressWarnings("WeakerAccess")
    public TaskTimestampUnsetTransmission(Context context,
                                          List<TimestampEmployeeItem> timestampEmployeeList,
                                          ScannerFragment.TimestampAdapter timestampAdapter) {
        this.context = new WeakReference<>(context);
        this.timestampEmployeeList = timestampEmployeeList;
        this.timestampAdapter = timestampAdapter;
    }

    @Override
    protected Void doInBackground(Integer... result) {
        // Update adapter
        int position = result[0];
        TimestampEmployeeItem timestampEmployeeItem = this.timestampEmployeeList.get(position);
        timestampEmployeeItem.transmission = null;
        // Update database
        Timestamp timestamp = this.singleton.database.timestampDao().findById(timestampEmployeeItem.id);
        timestamp.transmission = null;
        this.singleton.database.timestampDao().update(timestamp);
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        this.timestampAdapter.notifyDataSetChanged();
        Toast.makeText(this.context.get(),
                R.string.structures_marked_timestamp_as_untransmitted,
                Toast.LENGTH_SHORT).show();
    }
}
