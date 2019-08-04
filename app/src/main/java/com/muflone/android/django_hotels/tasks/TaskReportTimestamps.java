package com.muflone.android.django_hotels.tasks;

import android.os.AsyncTask;

import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.Utility;
import com.muflone.android.django_hotels.commands.CommandConstants;
import com.muflone.android.django_hotels.database.models.ReportTimestamp;
import com.muflone.android.django_hotels.database.models.TimestampEmployee;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class TaskReportTimestamps extends AsyncTask<Void, Void, List<ReportTimestamp>> {
    private final Singleton singleton = Singleton.getInstance();
    private final TaskReportInterface callback;

    @SuppressWarnings("WeakerAccess")
    public TaskReportTimestamps(TaskReportInterface callback) {
        this.callback = callback;
    }

    @Override
    protected List<ReportTimestamp> doInBackground(Void... params) {
        return this.singleton.database.timestampDao().listForReportTimestamps(
                this.singleton.selectedDate,
                this.singleton.selectedStructure != null ? this.singleton.selectedStructure.id : 0);
    }

    @Override
    protected void onPostExecute(List<ReportTimestamp> result) {
        HashMap<String, ReportTimestampListItem> timestampItems = new HashMap<>();
        for (ReportTimestamp timestamp : result) {
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
                    .replace("{{ EXIT_TIME_OK }}", item.exitTime == null ? "" :
                            singleton.settings.getString(CommandConstants.SETTING_REPORTS_TIMESTAMPS_MISSING_EXIT_TIME_OK, ""))
                    .replace("{{ EXIT_TIME_NO }}", item.exitTime == null ?
                            singleton.settings.getString(CommandConstants.SETTING_REPORTS_TIMESTAMPS_MISSING_EXIT_TIME_NO, "") :
                            "")
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
        // Output callback to return results
        if (this.callback != null) {
            // Show the reports data
            this.callback.showHTML(reportData);
            // Save data to PDF document
            this.callback.createPDF(reportData, this.getClass().getSimpleName());
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
