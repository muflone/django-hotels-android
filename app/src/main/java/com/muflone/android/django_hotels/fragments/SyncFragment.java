package com.muflone.android.django_hotels.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.tasks.AsyncTaskDownload;
import com.muflone.android.django_hotels.tasks.AsyncTaskListener;
import com.muflone.android.django_hotels.NotifyMessage;
import com.muflone.android.django_hotels.R;
import com.muflone.android.django_hotels.api.exceptions.InvalidDateTimeException;
import com.muflone.android.django_hotels.api.exceptions.InvalidResponseException;
import com.muflone.android.django_hotels.api.exceptions.NoConnectionException;
import com.muflone.android.django_hotels.api.exceptions.NoDownloadException;
import com.muflone.android.django_hotels.database.AppDatabase;
import com.muflone.android.django_hotels.tasks.AsyncTaskResult;

public class SyncFragment extends Fragment {
    private Context context;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View rootLayout = ((Activity) this.context).findViewById(R.id.drawer_layout);
        // Download data asynchronously from the server
        AsyncTaskDownload task = new AsyncTaskDownload(
                Singleton.getInstance().api,
                AppDatabase.getAppDatabase(this.context),
                new AsyncTaskListener<AsyncTaskResult<Void>>() {
                    @Override
                    public void onSuccess(AsyncTaskResult<Void> results) {
                        NotifyMessage.snackbar(context, rootLayout,
                                context.getString(R.string.message_established_connection),
                                Snackbar.LENGTH_INDEFINITE);
                        // Reload data from database
                        AppDatabase.getAppDatabase(context).reload(context);
                        AppDatabase.destroyInstance();
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        if (exception instanceof NoConnectionException) {
                            NotifyMessage.snackbar(context, rootLayout,
                                    context.getString(R.string.message_no_server_connection),
                                    Snackbar.LENGTH_INDEFINITE);
                        } else if (exception instanceof InvalidDateTimeException) {
                            NotifyMessage.snackbar(context, rootLayout,
                                    context.getString(R.string.message_invalid_date_time),
                                    Snackbar.LENGTH_INDEFINITE);
                        } else if (exception instanceof InvalidResponseException) {
                            NotifyMessage.snackbar(context, rootLayout,
                                    context.getString(R.string.message_invalid_server_response),
                                    Snackbar.LENGTH_INDEFINITE);
                        } else if (exception instanceof NoDownloadException) {
                            NotifyMessage.snackbar(context, rootLayout,
                                    context.getString(R.string.message_unable_to_download),
                                    Snackbar.LENGTH_INDEFINITE);
                        }
                        AppDatabase.destroyInstance();
                    }
                });
        task.execute();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.sync_fragment, container, false);
    }

    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }
}
