package com.muflone.android.django_hotels.tasks;

/*
 * Listener for TaskReports
 */
public interface TaskReportInterface {
    void showHTML(String data);
    void createPDF(String data, String className);
}
