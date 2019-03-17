package com.muflone.android.django_hotels.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.api.tasks.AsyncTaskListener;
import com.muflone.android.django_hotels.NotifyMessage;
import com.muflone.android.django_hotels.R;
import com.muflone.android.django_hotels.api.ApiData;
import com.muflone.android.django_hotels.api.exceptions.InvalidDateTimeException;
import com.muflone.android.django_hotels.api.exceptions.InvalidResponseException;
import com.muflone.android.django_hotels.api.exceptions.NoConnectionException;
import com.muflone.android.django_hotels.api.exceptions.NoDownloadException;
import com.muflone.android.django_hotels.database.AppDatabase;

public class SyncFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View rootLayout = getActivity().findViewById(R.id.drawer_layout);
        System.out.println(Singleton.getInstance().api.getCurrentTokenCode());
        // Download data asynchronously from the server
        Singleton.getInstance().api.getData(new AsyncTaskListener<ApiData>() {
            @Override
            public void onSuccess(ApiData data) {
                if (getActivity() != null) {
                    /*
                     * During screen orientation changes the fragment could be called without any
                     * attached activity, therefore we check here for missing context
                     */
                    NotifyMessage.snackbar(rootLayout,
                            getString(R.string.message_established_connection),
                            Snackbar.LENGTH_INDEFINITE);
                }
                AppDatabase.destroyInstance();
            }

            @Override
            public void onFailure(Exception exception) {
                if (exception instanceof NoConnectionException) {
                    NotifyMessage.snackbar(rootLayout,
                            getString(R.string.message_no_server_connection),
                            Snackbar.LENGTH_INDEFINITE);
                } else if (exception instanceof InvalidDateTimeException) {
                    NotifyMessage.snackbar(rootLayout,
                            getString(R.string.message_invalid_date_time),
                            Snackbar.LENGTH_INDEFINITE);
                } else if (exception instanceof InvalidResponseException) {
                    NotifyMessage.snackbar(rootLayout,
                            getString(R.string.message_invalid_server_response),
                            Snackbar.LENGTH_INDEFINITE);
                } else if (exception instanceof NoDownloadException) {
                    NotifyMessage.snackbar(rootLayout,
                            getString(R.string.message_unable_to_download),
                            Snackbar.LENGTH_INDEFINITE);
                }
                AppDatabase.destroyInstance();
            }
        });

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.sync_fragment, container, false);
    }
}
