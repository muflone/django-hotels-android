package com.muflone.android.django_hotels.commands;

import android.app.Activity;
import android.content.Context;

import com.muflone.android.django_hotels.Singleton;
import com.muflone.android.django_hotels.database.models.Command;
import com.muflone.android.django_hotels.database.models.CommandUsage;
import com.muflone.android.django_hotels.tasks.TaskCommandSetUsed;

import org.json.JSONException;

public class CommandSetUsed extends CommandBase {
    /**
     * This Command changes a Command usage count
     *
     * The command must have the following arguments:
     * id: the ID of the command to set
     * used: the number of uses to set
     */
    private final Singleton singleton = Singleton.getInstance();

    public CommandSetUsed(Activity activity, Context context, Command command) {
        super(activity, context, command);
    }

    @Override
    public void execute() {
        super.execute();
        try {
            CommandUsage[] commandUsages = this.singleton.apiData.commandsUsageMap.values().toArray(new CommandUsage[0]);
            int commandId = command.command.getInt("id");
            int commandUsed = command.command.getInt("used");
            // Find CommandUsages to update
            if (commandId == CommandConstants.SET_USED_SET_ALL_COMMAND_USAGES) {
                // Update every CommandUsages
                new TaskCommandSetUsed(commandUsed).execute(commandUsages);
            } else {
                // Find the CommandUsage to update
                for (CommandUsage commandUsage : commandUsages) {
                    if (commandUsage.id == commandId) {
                        new TaskCommandSetUsed(commandUsed).execute(commandUsage);
                        break;
                    }
                }
            }
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
    }
}
