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
import android.widget.TextView;

import com.muflone.android.django_hotels.Constants;
import com.muflone.android.django_hotels.R;
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.commands.CommandConstants;

import java.util.Objects;

public class ReportsFragment extends Fragment {
    private View rootLayout;
    private Context context;
    private TextView viewReportTimestamps;
    private TextView viewReportDailyActivities;
    private TextView viewReportMonthlyActivities;
    private WebView webReport;
    private final Singleton singleton = Singleton.getInstance();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Execute START REPORTS BEGIN commands
        Singleton.getInstance().commandFactory.executeCommands(
                this.getActivity(),
                this.getContext(),
                CommandConstants.CONTEXT_START_REPORTS_BEGIN);
        // Initialize UI
        this.loadUI(inflater, Objects.requireNonNull(container));
        View.OnClickListener clickListener = view -> {
            String reportText = null;
            if (view == this.viewReportTimestamps) {
                reportText = ReportsFragment.this.loadReportTimestamps();
            } else if (view == this.viewReportDailyActivities) {
                reportText = ReportsFragment.this.loadReportDailyActivities();
            } else if (view == this.viewReportMonthlyActivities) {
                reportText = ReportsFragment.this.loadReportMonthlyActivities();
            }
            // Check if valid report and show it
            if (reportText != null) {
                ReportsFragment.this.webReport.loadData(
                        reportText, "text/html", "utf-8");
            }
        };
        this.viewReportTimestamps.setOnClickListener(clickListener);
        this.viewReportDailyActivities.setOnClickListener(clickListener);
        this.viewReportMonthlyActivities.setOnClickListener(clickListener);
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
        this.viewReportTimestamps = this.rootLayout.findViewById(R.id.viewReportTimestamps);
        this.viewReportDailyActivities = this.rootLayout.findViewById(R.id.viewReportDailyActivities);
        this.viewReportMonthlyActivities = this.rootLayout.findViewById(R.id.viewReportMonthActivities);
        this.webReport = this.rootLayout.findViewById(R.id.webReport);
    }

    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }

    public String loadReportTimestamps() {
        return "<html><body><h1>Timestamps</h1></body></html>";
    }

    public String loadReportDailyActivities() {
        return "<html><body><h1>Daily Activities</h1></body></html>";
    }

    public String loadReportMonthlyActivities() {
        return "<html><body><h1>Monthly Activities</h1></body></html>";
    }
}
