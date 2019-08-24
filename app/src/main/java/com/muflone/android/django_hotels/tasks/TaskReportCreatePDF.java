package com.muflone.android.django_hotels.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.muflone.android.django_hotels.PDFCreator;
import com.muflone.android.django_hotels.R;
import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.commands.CommandConstants;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class TaskReportCreatePDF extends AsyncTask<Void, Void, Void> {
    private final String data;
    private final Class<?> reportClass;
    private final TaskListenerInterface callback;
    private final Singleton singleton = Singleton.getInstance();

    public TaskReportCreatePDF(String data, Class<?> reportClass, TaskListenerInterface callback) {
        this.data = data;
        this.reportClass = reportClass;
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(Void... params) {
        // Apply a timer to update background UI
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                TaskReportCreatePDF.this.callback.onProgress(0, 0);
            }
        }, 1000, 1000);
        // Create missing destination directory
        File destinationDirectory = new File(
                this.singleton.settings.context.getCacheDir() +
                        File.separator +
                        "reports");
        if (!destinationDirectory.exists()) {
            //noinspection ResultOfMethodCallIgnored
            destinationDirectory.mkdir();
        }
        String destinationPath = destinationDirectory + File.separator + this.reportClass.getSimpleName() + ".pdf";
        try {
            // Prepare PDF document information
            String reportTitle = "";
            String reportSubject = "";
            String reportKeywords = "";
            if (reportClass == TaskReportTimestamps.class) {
                // PDF information for Timestamps
                reportTitle = this.singleton.settings.context.getString(R.string.report_timestamps);
                reportSubject = this.singleton.settings.context.getString(R.string.report_timestamps);
                reportKeywords = this.singleton.settings.getString(CommandConstants.SETTING_REPORTS_TIMESTAMPS_KEYWORDS, "");
            } else if (reportClass == TaskReportActivities.class) {
                // PDF information for Activities Details
                reportTitle = this.singleton.settings.context.getString(R.string.report_activities);
                reportSubject = this.singleton.settings.context.getString(R.string.report_activities);
                reportKeywords = this.singleton.settings.getString(CommandConstants.SETTING_REPORTS_ACTIVITIES_KEYWORDS, "");
            }
            PDFCreator pdfCreator = new PDFCreator();
            pdfCreator.pageSize = PageSize.A4.rotate();
            pdfCreator.title = reportTitle;
            pdfCreator.subject = reportSubject;
            pdfCreator.creator = this.singleton.settings.getApplicationNameVersion();
            pdfCreator.author = this.singleton.settings.context.getString(R.string.author_name);
            pdfCreator.keywords = reportKeywords;
            if (!pdfCreator.htmlToPDF(data, destinationPath)) {
                Log.w(this.getClass().getSimpleName(), "Unable to create PDF document");
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        // Cancel updating timer
        timer.cancel();
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        // PDF Report was completed, can proceed
        this.callback.onSuccess(null);
    }
}
