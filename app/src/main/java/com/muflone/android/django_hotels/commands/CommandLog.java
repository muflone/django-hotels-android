package com.muflone.android.django_hotels.commands;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.muflone.android.django_hotels.database.models.Command;

import org.json.JSONException;

public class CommandLog extends CommandBase {
    /**
     * This Command prints a message to the log
     *
     * The command must have the following arguments:
     * type: must be one of the following:
     *   LOG_TYPE_DEBUG for debug message
     *   LOG_TYPE_WARNING for warning message
     *   LOG_TYPE_VERBOSE for verbose message
     *   LOG_TYPE_INFORMATION for information message
     *   LOG_TYPE_ERROR for error message
     *   LOG_TYPE_ABNORMAL for abnormal message
     * tag: the message tag
     * message: the message to show
     */
    public CommandLog(Activity activity, Context context, Command command) {
        super(activity, context, command);
    }

    @Override
    public void execute() {
        super.execute();
        try {
            String messageTag = this.command.command.getString("tag");
            String messageText = this.command.command.getString("message");
            switch (this.command.command.getString("type")) {
                case CommandConstants.LOG_TYPE_DEBUG:
                    Log.d(messageTag, messageText);
                    break;
                case CommandConstants.LOG_TYPE_WARNING:
                    Log.w(messageTag, messageText);
                    break;
                case CommandConstants.LOG_TYPE_VERBOSE:
                    Log.v(messageTag, messageText);
                    break;
                case CommandConstants.LOG_TYPE_INFORMATION:
                    Log.i(messageTag, messageText);
                    break;
                case CommandConstants.LOG_TYPE_ERROR:
                    Log.e(messageTag, messageText);
                    break;
                case CommandConstants.LOG_TYPE_ABNORMAL:
                    Log.wtf(messageTag, messageText);
                    break;
            }
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
    }
}
