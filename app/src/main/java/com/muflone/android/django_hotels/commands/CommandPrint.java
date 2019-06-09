package com.muflone.android.django_hotels.commands;

import android.app.Activity;
import android.content.Context;

import com.muflone.android.django_hotels.database.models.Command;

import org.json.JSONException;

public class CommandPrint extends CommandBase {
    /**
     * This Command prints a message to the console
     *
     * The command must have the following arguments:
     * message: the message to show
     */
    public CommandPrint(Activity activity, Context context, Command command) {
        super(activity, context, command);
    }

    @Override
    public void execute() {
        super.execute();
        try {
            System.out.println(this.command.command.getString("message"));
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
    }
}
