package com.muflone.android.django_hotels.commands;

import android.app.Activity;
import android.content.Context;

import com.muflone.android.django_hotels.Utility;
import com.muflone.android.django_hotels.activities.LoaderActivity;
import com.muflone.android.django_hotels.database.models.Command;

import org.json.JSONException;

public class CommandShortcutIcon extends CommandBase {
    /**
     * This Command adds a shortcut icon to the home
     *
     * The command must have the following arguments:
     * title: the icon title
     * height: the icon height
     * width: the icon width
     * icon: the base64 encoded icon
     */
    public CommandShortcutIcon(Activity activity, Context context, Command command) {
        super(activity, context, command);
    }

    @Override
    public void execute() {
        super.execute();
        try {
            Utility.createShortcutIcon(this.activity,
                    this.command.command.getString("title"),
                    this.command.command.getInt("width"),
                    this.command.command.getInt("height"),
                    this.command.command.getString("icon"),
                    LoaderActivity.class);
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
    }
}