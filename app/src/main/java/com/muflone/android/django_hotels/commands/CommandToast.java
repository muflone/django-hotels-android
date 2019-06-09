package com.muflone.android.django_hotels.commands;

import android.content.Context;
import android.widget.Toast;

import com.muflone.android.django_hotels.database.models.Command;

import org.json.JSONException;

public class CommandToast extends CommandBase {
    /**
     * This Command shows a Toast notification
     *
     * The command must have the following arguments:
     * @message: the message to show
     * @duration: the duration to use (0 for LENGTH_SHORT, 1 for LENGTH_LONG)
     */
    public CommandToast(Context context, Command command) {
        super(context, command);
    }

    @Override
    public void execute() {
        super.execute();
        try {
            Toast.makeText(this.context,
                    this.command.type.command.getString("message"),
                    this.command.type.command.getInt("duration")
            ).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
