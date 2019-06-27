package com.muflone.android.django_hotels.commands;

import android.app.Activity;
import android.content.Context;

import com.muflone.android.django_hotels.Utility;
import com.muflone.android.django_hotels.database.models.Command;

import org.json.JSONException;

public class CommandStartEmail extends CommandBase {
    /**
     * This Command opens a new email message
     *
     * The command must have the following arguments:
     * recipient: the recipient address
     * subject: the message subject
     * message: the message body
     */
    public CommandStartEmail(Activity activity, Context context, Command command) {
        super(activity, context, command);
    }

    @Override
    public void execute() {
        super.execute();
        try {
            Utility.sendEmail(this.activity,
                    this.command.command.getString("recipient").split(","),
                    this.command.command.getString("subject"),
                    this.command.command.getString("message"));
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
    }
}
