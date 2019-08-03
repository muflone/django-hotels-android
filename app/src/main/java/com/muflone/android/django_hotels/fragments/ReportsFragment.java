package com.muflone.android.django_hotels.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.muflone.android.django_hotels.PDFCreator;
import com.muflone.android.django_hotels.R;
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.Utility;
import com.muflone.android.django_hotels.commands.CommandConstants;
import com.muflone.android.django_hotels.database.models.TimestampEmployee;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ReportsFragment extends Fragment {
    private View rootLayout;
    private TextView buttonReportTimestamps;
    private TextView buttonReportDailyActivities;
    private TextView buttonReportMonthActivities;
    private WebView webReport;
    private Singleton singleton = Singleton.getInstance();

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
        View.OnClickListener clickListener = view -> {
            String reportText = null;
            if (view == this.buttonReportTimestamps) {
                ReportTimestampsListByDateTask task = new ReportTimestampsListByDateTask(this);
                task.execute();
            } else if (view == this.buttonReportDailyActivities) {
                reportText = "<html><body><h1>Daily Activities</h1></body></html>";
            } else if (view == this.buttonReportMonthActivities) {
                reportText = "<html><body><h1>Monthly Activities</h1></body></html>";
            }
            // Check if valid report and show it
            if (reportText != null) {
                ReportsFragment.this.webReport.loadData(
                        reportText, "text/html", "utf-8");
            }
        };
        this.buttonReportTimestamps.setOnClickListener(clickListener);
        this.buttonReportDailyActivities.setOnClickListener(clickListener);
        this.buttonReportMonthActivities.setOnClickListener(clickListener);
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
        this.buttonReportTimestamps = this.rootLayout.findViewById(R.id.buttonReportTimestamps);
        this.buttonReportDailyActivities = this.rootLayout.findViewById(R.id.buttonReportDailyActivities);
        this.buttonReportMonthActivities = this.rootLayout.findViewById(R.id.buttonReportMonthActivities);
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
                    this.singleton.selectedStructure != null ? this.singleton.selectedStructure.id : 0);
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
                    notes = singleton.settings.getString(CommandConstants.SETTING_REPORTS_TIMESTAMPS_MISSING_ENTER_TIME_MESSAGE,
                            "Missing enter time");
                } else if (item.exitTime == null) {
                    notes = singleton.settings.getString(CommandConstants.SETTING_REPORTS_TIMESTAMPS_MISSING_EXIT_TIME_MESSAGE,
                            "Missing exit time");
                }
                // Format a single line of content
                stringBuilder.append(reportContent
                        .replace("{{ FIRST_NAME }}", item.firstName)
                        .replace("{{ LAST_NAME }}", item.lastName)
                        .replace("{{ ENTER_TIME }}", item.enterTime == null ? "" :
                                new SimpleDateFormat(timeFormat).format(item.enterTime))
                        .replace("{{ ENTER_TIME_OK }}", item.enterTime == null ? "" :
                                singleton.settings.getString(CommandConstants.SETTING_REPORTS_TIMESTAMPS_MISSING_ENTER_TIME_OK, ""))
                        .replace("{{ ENTER_TIME_NO }}", item.enterTime == null ?
                                singleton.settings.getString(CommandConstants.SETTING_REPORTS_TIMESTAMPS_MISSING_ENTER_TIME_NO, "") :
                                "")
                        .replace("{{ ENTER_TIME_STYLE }}", item.enterTime == null ? "enter_empty" : "enter")
                        .replace("{{ EXIT_TIME }}", item.exitTime == null ? "" :
                                new SimpleDateFormat(timeFormat).format(item.exitTime))
                        .replace("{{ EXIT_TIME_STYLE }}", item.exitTime == null ? "exit_empty" : "exit")
                        .replace("{{ DURATION_TIME }}", Utility.formatElapsedTime(duration, durationFormat))
                        .replace("{{ DURATION_TIME_STYLE }}", duration == 0 ? "duration_empty" : "duration")
                        .replace("{{ NOTES }}", notes == null ? "" : notes)
                        .replace("{{ NOTES_STYLE }}", notes == null ? "notes_empty" : "notes"));
            }
            // Show report data
            String reportData = reportHeader + reportFirstLine + stringBuilder.toString() + reportTotals + reportFooter;
            if (reportData.length() == 0) {
                reportData = "<html><body><h1>Timestamps</h1><h3>No data</h3></body></html>";
            }
            this.fragment.showReportData(reportData);
            // Save data to PDF document
            File destinationDirectory = new File(
                    this.singleton.settings.context.getCacheDir() +
                            File.separator +
                            "reports");
            // Create missing destination directory
            if (! destinationDirectory.exists()) {
                //noinspection ResultOfMethodCallIgnored
                destinationDirectory.mkdir();
            }
            String destinationPath = destinationDirectory + File.separator + this.getClass().getSimpleName() + ".pdf";
            try {
                PDFCreator pdfCreator = new PDFCreator();
                pdfCreator.pageSize = PageSize.A4;
                pdfCreator.title = this.singleton.settings.context.getString(R.string.reports_timestamps);
                pdfCreator.subject = this.singleton.settings.context.getString(R.string.reports_timestamps);
                pdfCreator.creator = this.singleton.settings.getApplicationNameVersion();
                pdfCreator.author = this.singleton.settings.context.getString(R.string.author_name);
                pdfCreator.keywords = "report, timestamps";
                if (! pdfCreator.htmlToPDF(reportData, destinationPath)) {
                    Log.w(this.getClass().getSimpleName(), "Unable to create PDF document");
                }
            } catch (DocumentException e) {
                e.printStackTrace();
            }
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
