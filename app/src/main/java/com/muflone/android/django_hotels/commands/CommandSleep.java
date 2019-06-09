package com.muflone.android.django_hotels.commands;

import android.app.Activity;
import android.content.Context;

import com.muflone.android.django_hotels.Utility;
import com.muflone.android.django_hotels.database.models.Command;

import org.json.JSONException;

public class CommandSleep extends CommandBase {
    /**
     * This Command pauses the execution for some time
     *
     * The command must have the following arguments:
     * duration: the duration in milliseconds
     */
    public CommandSleep(Activity activity, Context context, Context applicationContext, Command command) {
        super(activity, context, applicationContext, command);
    }

    @Override
    public void execute() {
        super.execute();
        try {
            Utility.sleep(this.command.type.command.getInt("duration"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
