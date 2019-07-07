package com.muflone.android.django_hotels.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.muflone.android.django_hotels.R;
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.Utility;
import com.muflone.android.django_hotels.commands.CommandConstants;
import com.muflone.android.django_hotels.database.models.TimestampEmployee;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ReportsFragment extends Fragment {
    private View rootLayout;
    private TextView viewReportTimestamps;
    private TextView viewReportDailyActivities;
    private TextView viewReportMonthlyActivities;
    private WebView webReport;

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
                ReportTimestampsListByDateTask task = new ReportTimestampsListByDateTask(this);
                task.execute();
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
        super.onAttach(context);
    }

    @SuppressWarnings("SpellCheckingInspection")
    private void showReportData(String data) {
        // Load report data in webview
        this.webReport.loadData(data, "text/html", "utf-8");
    }

    private String loadReportDailyActivities() {
        return "<html><body><h1>Daily Activities</h1></body></html>";
    }

    private String loadReportMonthlyActivities() {
        return "<html><body><h1>Monthly Activities</h1></body></html>";
    }

    private static class ReportTimestampsListByDateTask extends AsyncTask<Void, Void, List<TimestampEmployee>> {
        private final Singleton singleton = Singleton.getInstance();
        private final ReportsFragment fragment;

        @SuppressWarnings("WeakerAccess")
        public ReportTimestampsListByDateTask(ReportsFragment fragment) {
            this.fragment = fragment;
        }

        @Override
        protected List<TimestampEmployee> doInBackground(Void... params) {
            return this.singleton.database.timestampDao().listForReportTimestamps(
                    this.singleton.selectedDate,
                    this.singleton.selectedStructure.id);
        }

        @Override
        protected void onPostExecute(List<TimestampEmployee> result) {
            HashMap<String, ReportTimestampListItem> timestampItems = new HashMap<>();
            for (TimestampEmployee timestamp : result) {
                String fullName = timestamp.firstName + " " + timestamp.lastName;
                // Get existing ReportTimestampListItem or create a new one
                ReportTimestampListItem item;
                if (timestampItems.containsKey(fullName))
                {
                    item = timestampItems.get(fullName);
                } else {
                    item = new ReportTimestampListItem(timestamp.firstName, timestamp.lastName);
                    timestampItems.put(fullName, item);
                }
                // Set enter and exit date
                if (timestamp.direction.equals(singleton.apiData.enterDirection.name)) {
                    Objects.requireNonNull(item).enterTime = timestamp.datetime;
                } else if (timestamp.direction.equals(singleton.apiData.exitDirection.name)) {
                    Objects.requireNonNull(item).exitTime = timestamp.datetime;
                }
            }
            // Prepare report data
            String reportHeader = this.singleton.settings.getString(CommandConstants.SETTING_REPORTS_TIMESTAMPS_HEADER, "");
            String reportFirstLine = this.singleton.settings.getString(CommandConstants.SETTING_REPORTS_TIMESTAMPS_FIRST,"");
            String reportContent = this.singleton.settings.getString(CommandConstants.SETTING_REPORTS_TIMESTAMPS_CONTENT,"");
            String reportTotals = this.singleton.settings.getString(CommandConstants.SETTING_REPORTS_TIMESTAMPS_TOTALS,"");
            String reportFooter = this.singleton.settings.getString(CommandConstants.SETTING_REPORTS_TIMESTAMPS_FOOTER,"");
            String timeFormat = singleton.settings.getString(CommandConstants.SETTING_REPORTS_TIMESTAMPS_TIME_FORMAT, "HH:mm.ss");
            String durationFormat = singleton.settings.getString(CommandConstants.SETTING_REPORTS_TIMESTAMPS_DURATION_FORMAT, null);
            // Loop results to prepare content data
            StringBuilder stringBuilder = new StringBuilder();
            for (ReportTimestampListItem item : timestampItems.values()) {
                // Calculate duration time
                long duration = 0;
                if (item.enterTime != null && item.exitTime != null) {
                    duration = item.exitTime.getTime() - item.enterTime.getTime();
                    duration /= 1000;
                }
                // Prepare notes for missing enter or exit time
                String notes = null;
                if (item.enterTime == null) {
                    notes = singleton.settings.getString(CommandConstants.SETTING_REPORTS_TIMESTAMPS_MISSING_ENTER_TIME,
                            "Missing enter time");
                } else if (item.exitTime == null) {
                    notes = singleton.settings.getString(CommandConstants.SETTING_REPORTS_TIMESTAMPS_MISSING_EXIT_TIME,
                            "Missing exit time");
                }
                // Format a single line of content
                stringBuilder.append(reportContent
                        .replace("{{ FIRST_NAME }}", item.firstName)
                        .replace("{{ LAST_NAME }}", item.lastName)
                        .replace("{{ ENTER_TIME }}", item.enterTime == null ? "" :
                                new SimpleDateFormat(timeFormat).format(item.enterTime))
                        .replace("{{ EXIT_TIME }}", item.exitTime == null ? "" :
                                new SimpleDateFormat(timeFormat).format(item.exitTime))
                        .replace("{{ DURATION_TIME }}", Utility.formatElapsedTime(duration, durationFormat))
                        .replace("{{ NOTES }}", notes == null ? "" : notes)
                        .replace("{{ NOTES_STYLE }}", notes == null ? "notes_empty" : "notes"));
            }
            // Show report data
            this.fragment.showReportData(reportHeader + reportFirstLine + stringBuilder.toString() + reportTotals + reportFooter);
        }

        private class ReportTimestampListItem {
            // Single item for timestamps hours report
            private final String firstName;
            private final String lastName;
            private Date enterTime;
            private Date exitTime;

            @SuppressWarnings("WeakerAccess")
            public ReportTimestampListItem(String firstName, String lastName) {
                this.firstName = firstName;
                this.lastName = lastName;
            }
        }
    }
}
