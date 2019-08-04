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
    public static final String CONTEXT_START_REPORTS_BEGIN = "START REPORTS BEGIN";
    public static final String CONTEXT_START_REPORTS_END = "START REPORTS END";
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
            CONTEXT_START_REPORTS_BEGIN,
            CONTEXT_START_REPORTS_END,
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
    public static final String SETTING_DEFAULT_DATE_FORMAT = "default_date_format";
    public static final String SETTING_DEFAULT_TIME_FORMAT = "default_time_format";
    public static final String SETTING_SCANNER_TIMESTAMPS_FORMAT_TIME = "scanner_timestamps_format_time";
    public static final String SETTING_SCANNER_CURRENT_DAY_FORMAT = "scanner_current_day_format";
    public static final String SETTING_SCANNER_CURRENT_DATE_FORMAT = "scanner_current_date_format";
    public static final String SETTING_SCANNER_CURRENT_TIME_FORMAT = "scanner_current_time_format";
    public static final String SETTING_STRUCTURES_BUILDING_CLOSED = "structures_buildings_closed";
    public static final String SETTING_STRUCTURES_ROOMS_STANDARD_HEIGHT = "structures_rooms_list_standard_height";
    public static final String SETTING_SYNC_CONNECT_TIMEOUT = "sync_connect_timeout";
    public static final String SETTING_SYNC_READ_TIMEOUT = "sync_read_timeout";
    public static final String SETTING_EXTRAS_TIME_STEP = "extras_time_step";
    public static final String SETTING_REPORTS_ZOOM_ENABLE = "reports_zoom_enable";
    public static final String SETTING_REPORTS_ZOOM_CONTROLS = "reports_zoom_controls";
    public static final String SETTING_REPORTS_ZOOM_DEFAULT = "reports_zoom_default";

    // Report for timestamps
    public static final String SETTING_REPORTS_TIMESTAMPS_HEADER = "reports_timestamps_header";
    public static final String SETTING_REPORTS_TIMESTAMPS_CONTENT = "reports_timestamps_content";
    public static final String SETTING_REPORTS_TIMESTAMPS_TOTALS = "reports_timestamps_totals";
    public static final String SETTING_REPORTS_TIMESTAMPS_FOOTER = "reports_timestamps_footer";
    public static final String SETTING_REPORTS_TIMESTAMPS_KEYWORDS = "reports_timestamps_keywords";
    public static final String SETTING_REPORTS_TIMESTAMPS_TIME_FORMAT = "reports_timestamps_time_format";
    public static final String SETTING_REPORTS_TIMESTAMPS_DURATION_FORMAT = "reports_timestamps_duration_format";
    public static final String SETTING_REPORTS_TIMESTAMPS_MISSING_ENTER_TIME_OK = "reports_timestamps_missing_enter_time_ok";
    public static final String SETTING_REPORTS_TIMESTAMPS_MISSING_ENTER_TIME_NO = "reports_timestamps_missing_enter_time_no";
    public static final String SETTING_REPORTS_TIMESTAMPS_MISSING_ENTER_TIME_MESSAGE = "reports_timestamps_missing_enter_time_message";
    public static final String SETTING_REPORTS_TIMESTAMPS_MISSING_EXIT_TIME_OK = "reports_timestamps_missing_exit_time_ok";
    public static final String SETTING_REPORTS_TIMESTAMPS_MISSING_EXIT_TIME_NO = "reports_timestamps_missing_exit_time_no";
    public static final String SETTING_REPORTS_TIMESTAMPS_MISSING_EXIT_TIME_MESSAGE = "reports_timestamps_missing_exit_time_message";
    public static final String SETTING_REPORTS_TIMESTAMPS_DETAILS_NO_DATA = "reports_timestamps_details_no_data";

    // Report for timestamps
    public static final String SETTING_REPORTS_ACTIVITIES_HEADER = "reports_activities_header";
    public static final String SETTING_REPORTS_ACTIVITIES_CONTENT = "reports_activities_content";
    public static final String SETTING_REPORTS_ACTIVITIES_TOTALS = "reports_activities_totals";
    public static final String SETTING_REPORTS_ACTIVITIES_FOOTER = "reports_activities_footer";
    public static final String SETTING_REPORTS_ACTIVITIES_DATE_FORMAT = "reports_activities_date_format";
    public static final String SETTING_REPORTS_ACTIVITIES_KEYWORDS = "reports_activities_keywords";
    public static final String SETTING_REPORTS_ACTIVITIES_GROUP_HEADER = "reports_activities_group_header";
    public static final String SETTING_REPORTS_ACTIVITIES_GROUP_FOOTER = "reports_activities_group_footer";
    public static final String SETTING_REPORTS_ACTIVITIES_NO_DATA = "reports_activities_no_data";
    public static final String SETTING_REPORTS_ACTIVITIES_TOTAL_SERVICES_FORMAT = "reports_activities_total_services_format";

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
