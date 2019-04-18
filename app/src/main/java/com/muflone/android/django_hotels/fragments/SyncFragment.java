package com.muflone.android.django_hotels.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.tasks.AsyncTaskSync;
import com.muflone.android.django_hotels.tasks.AsyncTaskListener;
import com.muflone.android.django_hotels.R;
import com.muflone.android.django_hotels.api.exceptions.InvalidDateTimeException;
import com.muflone.android.django_hotels.api.exceptions.InvalidResponseException;
import com.muflone.android.django_hotels.api.exceptions.NoConnectionException;
import com.muflone.android.django_hotels.api.exceptions.NoDownloadException;
import com.muflone.android.django_hotels.database.AppDatabase;
import com.muflone.android.django_hotels.tasks.AsyncTaskResult;

import java.util.ArrayList;
import java.util.List;

public class SyncFragment extends Fragment {
    private Context context;
    private NumberProgressBar progressBar;
    private ProgressBar progressBar2;
    private TextView progressView;
    private TextView errorMessage;
    private ImageView errorView;
    private List<String> progressPhases = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sync_fragment, container, false);
        this.progressBar = rootView.findViewById(R.id.progressBar);
        this.progressBar.setMax(AsyncTaskSync.totalSteps);
        this.progressBar2 = rootView.findViewById(R.id.progressBar2);
        this.progressView = rootView.findViewById(R.id.progressView);
        this.errorMessage = rootView.findViewById(R.id.errorMessage);
        this.errorMessage.setText("");
        this.errorView = rootView.findViewById(R.id.errorView);
        this.errorView.setVisibility(View.INVISIBLE);
        this.progressPhases.add(this.context.getString(R.string.sync_step_timestamps_transmission));
        this.progressPhases.add(this.context.getString(R.string.sync_step_activities_transmission));
        this.progressPhases.add(this.context.getString(R.string.sync_step_download));
        this.progressPhases.add(this.context.getString(R.string.sync_step_completed));

        // Download data asynchronously from the server
        AsyncTaskSync task = new AsyncTaskSync(
                Singleton.getInstance().api,
                AppDatabase.getAppDatabase(this.context),
                new AsyncTaskListener() {
                    @Override
                    public void onSuccess(AsyncTaskResult result) {
                        // Reload data from database
                        AppDatabase.getAppDatabase(context).reload(context);
                        errorView.setImageDrawable(
                                context.getResources().getDrawable(R.drawable.ic_check_ok));
                        progressBar2.setVisibility(View.INVISIBLE);
                        errorView.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onFailure(Exception exception) {
                        if (exception instanceof NoConnectionException) {
                            errorMessage.setText(R.string.sync_error_no_server_connection);
                            errorView.setImageDrawable(
                                    context.getResources().getDrawable(R.drawable.ic_error_signal));
                        } else if (exception instanceof InvalidDateTimeException) {
                            errorMessage.setText(R.string.sync_error_invalid_date_time);
                            errorView.setImageDrawable(
                                    context.getResources().getDrawable(R.drawable.ic_error_date));
                        } else if (exception instanceof InvalidResponseException) {
                            errorMessage.setText(R.string.sync_error_invalid_server_response);
                            errorView.setImageDrawable(
                                    context.getResources().getDrawable(R.drawable.ic_error_generic));
                        } else if (exception instanceof NoDownloadException) {
                            errorMessage.setText(R.string.sync_error_unable_to_download);
                            errorView.setImageDrawable(
                                    context.getResources().getDrawable(R.drawable.ic_error_generic));
                        }
                        progressBar2.setVisibility(View.INVISIBLE);
                        errorView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onProgress(int step, int total) {
                        System.out.println(String.valueOf(step) + " / " + String.valueOf(total));
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        // Update progress bar
                        if (step <= total) {
                            progressView.post(new Runnable() {
                                private int step;

                                public Runnable init(int step) {
                                    this.step = step;
                                    return (this);
                                }

                                @Override
                                public void run() {
                                    progressBar.setProgress(this.step);
                                    progressView.setText(progressPhases.get(this.step - 1));
                                }
                            }.init(step));
                        }
                    }
                });
        task.execute();
        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }
}
