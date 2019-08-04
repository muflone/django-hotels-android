package com.muflone.android.django_hotels.tasks;

/*
 * Listener for TaskReports
 */
public interface TaskReportInterface {
    void showHTML(String data, Class<?> reportClass);
    void createPDF(String data, Class<?> reportClass);
}
