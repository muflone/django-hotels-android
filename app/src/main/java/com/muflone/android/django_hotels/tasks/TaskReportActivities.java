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
import android.text.TextUtils;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.muflone.android.django_hotels.R;
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.Utility;
import com.muflone.android.django_hotels.commands.CommandConstants;
import com.muflone.android.django_hotels.database.models.ReportActivityDetail;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class TaskReportActivities extends AsyncTask<Void, Void, List<ReportActivityDetail>> {
    private final Singleton singleton = Singleton.getInstance();
    private final TaskReportInterface callback;
    private final String extraActivitiesName = this.singleton.settings.context.getString(R.string.extras_activities);

    @SuppressWarnings("WeakerAccess")
    public TaskReportActivities(TaskReportInterface callback) {
        this.callback = callback;
    }

    @Override
    protected List<ReportActivityDetail> doInBackground(Void... params) {
        return this.singleton.database.serviceActivityDao().listForReportActivities(
                this.singleton.selectedDate,
                this.singleton.selectedStructure != null ? this.singleton.selectedStructure.id : 0);
    }

    @Override
    protected void onPostExecute(List<ReportActivityDetail> result) {
        // Prepare report data
        String reportHeader = this.singleton.settings.getString(CommandConstants.SETTING_REPORTS_ACTIVITIES_HEADER, "")
                .replace("{{ DEFAULT_STYLE }}", this.singleton.settings.getString(CommandConstants.SETTING_REPORTS_DEFAULT_STYLE, ""));
        String reportContent = this.singleton.settings.getString(CommandConstants.SETTING_REPORTS_ACTIVITIES_CONTENT,"");
        String reportTotals = this.singleton.settings.getString(CommandConstants.SETTING_REPORTS_ACTIVITIES_TOTALS,"");
        String reportTotalsTotalsFormat = this.singleton.settings.getString(CommandConstants.SETTING_REPORTS_ACTIVITIES_TOTALS_TOTAL_SERVICES_FORMAT, "%s %d\n");
        String reportFooter = this.singleton.settings.getString(CommandConstants.SETTING_REPORTS_ACTIVITIES_FOOTER,"");
        String reportGroupHeader = this.singleton.settings.getString(CommandConstants.SETTING_REPORTS_ACTIVITIES_GROUP_HEADER, "");
        String reportGroupFooter = this.singleton.settings.getString(CommandConstants.SETTING_REPORTS_ACTIVITIES_GROUP_FOOTER,"");
        String reportGroupTotalsFormat = this.singleton.settings.getString(CommandConstants.SETTING_REPORTS_ACTIVITIES_GROUP_FOOTER_TOTAL_SERVICES_FORMAT, "%s %d\n");
        // Loop results to prepare totals per contract/service
        Table<Long, Long, Long> totalsPerContractTable = HashBasedTable.create();
        Hashtable<String, Long> totalsPerServiceList = new Hashtable<>();
        Hashtable<Long, List<ReportActivityDetail>> activityDetailsList = new Hashtable<>();
        String totalExtraActivities;
        for (ReportActivityDetail activityDetail : result) {
            // Create new list of services
            if (! activityDetailsList.containsKey(activityDetail.contractId)) {
                activityDetailsList.put(activityDetail.contractId, new ArrayList<>());
            }
            Objects.requireNonNull(activityDetailsList.get(activityDetail.contractId)).add(activityDetail);
            totalsPerContractTable.put(activityDetail.contractId, activityDetail.serviceId,
                    totalsPerContractTable.contains(activityDetail.contractId, activityDetail.serviceId) ?
                            totalsPerContractTable.get(activityDetail.contractId, activityDetail.serviceId) + activityDetail.service_qty :
                            activityDetail.service_qty);
        }
        // Loop results to prepare content data
        StringBuilder reportContentBuilder = new StringBuilder();
        for (Long contractId : totalsPerContractTable.rowKeySet()) {
            List<ReportActivityDetail> activityDetailsPerContract = Objects.requireNonNull(activityDetailsList.get(contractId));
            // Content header
            if (activityDetailsPerContract.size() > 0) {
                reportContentBuilder.append(this.replaceTags(reportGroupHeader, activityDetailsPerContract.listIterator(0).next()));
            }
            // Content body
            for (ReportActivityDetail activityDetail : activityDetailsPerContract) {
                reportContentBuilder.append(this.replaceTags(reportContent, activityDetail));
            }
            // Content footer
            StringBuilder totalServicesBuilder = new StringBuilder();
            totalExtraActivities = null;
            for (Long serviceId : totalsPerContractTable.row(contractId).keySet()) {
                String serviceName = serviceId != 0 ?
                        // Normal service
                        Objects.requireNonNull(this.singleton.apiData.serviceMap.get(serviceId)).name :
                        // Extra service
                        this.extraActivitiesName;
                // Add totals per service
                totalsPerServiceList.put(serviceName, totalsPerContractTable.get(contractId, serviceId) +
                        (totalsPerServiceList.containsKey(serviceName) ? Objects.requireNonNull(totalsPerServiceList.get(serviceName)) : 0L));
                if (serviceId != 0) {
                    // Normal service
                    totalServicesBuilder.append(String.format(Locale.ROOT,
                            reportGroupTotalsFormat, serviceName, totalsPerContractTable.get(contractId, serviceId)));
                } else {
                    // Extra service
                    totalExtraActivities = String.format(Locale.ROOT,
                            reportGroupTotalsFormat, serviceName,
                            Utility.formatElapsedTime(totalsPerContractTable.get(contractId, serviceId) * 60, null, false));
                }
            }
            // Add extras to the end of the services
            if (totalExtraActivities != null) {
                totalServicesBuilder.insert(0, totalExtraActivities);
            }
            reportContentBuilder.append(reportGroupFooter
                    .replace("{{ TOTALS_SERVICES }}", totalServicesBuilder.toString())
            );
        }
        // Check for empty data
        if (reportContentBuilder.length() == 0) {
            reportContentBuilder.append(this.singleton.settings.getString(CommandConstants.SETTING_REPORTS_ACTIVITIES_NO_DATA, "No data"));
        }
        // Build totals
        StringBuilder totalServicesBuilder = new StringBuilder();
        totalExtraActivities = null;
        //noinspection unchecked
        List<String> servicesTotalsKeysList = Utility.sortHashtableKeys(totalsPerServiceList, false);
        for (String serviceName : servicesTotalsKeysList) {
            if (! serviceName.equals(this.extraActivitiesName)) {
                // Normal service
                totalServicesBuilder.append(String.format(Locale.ROOT, reportTotalsTotalsFormat, serviceName, totalsPerServiceList.get(serviceName)));
            } else {
                // Extra service
                totalExtraActivities = String.format(Locale.ROOT, reportTotalsTotalsFormat, serviceName,
                        Utility.formatElapsedTime(Objects.requireNonNull(totalsPerServiceList.get(serviceName)) * 60, null, false));
            }
        }
        // Add extras to the end of the services
        if (totalExtraActivities != null) {
            totalServicesBuilder.insert(0, totalExtraActivities);
        }
        // Show report data
        String reportData = reportHeader +
                reportContentBuilder.toString() +
                reportTotals.replace("{{ TOTALS_SERVICES }}", totalServicesBuilder.toString()) +
                reportFooter;
        // Replace common data
        reportData = reportData
                .replace("{{ SELECTED_DATE }}", this.singleton.defaultDateFormatter.format(this.singleton.selectedDate))
                .replace("{{ SELECTED_STRUCTURE }}", this.singleton.selectedStructure.name);
        if (reportContentBuilder.length() == 0) {
            reportData = "<html><body><h1>Activities details</h1><h3>No data</h3></body></html>";
        }
        // Output callback to return results
        if (this.callback != null) {
            // Show the reports data
            this.callback.showHTML(reportData, this.getClass());
        }
    }
    private String replaceTags(String text, ReportActivityDetail item) {
        // Format a single line of content
        DateFormat dateFormat = new SimpleDateFormat(this.singleton.settings.getString(CommandConstants.SETTING_REPORTS_ACTIVITIES_DATE_FORMAT, "yyyy-MM-dd"));
        return (text
                .replace("{{ COMPANY_ID }}", String.valueOf(item.companyId))
                .replace("{{ COMPANY }}", TextUtils.htmlEncode(item.company))
                .replace("{{ EMPLOYEE_ID }}", String.valueOf(item.employeeId))
                .replace("{{ FIRST_NAME }}", TextUtils.htmlEncode(item.firstName))
                .replace("{{ LAST_NAME }}", TextUtils.htmlEncode(item.lastName))
                .replace("{{ CONTRACT_ID }}", String.valueOf(item.contractId))
                .replace("{{ DATETIME }}", dateFormat.format(item.datetime))
                .replace("{{ BUILDING_ID }}", String.valueOf(item.buildingId))
                .replace("{{ BUILDING }}", TextUtils.htmlEncode(item.building))
                .replace("{{ ROOM_ID }}", String.valueOf(item.roomId))
                .replace("{{ ROOM }}", TextUtils.htmlEncode(item.room))
                .replace("{{ SERVICE_ID }}", String.valueOf(item.serviceId))
                .replace("{{ SERVICE }}", TextUtils.htmlEncode(
                        item.serviceId != 0 ? item.service : this.extraActivitiesName))
                .replace("{{ DESCRIPTION }}", item.serviceId != 0 ?
                        // Normal service
                        TextUtils.htmlEncode(item.description) :
                        // Extra service
                        Utility.formatElapsedTime(item.service_qty * 60, null, false))
        );
    }
}
