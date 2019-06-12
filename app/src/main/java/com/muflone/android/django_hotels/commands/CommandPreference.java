package com.muflone.android.django_hotels.commands;

import android.app.Activity;
import android.content.Context;

import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.database.models.Command;

import org.json.JSONException;

public class CommandPreference extends CommandBase {
    /**
     * This Command sets a Preference
     *
     * The command must have the following arguments:
     * type: must be one of the following:
     *   s for string values
     *   i for integer values
     *   l for long values
     *   b for boolean values
     *   f for float values
     *   u to unset the value
     * key: the name of the preference
     * value: the value to assign (ignored for removal)
     */

    private static final Singleton singleton = Singleton.getInstance();

    public CommandPreference(Activity activity, Context context, Command command) {
        super(activity, context, command);
    }

    @Override
    public void execute() {
        super.execute();
        try {
            String preferenceKey = this.command.command.getString("key");
            switch (this.command.command.getString("type")) {
                case "s":
                    this.singleton.settings.setValue(preferenceKey,
                            this.command.command.getString("value"));
                    break;
                case "i":
                    this.singleton.settings.setValue(preferenceKey,
                            this.command.command.getInt("value"));
                    break;
                case "l":
                    this.singleton.settings.setValue(preferenceKey,
                            this.command.command.getLong("value"));
                    break;
                case "b":
                    this.singleton.settings.setValue(preferenceKey,
                            this.command.command.getBoolean("value"));
                    break;
                case "f":
                    this.singleton.settings.setValue(preferenceKey,
                            (float) this.command.command.getDouble("value"));
                    break;
                case "u":
                    this.singleton.settings.unset(preferenceKey);
                    break;
            }

        } catch (JSONException exception) {
            exception.printStackTrace();
        }
    }
}
