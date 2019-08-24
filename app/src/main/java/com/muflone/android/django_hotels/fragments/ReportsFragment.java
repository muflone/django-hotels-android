package com.muflone.android.django_hotels.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.muflone.android.django_hotels.R;
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.commands.CommandConstants;
import com.muflone.android.django_hotels.tasks.TaskListenerInterface;
import com.muflone.android.django_hotels.tasks.TaskReportActivities;
import com.muflone.android.django_hotels.tasks.TaskReportInterface;
import com.muflone.android.django_hotels.tasks.TaskReportCreatePDF;
import com.muflone.android.django_hotels.tasks.TaskReportTimestamps;
import com.muflone.android.django_hotels.tasks.TaskResult;

import java.util.Locale;
import java.util.Objects;

public class ReportsFragment extends Fragment {
    private View rootLayout;
    private TextView progressView;
    private ProgressBar progressBar;
    private TextView buttonReportTimestamps;
    private TextView buttonReportActivities;
    private WebView webReport;
    private final Singleton singleton = Singleton.getInstance();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Execute START REPORTS BEGIN commands
        this.singleton.commandFactory.executeCommands(
                this.getActivity(),
                this.getContext(),
                CommandConstants.CONTEXT_START_REPORTS_BEGIN);
        // Initialize UI
        this.loadUI(inflater, Objects.requireNonNull(container));
        // Prepares output callback functions for TaskReports
        View.OnClickListener clickListener = view -> {
            TaskReportInterface reportCallback = (data, reportClass) -> {
                long startingTime = System.currentTimeMillis();
                TaskReportCreatePDF task = new TaskReportCreatePDF(data, reportClass, new TaskListenerInterface() {
                    @Override
                    public void onSuccess(TaskResult result) {
                        // Load report HTML data in WebView
                        ReportsFragment.this.webReport.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);
                        // Loading completed
                        ReportsFragment.this.webReport.setVisibility(View.VISIBLE);
                        ReportsFragment.this.progressView.setVisibility(View.INVISIBLE);
                        ReportsFragment.this.progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onFailure(Exception exception) {
                    }

                    @Override
                    public void onProgress(int step, int total) {
                        // Update loading text with the elapsed time
                        Objects.requireNonNull(ReportsFragment.this.getActivity()).runOnUiThread(() -> ReportsFragment.this.progressView.setText(
                                String.format(Locale.ROOT,
                                        Objects.requireNonNull(ReportsFragment.this.getContext()).getString(R.string.report_loading),
                                        (System.currentTimeMillis() - startingTime) / 1000)));
                    }
                });
                task.execute();
            };
            // Loading started
            this.webReport.setVisibility(View.INVISIBLE);
            this.progressView.setVisibility(View.VISIBLE);
            this.progressBar.setVisibility(View.VISIBLE);
            if (view == this.buttonReportTimestamps) {
                TaskReportTimestamps task = new TaskReportTimestamps(reportCallback);
                task.execute();
            } else if (view == this.buttonReportActivities) {
                TaskReportActivities task = new TaskReportActivities(reportCallback);
                task.execute();
            }
        };
        this.buttonReportTimestamps.setOnClickListener(clickListener);
        this.buttonReportActivities.setOnClickListener(clickListener);
        // Set WebView zoom enabled
        this.webReport.getSettings().setBuiltInZoomControls(
                this.singleton.settings.getBoolean(CommandConstants.SETTING_REPORTS_ZOOM_ENABLE, false));
        // Set WebView zoom control
        this.webReport.getSettings().setDisplayZoomControls(
                this.singleton.settings.getBoolean(CommandConstants.SETTING_REPORTS_ZOOM_CONTROLS, false));
        // Set WebView default zoom level
        this.webReport.getSettings().setTextZoom(
                this.singleton.settings.getInteger(CommandConstants.SETTING_REPORTS_ZOOM_DEFAULT, 100));
        // Execute START REPORTS END commands
        Singleton.getInstance().commandFactory.executeCommands(
                this.getActivity(),
                this.getContext(),
                CommandConstants.CONTEXT_START_REPORTS_END);
        return this.rootLayout;
    }

    private void loadUI(@NonNull final LayoutInflater inflater, @NonNull final ViewGroup container) {
        // Inflate the layout for this fragment
        this.rootLayout = inflater.inflate(R.layout.reports_fragment, container, false);
        this.progressView = this.rootLayout.findViewById(R.id.progressView);
        this.progressView.setVisibility(View.INVISIBLE);
        this.progressBar = this.rootLayout.findViewById(R.id.progressBar);
        this.progressBar.setVisibility(View.INVISIBLE);
        this.buttonReportTimestamps = this.rootLayout.findViewById(R.id.buttonReportTimestamps);
        this.buttonReportActivities = this.rootLayout.findViewById(R.id.buttonReportActivities);
        this.webReport = this.rootLayout.findViewById(R.id.webReport);
        this.webReport.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
