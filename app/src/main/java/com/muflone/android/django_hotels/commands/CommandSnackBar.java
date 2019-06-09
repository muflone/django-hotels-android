package com.muflone.android.django_hotels.commands;

import android.app.Activity;
import android.content.Context;

import com.muflone.android.django_hotels.NotifyMessage;
import com.muflone.android.django_hotels.database.models.Command;

import org.json.JSONException;

public class CommandSnackBar extends CommandBase {
    /**
     * This Command shows a SnackBar notification
     *
     * The command must have the following arguments:
     * message: the message to show
     * action: the message to show on the closing action
     * duration: the duration in milliseconds
     */
    public CommandSnackBar(Activity activity, Context context, Command command) {
        super(activity, context, command);
    }

    @Override
    public void execute() {
        super.execute();
        if (this.activity != null) {
            try {
                NotifyMessage.snackbar(this.context,
                        this.activity.getWindow().getDecorView(),
                        this.command.command.getString("message"),
                        this.command.command.getString("action"),
                        this.command.command.getInt("duration")
                );
            } catch (JSONException exception) {
                exception.printStackTrace();
            }
        }
    }
}
