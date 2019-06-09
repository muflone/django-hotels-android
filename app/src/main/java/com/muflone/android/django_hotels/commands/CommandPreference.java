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
    public CommandPreference(Activity activity, Context context, Context applicationContext, Command command) {
        super(activity, context, applicationContext, command);
    }

    @Override
    public void execute() {
        super.execute();
        try {
            String preferenceKey = this.command.type.command.getString("key");
            switch (this.command.type.command.getString("type")) {
                case "s":
                    Singleton.getInstance().settings.setValue(preferenceKey,
                            this.command.type.command.getString("value"));
                    break;
                case "i":
                    Singleton.getInstance().settings.setValue(preferenceKey,
                            this.command.type.command.getInt("value"));
                    break;
                case "l":
                    Singleton.getInstance().settings.setValue(preferenceKey,
                            this.command.type.command.getLong("value"));
                    break;
                case "b":
                    Singleton.getInstance().settings.setValue(preferenceKey,
                            this.command.type.command.getBoolean("value"));
                    break;
                case "f":
                    Singleton.getInstance().settings.setValue(preferenceKey,
                            (float) this.command.type.command.getDouble("value"));
                    break;
                case "u":
                    Singleton.getInstance().settings.unset(preferenceKey);
                    break;
            }

        } catch (JSONException exception) {
            exception.printStackTrace();
        }
    }
}
