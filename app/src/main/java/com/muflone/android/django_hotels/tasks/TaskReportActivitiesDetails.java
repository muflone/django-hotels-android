package com.muflone.android.django_hotels.tasks;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.commands.CommandConstants;
import com.muflone.android.django_hotels.database.models.ReportActivityDetail;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class TaskReportActivitiesDetails extends AsyncTask<Void, Void, List<ReportActivityDetail>> {
    private final Singleton singleton = Singleton.getInstance();
    private final TaskReportInterface callback;

    @SuppressWarnings("WeakerAccess")
    public TaskReportActivitiesDetails(TaskReportInterface callback) {
        this.callback = callback;
    }

    @Override
    protected List<ReportActivityDetail> doInBackground(Void... params) {
        return this.singleton.database.serviceActivityDao().listForReportActivityDetails(
                this.singleton.selectedDate,
                this.singleton.selectedStructure != null ? this.singleton.selectedStructure.id : 0);
    }

    @Override
    protected void onPostExecute(List<ReportActivityDetail> result) {
        // Prepare report data
        String reportHeader = this.singleton.settings.getString(CommandConstants.SETTING_REPORTS_ACTIVITIES_DETAILS_HEADER, "");
        String reportContent = this.singleton.settings.getString(CommandConstants.SETTING_REPORTS_ACTIVITIES_DETAILS_CONTENT,"");
        String reportTotals = this.singleton.settings.getString(CommandConstants.SETTING_REPORTS_ACTIVITIES_DETAILS_TOTALS,"");
        String reportFooter = this.singleton.settings.getString(CommandConstants.SETTING_REPORTS_ACTIVITIES_DETAILS_FOOTER,"");
        DateFormat dateFormat = new SimpleDateFormat(this.singleton.settings.getString(CommandConstants.SETTING_REPORTS_ACTIVITIES_DETAILS_DATE_FORMAT, "yyyy-MM-dd"));
        // Loop results to prepare content data
        StringBuilder reportContentBuilder = new StringBuilder();
        for (ReportActivityDetail activityDetail : result) {
            reportContentBuilder.append(reportContent
                .replace("{{ COMPANY_ID }}", String.valueOf(activityDetail.companyId))
                .replace("{{ COMPANY }}", TextUtils.htmlEncode(activityDetail.company))
                .replace("{{ EMPLOYEE_ID }}", String.valueOf(activityDetail.employeeId))
                .replace("{{ FIRST_NAME }}", TextUtils.htmlEncode(activityDetail.firstName))
                .replace("{{ LAST_NAME }}", TextUtils.htmlEncode(activityDetail.lastName))
                .replace("{{ CONTRACT_ID }}", String.valueOf(activityDetail.contractId))
                .replace("{{ DATETIME }}", dateFormat.format(activityDetail.datetime))
                .replace("{{ BUILDING_ID }}", String.valueOf(activityDetail.buildingId))
                .replace("{{ BUILDING }}", TextUtils.htmlEncode(activityDetail.building))
                .replace("{{ ROOM_ID }}", String.valueOf(activityDetail.roomId))
                .replace("{{ ROOM }}", TextUtils.htmlEncode(activityDetail.room))
                .replace("{{ SERVICE_ID }}", String.valueOf(activityDetail.serviceId))
                .replace("{{ SERVICE }}", TextUtils.htmlEncode(activityDetail.service))
                .replace("{{ DESCRIPTION }}", TextUtils.htmlEncode(activityDetail.description))
            );
        }
        // Show report data
        String reportData = reportHeader + reportContentBuilder.toString() + reportTotals + reportFooter;
        // Replace common data
        reportData = reportData
                .replace("{{ SELECTED_DATE }}", this.singleton.defaultDateFormatter.format(this.singleton.selectedDate))
                .replace("{{ SELECTED_STRUCTURE }}", this.singleton.selectedStructure.name);
        if (reportData.length() == 0) {
            reportData = "<html><body><h1>Activities details</h1><h3>No data</h3></body></html>";
        }
        // Output callback to return results
        if (this.callback != null) {
            // Show the reports data
            this.callback.showHTML(reportData, this.getClass());
        }
    }
}
