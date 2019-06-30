package com.muflone.android.django_hotels.commands;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.muflone.android.django_hotels.database.models.Command;

import org.json.JSONException;

public class CommandStartUri extends CommandBase {
    /**
     * This Command starts an Uri
     *
     * The command must have the following arguments:
     * uri: the Uri to start
     */
    public CommandStartUri(Activity activity, Context context, Command command) {
        super(activity, context, command);
    }

    @Override
    public void execute() {
        super.execute();
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(this.command.command.getString("uri")));
            this.context.startActivity(intent);
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
    }
}
