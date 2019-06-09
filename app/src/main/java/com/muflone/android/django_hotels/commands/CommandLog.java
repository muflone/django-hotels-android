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
     *   d for debug message
     *   w for warning message
     *   v for verbose message
     *   i for information message
     *   e for error message
     *   a for abnormal message
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
            String messageTag = this.command.type.command.getString("tag");
            String messageText = this.command.type.command.getString("message");
            switch (this.command.type.command.getString("type")) {
                case "d":
                    Log.d(messageTag, messageText);
                    break;
                case "w":
                    Log.w(messageTag, messageText);
                    break;
                case "v":
                    Log.v(messageTag, messageText);
                    break;
                case "i":
                    Log.i(messageTag, messageText);
                    break;
                case "e":
                    Log.e(messageTag, messageText);
                    break;
                case "a":
                    Log.wtf(messageTag, messageText);
                    break;
            }
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
    }
}
