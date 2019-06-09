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
    public CommandShortcutIcon(Activity activity, Context context, Context applicationContext, Command command) {
        super(activity, context, applicationContext, command);
    }

    @Override
    public void execute() {
        super.execute();
        try {
            Utility.createShortcutIcon(this.activity,
                    this.command.type.command.getString("title"),
                    this.command.type.command.getInt("width"),
                    this.command.type.command.getInt("height"),
                    this.command.type.command.getString("icon"),
                    LoaderActivity.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
