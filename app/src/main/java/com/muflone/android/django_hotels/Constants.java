package com.muflone.android.django_hotels;

public class Constants {
    public static final int DATABASE_VERSION = 7;
    public static final String DATABASE_NAME = "hotels.sqlite";
    public static final int LATEST_TIMESTAMPS = 30;
    public static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 5001;

    // Command Contexts for CommandFactory
    public static final String CONTEXT_APP_BEGIN = "APP BEGIN";
    public static final String CONTEXT_APP_END = "APP END";
    public static final String CONTEXT_START_MAIN_POST = "START MAIN POST";
    public static final String CONTEXT_START_SCANNER_POST = "START SCANNER POST";
    public static final String CONTEXT_START_STRUCTURE_POST = "START STRUCTURE POST";
    public static final String CONTEXT_START_EXTRA_POST = "START EXTRA POST";
    public static final String CONTEXT_START_SYNC_POST = "START SYNC POST";
    public static final String CONTEXT_START_SETTINGS_POST = "START SETTINGS POST";
    public static final String CONTEXT_START_ABOUT_POST = "START ABOUT POST";
    public static final String CONTEXT_SYNC_PROGRESS = "SYNC PROGRESS";
    public static final String CONTEXT_SYNC_FAIL = "SYNC FAIL";
    public static final String CONTEXT_SYNC_END = "SYNC END";
    public static final String[] contexts = {
            CONTEXT_APP_BEGIN,
            CONTEXT_START_MAIN_POST,
            CONTEXT_START_SCANNER_POST,
            CONTEXT_START_STRUCTURE_POST,
            CONTEXT_START_EXTRA_POST,
            CONTEXT_START_SYNC_POST,
            CONTEXT_START_SETTINGS_POST,
            CONTEXT_START_ABOUT_POST,
            CONTEXT_SYNC_PROGRESS,
            CONTEXT_SYNC_FAIL,
            CONTEXT_SYNC_END,
            CONTEXT_APP_END
    };
}
