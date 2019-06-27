package com.muflone.android.django_hotels;

public class Constants {
    public static final int DATABASE_VERSION = 7;
    public static final String DATABASE_NAME = "hotels.sqlite";
    public static final int LATEST_TIMESTAMPS = 30;

    // Command Contexts for CommandFactory
    public static final String CONTEXT_APP_BEGIN = "APP BEGIN";
    public static final String CONTEXT_APP_END = "APP END";
    public static final String CONTEXT_START_MAIN = "START MAIN";
    public static final String CONTEXT_START_SCANNER = "START SCANNER";
    public static final String CONTEXT_START_STRUCTURE = "START STRUCTURE";
    public static final String CONTEXT_START_EXTRA = "START EXTRA";
    public static final String CONTEXT_START_SYNC = "START SYNC";
    public static final String CONTEXT_START_SETTINGS = "START SETTINGS";
    public static final String CONTEXT_START_ABOUT = "START ABOUT";
    public static final String CONTEXT_SYNC_PROGRESS = "SYNC PROGRESS";
    public static final String CONTEXT_SYNC_FAIL = "SYNC FAIL";
    public static final String CONTEXT_SYNC_END = "SYNC END";
    public static final String contexts[] = {
        CONTEXT_APP_BEGIN,
        CONTEXT_APP_END,
        CONTEXT_START_MAIN,
        CONTEXT_START_SCANNER,
        CONTEXT_START_STRUCTURE,
        CONTEXT_START_EXTRA,
        CONTEXT_START_SYNC,
        CONTEXT_START_SETTINGS,
        CONTEXT_START_ABOUT,
        CONTEXT_SYNC_PROGRESS,
        CONTEXT_SYNC_FAIL,
        CONTEXT_SYNC_END
    };
}
