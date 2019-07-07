package com.muflone.android.django_hotels.commands;

public class CommandConstants {
    // Command Contexts for CommandFactory
    public static final String CONTEXT_APP_BEGIN = "APP BEGIN";
    public static final String CONTEXT_APP_END = "APP END";
    public static final String CONTEXT_START_MAIN_BEGIN = "START MAIN BEGIN";
    public static final String CONTEXT_START_MAIN_END = "START MAIN END";
    public static final String CONTEXT_START_SCANNER_BEGIN = "START SCANNER BEGIN";
    public static final String CONTEXT_START_SCANNER_END = "START SCANNER END";
    public static final String CONTEXT_START_STRUCTURE_BEGIN = "START STRUCTURE BEGIN";
    public static final String CONTEXT_START_STRUCTURE_END = "START STRUCTURE END";
    public static final String CONTEXT_START_EXTRA_BEGIN = "START EXTRA BEGIN";
    public static final String CONTEXT_START_EXTRA_END = "START EXTRA END";
    public static final String CONTEXT_START_SYNC_BEGIN = "START SYNC BEGIN";
    public static final String CONTEXT_START_SYNC_END = "START SYNC END";
    public static final String CONTEXT_START_SETTINGS_BEGIN = "START SETTINGS BEGIN";
    public static final String CONTEXT_START_SETTINGS_END = "START SETTINGS END";
    public static final String CONTEXT_START_ABOUT_BEGIN = "START ABOUT BEGIN";
    public static final String CONTEXT_START_ABOUT_END = "START ABOUT END";
    public static final String CONTEXT_SYNC_PROGRESS = "SYNC PROGRESS";
    public static final String CONTEXT_SYNC_FAIL = "SYNC FAIL";
    public static final String CONTEXT_SYNC_END = "SYNC END";
    public static final String[] contexts = {
            CONTEXT_APP_BEGIN,
            CONTEXT_START_MAIN_BEGIN,
            CONTEXT_START_MAIN_END,
            CONTEXT_START_SCANNER_BEGIN,
            CONTEXT_START_SCANNER_END,
            CONTEXT_START_STRUCTURE_BEGIN,
            CONTEXT_START_STRUCTURE_END,
            CONTEXT_START_EXTRA_BEGIN,
            CONTEXT_START_EXTRA_END,
            CONTEXT_START_SYNC_BEGIN,
            CONTEXT_START_SYNC_END,
            CONTEXT_START_SETTINGS_BEGIN,
            CONTEXT_START_SETTINGS_END,
            CONTEXT_START_ABOUT_BEGIN,
            CONTEXT_START_ABOUT_END,
            CONTEXT_SYNC_PROGRESS,
            CONTEXT_SYNC_FAIL,
            CONTEXT_SYNC_END,
            CONTEXT_APP_END
    };

    // Settings configured from remote server
    public static final String SETTING_APP_EXIT_COMMANDS_DELAY = "app_exit_commands_delay";
    public static final String SETTING_SCANNER_TIMESTAMPS_FORMAT_DATE = "scanner_timestamps_format_date";
    public static final String SETTING_SCANNER_TIMESTAMPS_FORMAT_TIME = "scanner_timestamps_format_time";
    public static final String SETTING_SCANNER_CURRENT_DAY_FORMAT = "scanner_current_day_format";
    public static final String SETTING_SCANNER_CURRENT_DATE_FORMAT = "scanner_current_date_format";
    public static final String SETTING_SCANNER_CURRENT_TIME_FORMAT = "scanner_current_time_format";
    public static final String SYNC_CONNECT_TIMEOUT = "sync_connect_timeout";
    public static final String SYNC_READ_TIMEOUT = "sync_read_timeout";

    // Set used value for all commands at once
    public static final int SET_USED_SET_ALL_COMMAND_USAGES = -1;

    // Log message types
    public static final String LOG_TYPE_DEBUG = "debug";
    public static final String LOG_TYPE_WARNING = "warning";
    public static final String LOG_TYPE_VERBOSE = "verbose";
    public static final String LOG_TYPE_INFORMATION = "information";
    public static final String LOG_TYPE_ERROR = "error";
    public static final String LOG_TYPE_ABNORMAL = "abnormal";

    // Dialog types
    public static final String DIALOG_TYPE_MESSAGE = "message";
    public static final String DIALOG_TYPE_INPUT = "input";
    public static final String DIALOG_TYPE_LIST = "list";
}
