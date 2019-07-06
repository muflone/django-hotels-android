package com.muflone.android.django_hotels.commands;

public class CommandConstants {
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

    // Settings configured from remote server
    public static final String SETTING_APP_EXIT_COMMANDS_DELAY = "app_exit_commands_delay";
    public static final String SETTING_SCANNER_TIMESTAMPS_FORMAT_DATE = "scanner_timestamps_format_date";
    public static final String SETTING_SCANNER_TIMESTAMPS_FORMAT_TIME = "scanner_timestamps_format_time";
    public static final String SETTING_SCANNER_CURRENT_DAY_FORMAT = "scanner_current_day_format";
    public static final String SETTING_SCANNER_CURRENT_DATE_FORMAT = "scanner_current_date_format";
    public static final String SETTING_SCANNER_CURRENT_TIME_FORMAT = "scanner_current_time_format";

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
