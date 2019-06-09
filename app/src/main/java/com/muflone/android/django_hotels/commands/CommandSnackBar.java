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
    public CommandSnackBar(Activity activity, Context context, Context applicationContext, Command command) {
        super(activity, context, applicationContext, command);
    }

    @Override
    public void execute() {
        super.execute();
        try {
            NotifyMessage.snackbar(this.context,
                    this.activity.getWindow().getDecorView(),
                    this.command.type.command.getString("message"),
                    this.command.type.command.getString("action"),
                    this.command.type.command.getInt("duration")
                    );
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
