/*
 *     Project: Django Hotels Android
 * Description: The Android client companion app for Django Hotels
 *     Website: http://www.muflone.com/django-hotels-android/
 *      Author: Fabio Castelli (Muflone) <muflone@muflone.com>
 *   Copyright: 2018-2020 Fabio Castelli
 *     License: GPL-3+
 * Source code: https://github.com/muflone/django-hotels-android
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.muflone.android.django_hotels.tasks;

import android.os.AsyncTask;

import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.Utility;
import com.muflone.android.django_hotels.commands.CommandConstants;
import com.muflone.android.django_hotels.database.models.ReportTimestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
            } else {
                Objects.requireNonNull(item).conditions.add(timestamp.direction);
            }
        }
        // Prepare report data
        String reportHeader = this.singleton.settings.getString(CommandConstants.SETTING_REPORTS_TIMESTAMPS_HEADER, "")
                .replace("{{ DEFAULT_STYLE }}", this.singleton.settings.getString(CommandConstants.SETTING_REPORTS_DEFAULT_STYLE, ""));
        String reportContent = this.singleton.settings.getString(CommandConstants.SETTING_REPORTS_TIMESTAMPS_CONTENT,"");
        String reportTotals = this.singleton.settings.getString(CommandConstants.SETTING_REPORTS_TIMESTAMPS_TOTALS,"");
        String reportFooter = this.singleton.settings.getString(CommandConstants.SETTING_REPORTS_TIMESTAMPS_FOOTER,"");
        // Loop results to prepare content data
        StringBuilder reportContentBuilder = new StringBuilder();
        for (ReportTimestampListItem item : timestampItems.values()) {
            reportContentBuilder.append(this.replaceTags(reportContent, item));
        }
        // Check for empty data
        if (reportContentBuilder.length() == 0) {
            reportContentBuilder.append(this.singleton.settings.getString(CommandConstants.SETTING_REPORTS_TIMESTAMPS_NO_DATA, "No data"));
        }
        // Show report data
        String reportData = reportHeader + reportContentBuilder.toString() + reportTotals + reportFooter;
        // Replace common data
        reportData = reportData
                .replace("{{ SELECTED_DATE }}", this.singleton.defaultDateFormatter.format(this.singleton.selectedDate))
                .replace("{{ SELECTED_STRUCTURE }}", this.singleton.selectedStructure.name);
        if (reportContent.length() == 0) {
            reportData = "<html><body><h1>Timestamps</h1><h3>No data</h3></body></html>";
        }
        // Output callback to return results
        if (this.callback != null) {
            // Show the reports data
            this.callback.showHTML(reportData, this.getClass());
        }
    }

    private String replaceTags(String text, ReportTimestampListItem item) {
        // Format a single line of content
        String timeFormat = singleton.settings.getString(CommandConstants.SETTING_REPORTS_TIMESTAMPS_TIME_FORMAT, this.singleton.defaultTimeFormat);
        String durationFormat = singleton.settings.getString(CommandConstants.SETTING_REPORTS_TIMESTAMPS_DURATION_FORMAT, this.singleton.defaultTimeFormat);
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
        // Add employee conditions
        StringBuilder stringBuilder = new StringBuilder();
        for (String direction : item.conditions) {
            stringBuilder.append(direction);
            stringBuilder.append("<br />");
        }
        // Replace notes with conditions
        if (stringBuilder.length() > 0) {
            notes = stringBuilder.toString();
        }
        return (text
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
                .replace("{{ DURATION_TIME }}", Utility.formatElapsedTime(duration, durationFormat, true))
                .replace("{{ DURATION_TIME_STYLE }}", duration == 0 ? "duration_empty" : "duration")
                .replace("{{ NOTES }}", notes == null ? "" : notes)
                .replace("{{ NOTES_STYLE }}", notes == null ? "notes_empty" : "notes")
        );
    }

    private static class ReportTimestampListItem {
        // Single item for timestamps hours report
        private final String firstName;
        private final String lastName;
        private final List<String> conditions = new ArrayList<>();
        private Date enterTime;
        private Date exitTime;

        @SuppressWarnings("WeakerAccess")
        public ReportTimestampListItem(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }
}
